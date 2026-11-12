package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository used by Assign* controllers to insert rows into:
 * - AuctionIssued
 * - UserIssued
 * - StoreIssued
 */
public class IssueRepository {

    private final Connection connection;

    public IssueRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public int addAuctionIssued(int auctionId, int quantity, Date issuedDate, String remarks) {
        String sql = "INSERT INTO AuctionIssued (AuctionID, Quantity, IssuedDate, Remarks) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, auctionId);
            ps.setInt(2, quantity);
            ps.setDate(3, issuedDate);
            ps.setString(4, remarks);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert AuctionIssued", e);
        }
    }

    public int addUserIssued(int userId, Date issuedDate, String remarks) {
        String sql = "INSERT INTO UserIssued (UserID, IssuedDate, Remarks) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDate(2, issuedDate);
            ps.setString(3, remarks);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert UserIssued", e);
        }
    }

    public int addStoreIssued(int storeInventoryId, int quantity, String dhaId,
            Date issuedDate, int issueTypeId, String registerNo, String remarks) {
        String insertSql = "INSERT INTO StoreIssued (StoreInventoryID, Quantity, DHAID, IssuedDate, IssueTypeID, RegisterNo, Remarks) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE StoreRemaining SET Quantity = Quantity - ? WHERE ItemName = (SELECT ItemName FROM StoreInventory WHERE ID = ?)";

        try {
            connection.setAutoCommit(false);

            // 1. Update Remaining Quantity
            try (PreparedStatement psUpdate = connection.prepareStatement(updateSql)) {
                psUpdate.setInt(1, quantity);
                psUpdate.setInt(2, storeInventoryId);
                int rowsUpdated = psUpdate.executeUpdate();
                // Optionally check if rowsUpdated > 0, but if item is not in StoreRemaining
                // yet, this might fail or do nothing.
                // Assuming StoreRemaining is always populated for valid items.
            }

            // 2. Insert Issue Record
            int generatedId = -1;
            try (PreparedStatement psInsert = connection.prepareStatement(insertSql,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                psInsert.setInt(1, storeInventoryId);
                psInsert.setInt(2, quantity);
                psInsert.setString(3, dhaId);
                psInsert.setDate(4, issuedDate);
                psInsert.setInt(5, issueTypeId);
                psInsert.setString(6, registerNo);
                psInsert.setString(7, remarks);
                psInsert.executeUpdate();
                try (ResultSet keys = psInsert.getGeneratedKeys()) {
                    if (keys.next())
                        generatedId = keys.getInt(1);
                }
            }

            if (generatedId > 0) {
                connection.commit();
                return generatedId;
            } else {
                connection.rollback();
                return -1;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Failed to insert StoreIssued transaction", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // New: delete UserIssued by ID (used for rollback if subsequent operations
    // fail)
    public int deleteUserIssuedById(int id) {
        String sql = "DELETE FROM UserIssued WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete UserIssued", e);
        }
    }
}
