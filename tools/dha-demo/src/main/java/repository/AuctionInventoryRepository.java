package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple repository for reading data from the AuctionInventory table.
 * Used by the "Assign Auction Item" workflow.
 */
public class AuctionInventoryRepository {

    private final Connection connection;

    public AuctionInventoryRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<AuctionInventoryItem> findAll() {
        String sql = "SELECT ID, ItemName, Quantity FROM AuctionInventory ORDER BY ItemName";
        List<AuctionInventoryItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new AuctionInventoryItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load AuctionInventory items", e);
        }
        return result;
    }

    public AuctionInventoryItem findById(int id) {
        String sql = "SELECT ID, ItemName, Quantity FROM AuctionInventory WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new AuctionInventoryItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load AuctionInventory item", e);
        }
        return null;
    }

    // New: find ID by ItemName
    public Integer findIdByName(String itemName) {
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

    public static class AuctionInventoryItem {
        private final int id;
        private final String itemName;
        private final int quantity;

        public AuctionInventoryItem(int id, String itemName, int quantity) {
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
            // This is used by the ComboBox to render a user friendly label
            return itemName;
        }
    }

    public int getTotalQuantity() {
        String sql = "SELECT SUM(Quantity) as TotalQty FROM AuctionInventory";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("TotalQty");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get total quantity from AuctionInventory", e);
        }
        return 0;
    }

    public int getTotalQuantityForItem(int id) {
        String sql = "SELECT Quantity FROM AuctionInventory WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Quantity");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get total quantity for item", e);
        }
        return 0;
    }

    public java.util.List<dto.auction.TotalAuctionInventoryDTO> getAuctionInventoryOverview() {
        // Total Auction Tab should show:
        // ItemName and Received Quantity from AuctionInventory (ItemName, Quantity)
        // Issued Quantity from AuctionIssued (Quantity) with the corresponding item name
        // Remaining quantity from AuctionRemaining (Quantity) with the corresponding item name

        String sql = "SELECT " +
                "a.ItemName, " +
                "a.Quantity as ReceivedQty, " +
                "COALESCE((SELECT SUM(ai.Quantity) FROM AuctionIssued ai WHERE ai.AuctionID = a.ID), 0) as IssuedQty, " +
                "COALESCE((SELECT ar.Quantity FROM AuctionRemaining ar WHERE ar.ItemName = a.ItemName), 0) as RemainingQty " +
                "FROM AuctionInventory a " +
                "ORDER BY a.ItemName";

        java.util.List<dto.auction.TotalAuctionInventoryDTO> result = new java.util.ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("ItemName");
                int rec = rs.getInt("ReceivedQty");
                int iss = rs.getInt("IssuedQty");
                int rem = rs.getInt("RemainingQty");
                result.add(new dto.auction.TotalAuctionInventoryDTO(name, rec, iss, rem));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load Auction Inventory Overview", e);
        }
        return result;
    }
}

