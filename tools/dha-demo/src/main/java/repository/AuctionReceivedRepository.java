package repository;

import database.ConnectionFactory;
import dto.auction.ReceivedAuctionInventoryDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuctionReceivedRepository {

    private final Connection connection;

    public AuctionReceivedRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<ReceivedAuctionInventoryDTO> getAllReceivedItems() {
        // User said: "get ItemName as Item Name, Quantity as Quantity, LetterNo as Letter No., ReceiveDate as Date and Remarks as Remarks from AuctionReceived table"
        // And we map it to DTO.
        // AuctionReceived has ItemName column directly.
        String sql = "SELECT ID, ItemName, Quantity, LetterNo, ReceiveDate, Remarks FROM AuctionReceived ORDER BY ReceiveDate DESC";
        List<ReceivedAuctionInventoryDTO> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new ReceivedAuctionInventoryDTO(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("LetterNo"),
                        rs.getDate("ReceiveDate"),
                        rs.getString("Remarks"),
                        0 // AuctionInventoryId unknown here, but not critical for display
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load AuctionReceived items", e);
        }
        return result;
    }

    public int updateAuctionReceived(int id, int quantity, String letterNo, java.sql.Date date, String remarks) {
        // Update Quantity, Letter No., Date, Remarks
        String sql = "UPDATE AuctionReceived SET Quantity = ?, LetterNo = ?, ReceiveDate = ?, Remarks = ? WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setString(2, letterNo);
            ps.setDate(3, date);
            ps.setString(4, remarks);
            ps.setInt(5, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update AuctionReceived", e);
        }
    }

    public int deleteAuctionReceived(int id) {
        // "delete that data from AuctionReceived, AuctionIssued, AuctionInventory"
        // Interpreted as: Delete receipt, and update Inventory stock.
        // Assuming AuctionInventory.Quantity tracks stock.
        Connection conn = this.connection;
        try {
            conn.setAutoCommit(false);

            // 1. Get ItemName and Quantity
            String itemName = null;
            int quantity = 0;
            String sqlGet = "SELECT ItemName, Quantity FROM AuctionReceived WHERE ID = ?";
            try (PreparedStatement ps = connection.prepareStatement(sqlGet)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        itemName = rs.getString("ItemName");
                        quantity = rs.getInt("Quantity");
                    } else {
                        conn.rollback(); conn.setAutoCommit(true);
                        return 0;
                    }
                }
            }

            // 2. Adjust AuctionInventory Quantity (decrease stock)
            // Need to find AuctionInventory by ItemName
            if (itemName != null) {
                String sqlUp = "UPDATE AuctionInventory SET Quantity = Quantity - ? WHERE ItemName = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUp)) {
                    ps.setInt(1, quantity);
                    ps.setString(2, itemName);
                    ps.executeUpdate();
                }
            }
            
            // 3. Delete from AuctionReceived
            String sqlDel = "DELETE FROM AuctionReceived WHERE ID = ?";
            int count;
            try (PreparedStatement ps = connection.prepareStatement(sqlDel)) {
                ps.setInt(1, id);
                count = ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return count;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            throw new RuntimeException("Failed to delete AuctionReceived", e);
        }
    }
}
