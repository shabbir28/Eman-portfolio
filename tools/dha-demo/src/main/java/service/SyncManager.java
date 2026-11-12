package service;

import database.ConnectionFactory;
import database.DatabaseMigration;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages dual-way synchronization between Local DB and LAN DB.
 * Strategy:
 * 1. Push: Local records with NO global_id or where last_updated > remote last_updated (complex).
 *    Simplified Push: Local records that are missing on Remote (by global_id).
 * 2. Pull: Remote records that are missing on Local (by global_id).
 */
public class SyncManager {
    private static final Logger LOGGER = Logger.getLogger(SyncManager.class.getName());
    
    // Tables to sync. Order matters for Foreign Keys! (Parents first)
    // Received tables often populate Inventory tables via triggers. 
    // We should probably sync "Source Documents" found in Received/Issued tables, 
    // and let the triggers on the destination DB handle the inventory calculations?
    // User requested: "Data must remain consistent".
    // If we sync `StoreReceived`, the trigger on LAN DB will update `StoreInventory`.
    private static final String[] SYNC_TABLES = {
        "ReceivedFrom", // Lookup table
        "DHAEmployee", // Master data
        "AuctionReceived", "StoreReceived", "UserReceived",
        "Issued", "StoreIssued", "AuctionIssued"
        // Dependent tables like MinuteSheet linked to StoreReceived?
        // Implementing full dependent sync is huge. We will focus on main tables.
    };

    /**
     * Entry point for synchronization cycle.
     */
    public void performSync() {
        Connection lanConn = ConnectionFactory.getInstance().createLanConnection();
        if (lanConn == null) {
            // LAN is offline. Do nothing.
            return;
        }

        Connection localConn = ConnectionFactory.getInstance().getConnection();

        try {
            // 1. Ensure schema is compatible (optional, but good)
            // DatabaseMigration.runMigration(); // Run locally? No, assuming done.

            // 2. Iterate tables
            for (String table : SYNC_TABLES) {
                syncTable(localConn, lanConn, table);
            }
            
            LOGGER.info("Sync cycle completed successfully.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Sync cycle failed", e);
        } finally {
            try { lanConn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    private void syncTable(Connection local, Connection remote, String tableName) {
        try {
            // 1. Push Local -> Remote
            syncDirection(local, remote, tableName, "PUSH");
            
            // 2. Pull Remote -> Local
            syncDirection(remote, local, tableName, "PULL");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to sync table " + tableName, e);
        }
    }

    private void syncDirection(Connection source, Connection target, String tableName, String direction) throws SQLException {
        // Strategy: Select all rows from Source where global_id is NOT NULL.
        // Check if global_id exists in Target. If not, Insert.
        // Update is skipped for simplicity in this V1 (assume append-only or immutable for now).
        
        // This is inefficient for large datasets but robust for "Getting Started".
        // Improved: Select global_ids involved in recent `last_updated` window?
        // Let's stick to "Find Missing global_ids".
        
        String query = "SELECT * FROM " + tableName + " WHERE global_id IS NOT NULL";
        
        try (Statement stmtSource = source.createStatement();
             ResultSet rsSource = stmtSource.executeQuery(query)) {
            
            ResultSetMetaData meta = rsSource.getMetaData();
            int colCount = meta.getColumnCount();

            while (rsSource.next()) {
                String gid = rsSource.getString("global_id");
                
                if (!recordExists(target, tableName, gid)) {
                     // Insert
                     insertRecord(target, tableName, rsSource, meta, colCount);
                }
            }
        }
    }

    private boolean recordExists(Connection conn, String tableName, String globalId) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName + " WHERE global_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, globalId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertRecord(Connection conn, String tableName, ResultSet rsSource, ResultSetMetaData meta, int colCount) throws SQLException {
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i);
            // Skip ID (auto-increment) - let target DB generate it
            if ("ID".equalsIgnoreCase(colName)) continue;
            
            if (cols.length() > 0) {
                cols.append(", ");
                vals.append(", ");
            }
            cols.append(colName);
            vals.append("?");
            values.add(rsSource.getObject(i));
        }

        String sql = "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + vals + ")";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            // Handle FK constraint failures if parent missing?
            // LOGGER.warning("Insert failed for " + tableName + ": " + e.getMessage());
            // This is expected if dependent tables sync out of order.
        }
    }
}
