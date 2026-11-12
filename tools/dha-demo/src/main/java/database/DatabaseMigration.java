package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseMigration {

    // List of tables that need sync capability
    private static final String[] TABLE_NAMES = {
        "AuctionReceived", "StoreReceived", "UserReceived",
        "StoreIssued", "Issued", "AuctionIssued",
        "AuctionInventory", "StoreInventory"
    };

    public static void runMigration() {
        Connection conn = ConnectionFactory.getInstance().getConnection();
        System.out.println("[INFO] Running Database Migration for Sync Support...");
        for (String table : TABLE_NAMES) {
            addSyncColumns(conn, table);
        }
    }

    private static void addSyncColumns(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            // Check if columns exist to avoid errors (simplified check by trying to add and ignoring error?)
            // Better: Check information_schema or just use IF NOT EXISTS if MySQL version supports it (8.0+ yes)
            // assuming MySQL 8.0 or reasonably modern MariaDB.
            
            // However, older MySQL doesn't support ADD COLUMN IF NOT EXISTS.
            // Safe way: try-catch.
            
            try {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN global_id VARCHAR(36)");
                System.out.println("[INFO] Added global_id to " + tableName);
                // Backfill UUIDs for existing rows?
                stmt.execute("UPDATE " + tableName + " SET global_id = UUID() WHERE global_id IS NULL");
            } catch (SQLException e) {
                // Ignore if duplicate column
                if (!e.getMessage().toLowerCase().contains("duplicate")) {
                    // System.err.println("Error adding global_id to " + tableName + ": " + e.getMessage());
                }
            }

            try {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
                System.out.println("[INFO] Added last_updated to " + tableName);
            } catch (SQLException e) {
                 if (!e.getMessage().toLowerCase().contains("duplicate")) {
                     // System.err.println("Error adding last_updated to " + tableName + ": " + e.getMessage());
                }
            }
            
            // Add Trigger for Auto-UUID
            addTrigger(conn, tableName);

        } catch (SQLException e) {
            // Table might not exist, just log warning
            // System.err.println("[WARN] Could not migrate table " + tableName + ": " + e.getMessage());
        }
    }

    private static void addTrigger(Connection conn, String tableName) {
        String triggerName = "trg_uuid_" + tableName;
        String sql = "CREATE TRIGGER " + triggerName + " " +
                     "BEFORE INSERT ON " + tableName + " " +
                     "FOR EACH ROW " +
                     "BEGIN " +
                     "  IF NEW.global_id IS NULL THEN " +
                     "    SET NEW.global_id = UUID(); " +
                     "  END IF; " +
                     "END";
        
        try (Statement stmt = conn.createStatement()) {
            // Drop valid if exists (simplest way to ensure update)
            try { stmt.execute("DROP TRIGGER IF EXISTS " + triggerName); } catch (SQLException ignored) {}
            
            stmt.execute(sql);
            System.out.println("[INFO] Added trigger " + triggerName);
        } catch (SQLException e) {
            System.err.println("[WARN] Failed to create trigger " + triggerName + ": " + e.getMessage());
        }
    }
}
