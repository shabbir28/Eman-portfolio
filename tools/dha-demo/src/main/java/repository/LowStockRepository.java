package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LowStockRepository {
    private final Connection connection;

    public LowStockRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public void upsertLowStock(String itemName, int limit) throws SQLException {
        String check = "SELECT ItemName FROM LowStock WHERE ItemName = ?";
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setString(1, itemName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String upd = "UPDATE LowStock SET LowStockLimit = ? WHERE ItemName = ?";
                try (PreparedStatement ps2 = connection.prepareStatement(upd)) {
                    ps2.setInt(1, limit);
                    ps2.setString(2, itemName);
                    ps2.executeUpdate();
                }
            } else {
                String ins = "INSERT INTO LowStock (ItemName, LowStockLimit) VALUES (?, ?)";
                try (PreparedStatement ps2 = connection.prepareStatement(ins)) {
                    ps2.setString(1, itemName);
                    ps2.setInt(2, limit);
                    ps2.executeUpdate();
                }
            }
        }
    }

    public List<LowStockItem> findAllWithNames() {
        String sql = "SELECT ItemName, LowStockLimit FROM LowStock";
        List<LowStockItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new LowStockItem(
                        rs.getString("ItemName"),
                        rs.getInt("LowStockLimit")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load low stock items", e);
        }
        return result;
    }

    public void deleteByItemName(String itemName) throws SQLException {
        String sql = "DELETE FROM LowStock WHERE ItemName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemName);
            ps.executeUpdate();
        }
    }

    public static class LowStockItem {
        private final String itemName;
        private final int limit;

        public LowStockItem(String itemName, int limit) {
            this.itemName = itemName;
            this.limit = limit;
        }

        public String getItemName() {
            return itemName;
        }

        public int getLimit() {
            return limit;
        }
    }
}
