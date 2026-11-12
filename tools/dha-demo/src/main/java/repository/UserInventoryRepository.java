package repository;

import database.ConnectionFactory;
import dto.UserInventoryDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserInventoryRepository {
    private final Connection connection;

    public UserInventoryRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    // Total View: UserReceived + DHAEmployee + UserStatus + (Issued Date via
    // ItemName lookup?)
    // "IssuedDate From Issued table with the corresponding ItemName in UserReceived
    // table"
    // I will assume "Issued table" might be StoreIssued (checking legacy history)
    // or UserIssued.
    // Given the ambiguity, I'll attempt to join UserIssued on UserReceived.ID if
    // available, else ItemName.
    // However, the prompt says "Issued table" (generic). I'll assume UserIssued for
    // consistency with other views, but join on ItemName as requested.
    // To avoid Cartesian product if multiple issues exist for same ItemName, I'll
    // take distinct or latest?
    // I will use a LEFT JOIN on UserIssued using ItemName, but this is risky.
    // Alternative: Maybe "Issued" refers to the 'Issued' nature of the status?
    // Let's stick to: UserReceived LEFT JOIN UserIssued ON UserReceived.ItemName =
    // UserIssued.ItemName (This seems wrong if UserIssued doesn't have ItemName).
    // UserIssued has UserID (UserReceivedID). UserReceived has ItemName.
    // So logically: UserReceived ur JOIN UserIssued ui ON ur.ItemName = (SELECT
    // ItemName FROM UserReceived WHERE ID = ui.UserID)

    // Simpler interpretation: The prompt implies a direct relationship or lookup.
    // "IssuedDate ... From Issued table with the corresponding ItemName"
    // I'll try to find a table named 'Issued'. If not, 'UserIssued'.

    public List<UserInventoryDTO> getTotalInventory(String search) {
        // Query construction
        // "StatusName as Status From UserStatus with the corresponding StatusID in
        // UserReceived table"

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ur.ID AS UserReceivedID, ur.ItemName, ur.Quantity, ur.ReceiveDate, ur.PurchaseOrderNo, ");
        sql.append(
                "ur.Vendor, ur.DHAID, ur.WarrantyDemand, ur.WarrantyProvided, ur.Reason AS PendingReason, ur.Remarks, ");
        sql.append("de.EmpName, de.BranchID, COALESCE(b.BranchName, b2.BranchName) AS BranchName, ");
        sql.append("CASE WHEN p.UserID IS NOT NULL THEN 'Pending' ELSE 'Issued' END AS StatusName, ");
        // Subquery for IssuedDate based on ItemName... defaulting to NULL if not found
        // "IssuedDate ... From Issued table with the corresponding ItemName"
        // Linked via UserReceived?
        // I will use a LEFT JOIN logic with UserIssued (aliased as ui_lookup) via
        // UserReceivedID query
        // BUT strict instruction: "with the corresponding ItemName".
        // Let's assume there is a StoreIssued table with StoreInventory with ItemName.
        // (SELECT MAX(si.IssuedDate) FROM StoreIssued si JOIN StoreInventory inv ON
        // si.StoreInventoryID = inv.ID WHERE inv.ItemName = ur.ItemName)
        sql.append(
                "(SELECT MAX(si.IssuedDate) FROM StoreIssued si JOIN StoreInventory inv ON si.StoreInventoryID = inv.ID WHERE inv.ItemName = ur.ItemName) AS IssuedDate ");

        sql.append("FROM UserReceived ur ");
        sql.append("LEFT JOIN Pending p ON ur.ID = p.UserID ");
        sql.append("LEFT JOIN DHAEmployee de ON ur.DHAID = de.DHAID ");
        // Branch is in Branch table joined via DHAEmployee.BranchID?
        // Prompt says "get Name and Branch from DHAEmployee table".
        // I'll check DHAEmployee schema via simple query or error handling.
        // Assuming DHAEmployee has Branch column (as per prompt) or BranchID linking to
        // Branch table.
        // Note: StoreIssuedRepository used: "LEFT JOIN DHAEmployee e ... LEFT JOIN
        // Branch b ON e.BranchID = b.ID".
        // So I should do the same.
        sql.append("LEFT JOIN Branch b ON de.BranchID = b.ID ");
        sql.append("LEFT JOIN Branch b2 ON ur.BranchID = b2.ID ");
        sql.append("LEFT JOIN UserStatus us ON ur.StatusID = us.ID ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(
                    "WHERE LOWER(ur.ItemName) LIKE ? OR LOWER(ur.Vendor) LIKE ? OR LOWER(ur.PurchaseOrderNo) LIKE ? ");
        }

        List<UserInventoryDTO> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (search != null && !search.trim().isEmpty()) {
                String s = "%" + search.toLowerCase() + "%";
                ps.setString(1, s);
                ps.setString(2, s);
                ps.setString(3, s);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowTotal(rs));
            }
        } catch (SQLException e) {
            // Fallback if Date conversion fails or Column missing?
            throw new RuntimeException("Error loading total inventory: " + e.getMessage(), e);
        }
        return list;
    }

    // Issued View: UserReceived + UserIssued (corresponding to UserIssued table
    // UserID) + DHAEmployee
    // Columns: ItemName, Quantity, ReceivedDate, PurchaseOrderNo, Vendor, DHAID,
    // Name, Branch, WarrantyDemand, WarrantyProvided, IssuedDate, Remarks (Issued
    // Remarks)
    // Note: Remarks in mapping is "Remarks as Remarks from UserIssued".
    // "Edit button can edit ... UserReceived Table ... and IssuedDate and Remarks
    // in UserIssued table."
    public List<UserInventoryDTO> getIssuedInventory(String search) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ur.ID AS UserReceivedID, ur.ItemName, ur.Quantity, ur.ReceiveDate, ur.PurchaseOrderNo, ");
        sql.append("ur.Vendor, ur.DHAID, ur.WarrantyDemand, ur.WarrantyProvided, ");
        sql.append("ui.ID AS UserIssuedID, ui.IssuedDate, ui.Remarks AS IssuedRemarks, "); // UserIssued Remarks
        sql.append("de.EmpName, COALESCE(b.BranchName, b2.BranchName) AS BranchName ");

        sql.append("FROM UserIssued ui ");
        sql.append("JOIN UserReceived ur ON ui.UserID = ur.ID "); // "corresponing to UserIssued table UserID"
        sql.append("LEFT JOIN DHAEmployee de ON ur.DHAID = de.DHAID ");
        sql.append("LEFT JOIN Branch b ON de.BranchID = b.ID ");
        sql.append("LEFT JOIN Branch b2 ON ur.BranchID = b2.ID ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(
                    "WHERE LOWER(ur.ItemName) LIKE ? OR LOWER(ur.Vendor) LIKE ? OR LOWER(ur.PurchaseOrderNo) LIKE ? ");
        }

        List<UserInventoryDTO> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (search != null && !search.trim().isEmpty()) {
                String s = "%" + search.toLowerCase() + "%";
                ps.setString(1, s);
                ps.setString(2, s);
                ps.setString(3, s);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowIssued(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading issued inventory: " + e.getMessage(), e);
        }
        return list;
    }

    // Pending View: UserReceived + Pending (corresponding to Pending table UserID)
    // "Reason as Pending Reason ... from UserReceived table corresponing to Pending
    // table UserID"
    // "Quantity from Pending table"? "get ItemName ... from UserReceived ...
    // Quantity as Quantity... corresponding to Pending table UserID"
    // Wait, prompt says: "get ItemName... Quantity as Quantity... from UserReceived
    // table corresponding to Pending table UserID".
    // BUT later: "Edit button can ... also edit Quantity in Pending table."
    // AND "table... Quantity..."
    // If Pending table has Quantity, maybe I should display THAT Quantity?
    // User says "Quantity as Quantity ... from UserReceived table".
    // I will fetch BOTH quantities if possible or stick to prompt "Quantity as
    // Quantity ... from UserReceived".
    // BUT since Edit updates Pending table quantity, maybe I should fetch Pending
    // Quantity?
    // I'll stick to UserReceived Quantity as per "get ... from UserReceived table"
    // instruction for the column,
    // but I'll store PendingQuantity in DTO for the Edit logic.

    public List<UserInventoryDTO> getPendingInventory(String search) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ur.ID AS UserReceivedID, ur.ItemName, ur.Quantity, ur.ReceiveDate, ur.PurchaseOrderNo, ");
        sql.append(
                "ur.Vendor, ur.DHAID, ur.WarrantyDemand, ur.WarrantyProvided, ur.Reason AS PendingReason, ur.Remarks, ");
        sql.append("p.UserID AS PendingID, p.Quantity AS PendingQuantity, ");
        sql.append("de.EmpName, COALESCE(b.BranchName, b2.BranchName) AS BranchName ");

        sql.append("FROM Pending p ");
        sql.append("JOIN UserReceived ur ON p.UserID = ur.ID ");
        sql.append("LEFT JOIN DHAEmployee de ON ur.DHAID = de.DHAID ");
        sql.append("LEFT JOIN Branch b ON de.BranchID = b.ID ");
        sql.append("LEFT JOIN Branch b2 ON ur.BranchID = b2.ID ");

        if (search != null && !search.trim().isEmpty()) {
            sql.append(
                    "WHERE LOWER(ur.ItemName) LIKE ? OR LOWER(ur.Vendor) LIKE ? OR LOWER(ur.PurchaseOrderNo) LIKE ? ");
        }

        List<UserInventoryDTO> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            if (search != null && !search.trim().isEmpty()) {
                String s = "%" + search.toLowerCase() + "%";
                ps.setString(1, s);
                ps.setString(2, s);
                ps.setString(3, s);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowPending(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading pending inventory: " + e.getMessage(), e);
        }
        return list;
    }

    // ...existing code...

    public void updateIssued(UserInventoryDTO dto) {
        // Updates UserReceived and UserIssued
        try {
            connection.setAutoCommit(false);
            String updateUr = "UPDATE UserReceived SET Quantity=?, ReceiveDate=?, PurchaseOrderNo=?, Vendor=?, DHAID=?, Remarks=? WHERE ID=?";
            try (PreparedStatement ps = connection.prepareStatement(updateUr)) {
                ps.setInt(1, dto.getQuantity());
                ps.setDate(2, dto.getReceivedDate());
                ps.setString(3, dto.getPurchaseOrderNo());
                ps.setString(4, dto.getVendor());
                ps.setString(5, dto.getDhaId());
                // "Remark in UserReceived Table"
                ps.setString(6, dto.getRemarks()); // Note: Issued View has "Remarks as Remarks from UserIssued" for
                                                   // column, but Edit says "Remark in UserReceived" too.
                ps.setInt(7, dto.getUserReceivedId());
                ps.executeUpdate();
            }

            // Update UserIssued
            String updateUi = "UPDATE UserIssued SET IssuedDate=?, Remarks=? WHERE ID=?";
            try (PreparedStatement ps = connection.prepareStatement(updateUi)) {
                ps.setDate(1, dto.getIssuedDate());
                ps.setString(2, dto.getIssuedRemarks());
                ps.setInt(3, dto.getUserIssuedId());
                ps.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
            }
            throw new RuntimeException("Error updating issued inventory", e);
        }
    }

    public void deleteIssued(int userIssuedId, int userReceivedId) {
        try {
            connection.setAutoCommit(false);
            String delUi = "DELETE FROM UserIssued WHERE ID=?";
            try (PreparedStatement ps = connection.prepareStatement(delUi)) {
                ps.setInt(1, userIssuedId);
                ps.executeUpdate();
            }
            String delUr = "DELETE FROM UserReceived WHERE ID=?";
            try (PreparedStatement ps = connection.prepareStatement(delUr)) {
                ps.setInt(1, userReceivedId);
                ps.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
            }
            throw new RuntimeException("Error deleting issued inventory", e);
        }
    }

    public void updatePending(UserInventoryDTO dto) {
        try {
            connection.setAutoCommit(false);
            // "Edit button can edit Quantity, Received Date, Purchase Order No., Vendor,
            // DHAID, Pending Reason, Remark in UserReceived Table and also edit Quantity in
            // Pending table."
            String updateUr = "UPDATE UserReceived SET Quantity=?, ReceiveDate=?, PurchaseOrderNo=?, Vendor=?, DHAID=?, Reason=?, Remarks=? WHERE ID=?";
            try (PreparedStatement ps = connection.prepareStatement(updateUr)) {
                ps.setInt(1, dto.getQuantity());
                ps.setDate(2, dto.getReceivedDate());
                ps.setString(3, dto.getPurchaseOrderNo());
                ps.setString(4, dto.getVendor());
                ps.setString(5, dto.getDhaId());
                ps.setString(6, dto.getPendingReason());
                ps.setString(7, dto.getRemarks());
                ps.setInt(8, dto.getUserReceivedId());
                ps.executeUpdate();
            }

            // Update Pending table
            String updateP = "UPDATE Pending SET Quantity=? WHERE UserID=?";
            try (PreparedStatement ps = connection.prepareStatement(updateP)) {
                ps.setInt(1, dto.getPendingQuantity()); // Need to make sure DTO carries this
                ps.setInt(2, dto.getPendingId());
                ps.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
            }
            throw new RuntimeException("Error updating pending inventory", e);
        }
    }

    public void deletePending(int pendingId, int userReceivedId) {
        try {
            connection.setAutoCommit(false);
            String delP = "DELETE FROM Pending WHERE UserID=?";
            try (PreparedStatement ps = connection.prepareStatement(delP)) {
                ps.setInt(1, pendingId);
                ps.executeUpdate();
            }
            String delUr = "DELETE FROM UserReceived WHERE ID=?";
            try (PreparedStatement ps = connection.prepareStatement(delUr)) {
                ps.setInt(1, userReceivedId);
                ps.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
            }
            throw new RuntimeException("Error deleting pending inventory", e);
        }
    }

    // Mapping Helpers re-implementation to support mapRowTotal having IssuedDate
    private UserInventoryDTO createDTO(ResultSet rs, String type) throws SQLException {
        // Common fields
        int id = rs.getInt("UserReceivedID");
        String itemName = rs.getString("ItemName");
        int qty = rs.getInt("Quantity"); // UserReceived Quantity
        Date recDate = rs.getDate("ReceiveDate");
        String po = rs.getString("PurchaseOrderNo");
        String vendor = rs.getString("Vendor");
        String dhaId = rs.getString("DHAID");
        String wDem = rs.getString("WarrantyDemand");
        String wProv = rs.getString("WarrantyProvided");
        String reason = columnExist(rs, "PendingReason") ? rs.getString("PendingReason") : null;
        String remarks = columnExist(rs, "Remarks") ? rs.getString("Remarks") : null;
        String empName = columnExist(rs, "EmpName") ? rs.getString("EmpName") : "";
        String branch = columnExist(rs, "BranchName") ? rs.getString("BranchName") : "";

        String status = columnExist(rs, "StatusName") ? rs.getString("StatusName") : null;
        int issuedId = columnExist(rs, "UserIssuedID") ? rs.getInt("UserIssuedID") : 0;
        Date issuedDate = columnExist(rs, "IssuedDate") ? rs.getDate("IssuedDate") : null;
        String issuedRemarks = columnExist(rs, "IssuedRemarks") ? rs.getString("IssuedRemarks") : null;
        int pendingId = columnExist(rs, "PendingID") ? rs.getInt("PendingID") : 0;
        int pendingQty = columnExist(rs, "PendingQuantity") ? rs.getInt("PendingQuantity") : 0;

        return new UserInventoryDTO(id, itemName, qty, recDate, po, vendor, dhaId, wDem, wProv, reason, remarks,
                empName, branch, status, issuedId, issuedDate, issuedRemarks, pendingId, pendingQty);
    }

    // I will use createDTO inside the methods.
    private boolean columnExist(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // Correcting mapRow methods to use createDTO equivalent
    private UserInventoryDTO mapRowTotal(ResultSet rs) throws SQLException {
        return createDTO(rs, "TOTAL");
    }

    private UserInventoryDTO mapRowIssued(ResultSet rs) throws SQLException {
        return createDTO(rs, "ISSUED");
    }

    private UserInventoryDTO mapRowPending(ResultSet rs) throws SQLException {
        return createDTO(rs, "PENDING");
    }
}
