package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for reading from the StoreIssued table.
 * Used by the "Return Item" workflow.
 */
public class StoreIssuedRepository {

    private final Connection connection;

    public StoreIssuedRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    /**
     * Get distinct ItemNames from StoreIssued where IssueType is 'Temporary'
     */
    public List<String> getTemporaryItemNames() {
        String sql = "SELECT DISTINCT inv.ItemName " +
                "FROM StoreIssued si " +
                "JOIN StoreInventory inv ON si.StoreInventoryID = inv.ID " +
                "ORDER BY inv.ItemName";
        List<String> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("ItemName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load temporary item names", e);
        }
        return result;
    }

    /**
     * Get StoreIssued items by ItemName and IssueType='Temporary'
     * Returns list with ID, DHAID, Quantity, RegisterNo
     */
    public List<StoreIssuedItem> getTemporaryItemsByItemName(String itemName) {
        String sql = "SELECT si.ID, si.DHAID, si.Quantity, si.RegisterNo, de.EmpName, de.Designation " +
                "FROM StoreIssued si " +
                "JOIN StoreInventory inv ON si.StoreInventoryID = inv.ID " +
                "LEFT JOIN DHAEmployee de ON si.DHAID = de.DHAID " +
                "WHERE inv.ItemName = ? " +
                "ORDER BY si.ID";
        List<StoreIssuedItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new StoreIssuedItem(
                        rs.getInt("ID"),
                        rs.getString("DHAID"),
                        rs.getInt("Quantity"),
                        rs.getString("RegisterNo"),
                        rs.getString("EmpName"),
                        rs.getString("Designation")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load store issued items by item name", e);
        }
        return result;
    }

    /**
     * Get StoreIssued item by ID
     */
    public StoreIssuedItem getById(int id) {
        String sql = "SELECT si.ID, si.DHAID, si.Quantity, si.RegisterNo, de.EmpName, de.Designation " +
                "FROM StoreIssued si " +
                "LEFT JOIN DHAEmployee de ON si.DHAID = de.DHAID " +
                "WHERE si.ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new StoreIssuedItem(
                        rs.getInt("ID"),
                        rs.getString("DHAID"),
                        rs.getInt("Quantity"),
                        rs.getString("RegisterNo"),
                        rs.getString("EmpName"),
                        rs.getString("Designation")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load store issued item", e);
        }
        return null;
    }

    public static class StoreIssuedItem {
        private final int id;
        private final String dhaId;
        private final int quantity;
        private final String registerNo;
        private final String empName;
        private final String designation;

        public StoreIssuedItem(int id, String dhaId, int quantity, String registerNo, String empName, String designation) {
            this.id = id;
            this.dhaId = dhaId;
            this.quantity = quantity;
            this.registerNo = registerNo;
            this.empName = empName == null ? "" : empName;
            this.designation = designation == null ? "" : designation;
        }

        public int getId() {
            return id;
        }

        public String getDhaId() {
            return dhaId;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getRegisterNo() {
            return registerNo;
        }

        public String getEmpName() { return empName; }
        public String getDesignation() { return designation; }

        @Override
        public String toString() {
            return registerNo == null ? ("Issued#" + id) : registerNo;
        }
    }

    public java.util.List<dto.store.IssuedInventoryDTO> getIssuedItems(int issueTypeId) {
        // issueTypeId: 1 = Temporary, 2 = Permanent
        // Using distinct ReturnDate Logic: taking MAX(ReturnDate) if multiple exist for the issue ID
        // DHAEmployee now stores BranchID; join Branch to get BranchName
        String sql = "SELECT si.ID, si.DHAID, inv.ItemName, si.Quantity AS IssuedQty, " +
                     "e.EmpName, e.Designation, b.BranchName AS Branch, " +
                     "si.RegisterNo, si.IssuedDate, si.Remarks, " +
                     "(SELECT IFNULL(SUM(Quantity), 0) FROM StoreReturn WHERE StoreIssueID = si.ID) AS ReturnedQty, " +
                     // Compute RemainingQty as issued minus returned to avoid referencing a non-existent RemainingStore table
                     "(si.Quantity - (SELECT IFNULL(SUM(Quantity), 0) FROM StoreReturn WHERE StoreIssueID = si.ID)) AS RemainingQty, " +
                     "(SELECT MAX(ReturnDate) FROM StoreReturn WHERE StoreIssueID = si.ID) AS LastReturnDate " +
                     "FROM StoreIssued si " +
                     "JOIN StoreInventory inv ON si.StoreInventoryID = inv.ID " +
                     "LEFT JOIN DHAEmployee e ON si.DHAID = e.DHAID " +
                     "LEFT JOIN Branch b ON e.BranchID = b.ID " +
                     "WHERE si.IssueTypeID = ? " +
                     "ORDER BY si.IssuedDate DESC";

        java.util.List<dto.store.IssuedInventoryDTO> result = new java.util.ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
             ps.setInt(1, issueTypeId);
             ResultSet rs = ps.executeQuery();
             while (rs.next()) {
                 int issuedQty = rs.getInt("IssuedQty");
                 int returnedQty = rs.getInt("ReturnedQty");
                 int remainingQty = rs.getObject("RemainingQty") != null ? rs.getInt("RemainingQty") : (issuedQty - returnedQty);

                 result.add(new dto.store.IssuedInventoryDTO(
                     rs.getInt("ID"),
                     rs.getString("DHAID"),
                     rs.getString("ItemName"),
                     issuedQty,
                     returnedQty,
                     remainingQty,
                     rs.getString("EmpName"),
                     rs.getString("Designation"),
                     rs.getString("Branch"),
                     rs.getString("RegisterNo"),
                     rs.getDate("IssuedDate"),
                     rs.getDate("LastReturnDate"),
                     rs.getString("Remarks")
                ));
             }
        } catch (SQLException e) {
            // If column 'Department' doesn't exist in DHAEmployee, retry with a fallback query that omits it
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("unknown column") && msg.contains("department")) {
                String fallback = "SELECT si.ID, si.DHAID, inv.ItemName, si.Quantity AS IssuedQty, " +
                        "e.EmpName, e.Designation, IFNULL(b.BranchName,'') AS Branch, " +
                        "si.RegisterNo, si.IssuedDate, si.Remarks, " +
                        "(SELECT IFNULL(SUM(Quantity), 0) FROM StoreReturn WHERE StoreIssueID = si.ID) AS ReturnedQty, " +
                        "(si.Quantity - (SELECT IFNULL(SUM(Quantity), 0) FROM StoreReturn WHERE StoreIssueID = si.ID)) AS RemainingQty, " +
                        "(SELECT MAX(ReturnDate) FROM StoreReturn WHERE StoreIssueID = si.ID) AS LastReturnDate " +
                        "FROM StoreIssued si " +
                        "JOIN StoreInventory inv ON si.StoreInventoryID = inv.ID " +
                        "LEFT JOIN DHAEmployee e ON si.DHAID = e.DHAID " +
                        "LEFT JOIN Branch b ON e.BranchID = b.ID " +
                        "WHERE si.IssueTypeID = ? " +
                        "ORDER BY si.IssuedDate DESC";
                try (PreparedStatement ps2 = connection.prepareStatement(fallback)) {
                    ps2.setInt(1, issueTypeId);
                    ResultSet rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        int issuedQty = rs2.getInt("IssuedQty");
                        int returnedQty = rs2.getInt("ReturnedQty");
                        int remainingQty = rs2.getObject("RemainingQty") != null ? rs2.getInt("RemainingQty") : (issuedQty - returnedQty);
                        result.add(new dto.store.IssuedInventoryDTO(
                                rs2.getInt("ID"),
                                rs2.getString("DHAID"),
                                rs2.getString("ItemName"),
                                issuedQty,
                                returnedQty,
                                remainingQty,
                                rs2.getString("EmpName"),
                                rs2.getString("Designation"),
                                rs2.getString("Branch"),
                                rs2.getString("RegisterNo"),
                                rs2.getDate("IssuedDate"),
                                rs2.getDate("LastReturnDate"),
                                rs2.getString("Remarks")
                        ));
                    }
                } catch (SQLException ex2) {
                    throw new RuntimeException("Failed to load issued items for type " + issueTypeId, ex2);
                }
            } else {
                throw new RuntimeException("Failed to load issued items for type " + issueTypeId, e);
            }
        }
         return result;
     }

    /**
     * Update StoreIssued row and adjust StoreInventory accordingly when quantity changes.
     * Fields updated: Quantity, DHAID, RegisterNo, IssuedDate, Remarks
     */
    public int updateStoreIssued(int issuedId, int newQuantity, String newDhaId, String newRegisterNo, java.sql.Date newIssuedDate, String newRemarks) {
        try {
            connection.setAutoCommit(false);

            // Load existing issued row
            String sel = "SELECT StoreInventoryID, Quantity FROM StoreIssued WHERE ID = ?";
            Integer storeInventoryId = null;
            int oldQuantity = 0;
            try (PreparedStatement ps = connection.prepareStatement(sel)) {
                ps.setInt(1, issuedId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        storeInventoryId = rs.getInt("StoreInventoryID");
                        oldQuantity = rs.getInt("Quantity");
                    } else {
                        connection.rollback(); connection.setAutoCommit(true);
                        throw new RuntimeException("Issued row not found: " + issuedId);
                    }
                }
            }

            if (storeInventoryId != null) {
                int diff = newQuantity - oldQuantity; // positive means we need to reduce inventory further
                if (diff > 0) {
                    // check availability
                    try (PreparedStatement ps = connection.prepareStatement("SELECT IFNULL(Quantity,0) AS q FROM StoreInventory WHERE ID = ?")) {
                        ps.setInt(1, storeInventoryId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                int avail = rs.getInt("q");
                                if (avail < diff) {
                                    connection.rollback(); connection.setAutoCommit(true);
                                    throw new RuntimeException("Insufficient stock to increase issued quantity");
                                }
                            }
                        }
                    }
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE StoreInventory SET Quantity = Quantity - ? WHERE ID = ?")) {
                        ps.setInt(1, diff);
                        ps.setInt(2, storeInventoryId);
                        ps.executeUpdate();
                    }
                } else if (diff < 0) {
                    // return difference to inventory
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE StoreInventory SET Quantity = Quantity + ? WHERE ID = ?")) {
                        ps.setInt(1, -diff);
                        ps.setInt(2, storeInventoryId);
                        ps.executeUpdate();
                    }
                }
            }

            // Update issued row
            String up = "UPDATE StoreIssued SET Quantity = ?, DHAID = ?, RegisterNo = ?, IssuedDate = ?, Remarks = ? WHERE ID = ?";
            try (PreparedStatement ps = connection.prepareStatement(up)) {
                ps.setInt(1, newQuantity);
                if (newDhaId == null) ps.setObject(2, null); else ps.setString(2, newDhaId);
                ps.setString(3, newRegisterNo);
                ps.setDate(4, newIssuedDate);
                ps.setString(5, newRemarks);
                ps.setInt(6, issuedId);
                int updated = ps.executeUpdate();
                connection.commit(); connection.setAutoCommit(true);
                return updated;
            }
        } catch (SQLException e) {
            try { connection.rollback(); connection.setAutoCommit(true); } catch (SQLException ex) {}
            throw new RuntimeException("Failed to update StoreIssued", e);
        }
    }

    /**
     * Delete a StoreIssued row. Returns number of rows deleted (0/1).
     * It will: delete StoreReturn rows, delete RemainingStore row, add issued qty back to StoreInventory, then delete StoreIssued.
     */
    public int deleteStoreIssued(int issuedId) {
        try {
            connection.setAutoCommit(false);

            String sel = "SELECT StoreInventoryID, Quantity FROM StoreIssued WHERE ID = ?";
            Integer storeInventoryId = null;
            int issuedQty = 0;
            try (PreparedStatement ps = connection.prepareStatement(sel)) {
                ps.setInt(1, issuedId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        storeInventoryId = rs.getInt("StoreInventoryID");
                        issuedQty = rs.getInt("Quantity");
                    } else {
                        connection.rollback(); connection.setAutoCommit(true);
                        return 0;
                    }
                }
            }

            // delete StoreReturn rows
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM StoreReturn WHERE StoreIssueID = ?")) {
                ps.setInt(1, issuedId); ps.executeUpdate();
            }

            // delete RemainingStore row
            try {
                try (PreparedStatement ps = connection.prepareStatement("DELETE FROM RemainingStore WHERE StoreIssuedID = ?")) {
                    ps.setInt(1, issuedId); ps.executeUpdate();
                }
            } catch (SQLException exRem) {
                // ignore if table doesn't exist
                System.err.println("[WARN] RemainingStore delete skipped: " + exRem.getMessage());
            }

            // add back issuedQty to inventory
            if (storeInventoryId != null) {
                try (PreparedStatement ps = connection.prepareStatement("UPDATE StoreInventory SET Quantity = Quantity + ? WHERE ID = ?")) {
                    ps.setInt(1, issuedQty); ps.setInt(2, storeInventoryId); ps.executeUpdate();
                }
            }

            // delete the issued row
            int deleted;
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM StoreIssued WHERE ID = ?")) {
                ps.setInt(1, issuedId); deleted = ps.executeUpdate();
            }

            connection.commit(); connection.setAutoCommit(true);
            return deleted;
        } catch (SQLException e) {
            try { connection.rollback(); connection.setAutoCommit(true); } catch (SQLException ex) {}
            throw new RuntimeException("Failed to delete StoreIssued", e);
        }
    }
 }
