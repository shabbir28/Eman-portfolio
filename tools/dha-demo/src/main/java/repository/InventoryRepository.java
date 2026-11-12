package repository;

import database.ConnectionFactory;
import model.AuctionItem;
import model.InventoryStats;
import model.LowStockItem;
import model.PendingItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rewritten InventoryRepository to match the current database schema.
 * Key changes compared to the previous version:
 *  - StoreIssued no longer has StoreRID; it uses StoreInventoryID which references StoreInventory(ID).
 *  - InStore may be absent or empty; this repository prefers StoreInventory as source-of-truth.
 *  - Auction queries use AuctionInventory (accumulated quantities) rather than AuctionReceived.
 *  - Low stock logic compares StoreInventory.Quantity to LowStock.LowStockLimit (when LowStock exists)
 *  - All joins use StoreInventory -> ItemName to relate issued rows back to StoreReceived totals.
 */
public class InventoryRepository {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;
    private final Connection connection;

    public InventoryRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    /**
     * Return items that are below their configured low stock threshold.
     * Primary source: StoreInventory join LowStock (if LowStock rows exist).
     * Fallback: compute from StoreReceived - StoreIssued grouped by item.
     */
    public List<LowStockItem> fetchLowStockItems(int limit) {
        List<LowStockItem> items = new ArrayList<>();
        String sql = "SELECT l.ItemName, r.Quantity FROM LowStock l JOIN StoreRemaining r ON r.ItemName=l.ItemName WHERE r.Quantity<l.LowStockLimit ORDER BY r.Quantity LIMIT ?";
        try (PreparedStatement p=connection.prepareStatement(sql)) {
            p.setInt(1,limit);try(ResultSet r=p.executeQuery()){while(r.next())items.add(new LowStockItem(r.getString(1),r.getInt(2)));}
            return items;
        } catch(SQLException e){throw new IllegalStateException("Could not load low stock",e);}
    }

    public List<PendingItem> fetchPendingUserItems() {
        // Return item name and pending quantity from Pending table (Pending.Quantity)
        // Return item name and quantity solely from Pending table
        String query = "SELECT ur.ItemName AS itemName, p.Quantity AS qty "
                + "FROM Pending p "
                + "JOIN UserReceived ur ON p.UserID = ur.ID "
                + "ORDER BY ur.ItemName";

        List<PendingItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // PendingItem has fields (itemName, branch) where branch was previously used for branch name.
                // For dashboard we will store the quantity in the 'branch' field as a string so the table shows ItemName and Quantity.
                items.add(new PendingItem(
                        rs.getString("itemName"),
                        String.valueOf(rs.getInt("qty"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load pending user items", e);
        }
        return items;
    }

    public InventoryStats fetchUserStats() {
        // Total Issued: sum quantities from UserReceived rows that have a corresponding UserIssued row
        int totalIssued = fetchSingleInt("SELECT IFNULL(SUM(ur.Quantity),0) FROM UserReceived ur JOIN UserIssued ui ON ui.UserID = ur.ID");
        // Total Pending: sum quantities from Pending table
        int totalPending = fetchSingleInt("SELECT IFNULL(SUM(Quantity),0) FROM Pending");
        // Total User Items = Issued + Pending
        int totalUserItems = Math.max(0, totalIssued + totalPending);

        return new InventoryStats(totalUserItems, totalIssued, totalPending, 0);
    }

    /**
     * Fetch remaining auction items using AuctionRemaining as source-of-truth.
     * Only returns items with remaining > 0 and uses the ItemName from AuctionReceived
     * (to ensure the assigned item name matches received records) while total comes
     * from AuctionRemaining.Quantity.
     */
    public List<AuctionItem> fetchAuctionRemainingItems() {
        String query = "SELECT ItemName, Quantity FROM AuctionRemaining WHERE Quantity > 0 ORDER BY ItemName";

        List<AuctionItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new AuctionItem(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load auction items", e);
        }
        return items;
    }

    // New: fetch store remaining items
    public List<AuctionItem> fetchStoreRemainingItems() {
        String query = "SELECT ItemName AS itemName, IFNULL(Quantity,0) AS remaining FROM StoreRemaining ORDER BY itemName";
        List<AuctionItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new AuctionItem(
                        rs.getString("itemName"),
                        rs.getInt("remaining")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load store remaining items", e);
        }
        return items;
    }

    public InventoryStats fetchAuctionStats() {
        int totalInStore = fetchSingleInt("SELECT IFNULL(SUM(Quantity),0) FROM AuctionRemaining");
        int totalIssued = fetchSingleInt("SELECT IFNULL(SUM(Quantity),0) FROM AuctionIssued");
        int totalAuctionItems = Math.max(0, totalInStore + totalIssued);

        return new InventoryStats(totalAuctionItems, totalInStore, totalIssued, 0);
    }

    private int fetchSingleInt(String query) {
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: " + query, e);
        }
        return 0;
    }

    /**
     * Total in-store quantity: prefer StoreRemaining sum; fallback compute sum(received) - sum(issued)
     */
    public int fetchTotalInStoreQuantity() {
        int totalFromStoreRemaining = 0;
        try (PreparedStatement ps = connection.prepareStatement("SELECT IFNULL(SUM(Quantity),0) AS total FROM StoreRemaining")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) totalFromStoreRemaining = rs.getInt("total");
            System.out.println("[DEBUG] fetchTotalInStoreQuantity from StoreRemaining=" + totalFromStoreRemaining);
        } catch (SQLException e) {
            System.err.println("[WARN] fetchTotalInStoreQuantity StoreRemaining query failed: " + e.getMessage());
        }

        if (totalFromStoreRemaining > 0) return totalFromStoreRemaining;

        // fallback compute sum(received - issued) using StoreInventory mapping
        String query = "SELECT IFNULL(SUM(IFNULL(r.sum_received,0) - IFNULL(i.sum_issued,0)),0) AS totalAvailable "
                + "FROM (SELECT ItemName, SUM(Quantity) AS sum_received FROM StoreReceived GROUP BY ItemName) r "
                + "LEFT JOIN (SELECT si2.ItemName AS ItemName, SUM(sii.Quantity) AS sum_issued "
                + "           FROM StoreIssued sii JOIN StoreInventory si2 ON sii.StoreInventoryID = si2.ID "
                + "           GROUP BY si2.ItemName) i "
                + "  ON i.ItemName = r.ItemName";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("totalAvailable");
                System.out.println("[DEBUG] fetchTotalInStoreQuantity fallback totalAvailable=" + total);
                return total;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load in-store quantity", e);
        }
        return 0;
    }

    public int fetchStoreIssuedQuantityByStatus(String status) {
        String query = "SELECT IFNULL(SUM(si.Quantity - COALESCE((SELECT SUM(r.Quantity) FROM StoreReturn r WHERE r.StoreIssueID=si.ID),0)), 0) AS total "
                + "FROM StoreIssued si "
                + "JOIN IssueType it ON si.IssueTypeID = it.ID "
                + "WHERE LOWER(it.TypeName) = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status.toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load store issued quantity", e);
        }
        return 0;
    }

    public int fetchTotalStoreReceivedQuantity() {
        String query = "SELECT IFNULL(SUM(Quantity), 0) AS total FROM StoreReceived";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load total StoreReceived quantity", e);
        }
        return 0;
    }

    // Helper: find AuctionInventory ID by ItemName
    public Integer findAuctionIdByName(String itemName) {
        String sql = "SELECT ID FROM AuctionInventory WHERE ItemName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find AuctionInventory ID by name", e);
        }
        return null;
    }
}
