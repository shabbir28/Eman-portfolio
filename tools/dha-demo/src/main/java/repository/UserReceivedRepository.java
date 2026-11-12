package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for reading from the UserReceived table, used in the
 * "Assign User Item" workflow.
 */
public class UserReceivedRepository {

    private final Connection connection;

    public UserReceivedRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<UserItem> findAll() {
        String sql = "SELECT ID, ItemName, Quantity, DHAID, BranchID, PurchaseOrderNo FROM UserReceived ORDER BY ItemName";
        List<UserItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("DHAID"),
                        (Integer) rs.getObject("BranchID"),
                        rs.getString("PurchaseOrderNo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load UserReceived items", e);
        }
        return result;
    }

    // New: fetch only pending items (StatusID = 2)
    public List<UserItem> findPending() {
        String sql = "SELECT ID, ItemName, Quantity, DHAID, BranchID, PurchaseOrderNo FROM UserReceived WHERE StatusID = 2 ORDER BY ItemName";
        List<UserItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("DHAID"),
                        (Integer) rs.getObject("BranchID"),
                        rs.getString("PurchaseOrderNo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load pending UserReceived items", e);
        }
        return result;
    }

    public List<String> findDistinctPendingPOs() {
        String sql = "SELECT DISTINCT PurchaseOrderNo FROM UserReceived WHERE StatusID = 2 AND PurchaseOrderNo IS NOT NULL ORDER BY PurchaseOrderNo";
        List<String> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String po = rs.getString("PurchaseOrderNo");
                if (!po.trim().isEmpty()) {
                    result.add(po);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load pending POs", e);
        }
        return result;
    }

    public List<UserItem> findPendingByPO(String poNo) {
        String sql = "SELECT ID, ItemName, Quantity, DHAID, BranchID, PurchaseOrderNo FROM UserReceived WHERE StatusID = 2 AND PurchaseOrderNo = ? ORDER BY ItemName";
        List<UserItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, poNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new UserItem(
                        rs.getInt("ID"),
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("DHAID"),
                        (Integer) rs.getObject("BranchID"),
                        rs.getString("PurchaseOrderNo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load pending items for PO: " + poNo, e);
        }
        return result;
    }

    public static class UserItem {
        private final int id;
        private final String itemName;
        private final int quantity;
        private final String dhaId;
        private final Integer branchId;
        private final String purchaseOrderNo;

        public UserItem(int id, String itemName, int quantity, String dhaId, Integer branchId, String purchaseOrderNo) {
            this.id = id;
            this.itemName = itemName;
            this.quantity = quantity;
            this.dhaId = dhaId;
            this.branchId = branchId;
            this.purchaseOrderNo = purchaseOrderNo;
        }

        public int getId() { return id; }
        public String getItemName() { return itemName; }
        public int getQuantity() { return quantity; }
        public String getDhaId() { return dhaId; }
        public Integer getBranchId() { return branchId; }
        public String getPurchaseOrderNo() { return purchaseOrderNo; }
    }
}
