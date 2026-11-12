package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserStatusRepository {
    private final Connection connection;

    public UserStatusRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<UserStatusItem> findAll() {
        String query = "SELECT ID, StatusName FROM UserStatus ORDER BY ID";
        List<UserStatusItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new UserStatusItem(rs.getInt("ID"), rs.getString("StatusName")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch UserStatus items", e);
        }
        return items;
    }

    public UserStatusItem findById(int id) {
        String query = "SELECT ID, StatusName FROM UserStatus WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UserStatusItem(rs.getInt("ID"), rs.getString("StatusName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch UserStatus item", e);
        }
        return null;
    }

    public static class UserStatusItem {
        private final int id;
        private final String statusName;

        public UserStatusItem(int id, String statusName) {
            this.id = id;
            this.statusName = statusName;
        }

        public int getId() {
            return id;
        }

        public String getStatusName() {
            return statusName;
        }

        @Override
        public String toString() {
            return statusName;
        }
    }
}

