package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for the IssueType lookup table.
 */
public class IssueTypeRepository {

    private final Connection connection;

    public IssueTypeRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<IssueTypeItem> findAll() {
        String sql = "SELECT ID, TypeName FROM IssueType ORDER BY TypeName";
        List<IssueTypeItem> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new IssueTypeItem(
                        rs.getInt("ID"),
                        rs.getString("TypeName")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load IssueType items", e);
        }
        return items;
    }

    public static class IssueTypeItem {
        private final int id;
        private final String typeName;

        public IssueTypeItem(int id, String typeName) {
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


