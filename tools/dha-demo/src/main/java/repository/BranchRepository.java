package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchRepository {
    private final Connection connection;

    public BranchRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<BranchItem> findAll() {
        String query = "SELECT ID, BranchName FROM Branch ORDER BY BranchName";
        List<BranchItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new BranchItem(rs.getInt("ID"), rs.getString("BranchName")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch Branch items", e);
        }
        return items;
    }

    public BranchItem findById(int id) {
        String query = "SELECT ID, BranchName FROM Branch WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new BranchItem(rs.getInt("ID"), rs.getString("BranchName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch Branch item", e);
        }
        return null;
    }

    public static class BranchItem {
        private final int id;
        private final String branchName;

        public BranchItem(int id, String branchName) {
            this.id = id;
            this.branchName = branchName;
        }

        public int getId() {
            return id;
        }

        public String getBranchName() {
            return branchName;
        }

        @Override
        public String toString() {
            return branchName;
        }
    }
}

