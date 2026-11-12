package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for reading from the StoreInventory table.
 * Used by the "Assign Store Item" workflow.
 */
public class StoreInventoryRepository {

    private final Connection connection;

    public StoreInventoryRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<StoreInventoryItem> findAll() {
        String sql = "SELECT ID, ItemName, Quantity FROM StoreInventory ORDER BY ItemName";
        List<StoreInventoryItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new StoreInventoryItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load StoreInventory items", e);
        }
        return result;
    }

    public StoreInventoryItem findById(int id) {
        String sql = "SELECT ID, ItemName, Quantity FROM StoreInventory WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new StoreInventoryItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load StoreInventory item", e);
        }
        return null;
    }

    // New: find ID by ItemName
    public Integer findIdByName(String itemName) {
        String sql = "SELECT ID FROM StoreInventory WHERE ItemName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find StoreInventory ID by name", e);
        }
        return null;
    }

    /**
     * Returns the total quantity present in RemainingStore for all StoreIssued rows
     * that reference the given StoreInventory ID. This is used to compute the
     * available quantity for assignment by subtracting returned/reserved quantities.
     */
    public int getRemainingQuantityForInventory(int storeInventoryId) {
        String sql = "SELECT IFNULL(SUM(r.Quantity), 0) AS totalRemaining " +
                     "FROM StoreRemaining r " +
                     "JOIN StoreIssued si ON r.ItemName = (SELECT ItemName FROM StoreInventory WHERE ID = si.StoreInventoryID) " +
                     "WHERE si.StoreInventoryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, storeInventoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("totalRemaining");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to compute remaining quantity for inventory", e);
        }
        return 0;
    }

    public static class StoreInventoryItem {
        private final int id;
        private final String itemName;
        private final int quantity;

        public StoreInventoryItem(int id, String itemName, int quantity) {
            this.id = id;
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return itemName;
        }
    }

    public List<dto.store.StoreInventoryDTO> getInventoryOverview() {
        String sql = "SELECT inv.ItemName, inv.Quantity AS TotalItem, " +
                     "IFNULL(sr.Quantity, 0) AS InStore, " +
                     "(inv.Quantity - IFNULL(sr.Quantity, 0)) AS Assigned " +
                     "FROM StoreInventory inv " +
                     "LEFT JOIN StoreRemaining sr ON sr.ItemName = inv.ItemName " +
                     "LEFT JOIN StoreIssued si ON inv.ID = si.StoreInventoryID " +
                     "GROUP BY inv.ID, inv.ItemName, inv.Quantity, sr.Quantity " +
                     "ORDER BY inv.ItemName";
        List<dto.store.StoreInventoryDTO> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new dto.store.StoreInventoryDTO(
                        rs.getString("ItemName"),
                        rs.getInt("TotalItem"),
                        rs.getInt("InStore"),
                        rs.getInt("Assigned")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load inventory overview", e);
        }
        return result;
    }
}
