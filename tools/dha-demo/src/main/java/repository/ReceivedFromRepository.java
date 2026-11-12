package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReceivedFromRepository {
    private final Connection connection;

    public ReceivedFromRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<ReceivedFromItem> findAll() {
        String query = "SELECT ID, TypeName FROM ReceivedFrom ORDER BY ID";
        List<ReceivedFromItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new ReceivedFromItem(rs.getInt("ID"), rs.getString("TypeName")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch ReceivedFrom items", e);
        }
        return items;
    }

    public ReceivedFromItem findById(int id) {
        String query = "SELECT ID, TypeName FROM ReceivedFrom WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ReceivedFromItem(rs.getInt("ID"), rs.getString("TypeName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch ReceivedFrom item", e);
        }
        return null;
    }

    public static class ReceivedFromItem {
        private final int id;
        private final String typeName;

        public ReceivedFromItem(int id, String typeName) {
            this.id = id;
            this.typeName = typeName;
        }

        public int getId() {
            return id;
        }

        public String getTypeName() {
            return typeName;
        }

        @Override
        public String toString() {
            return typeName;
        }
    }
}

