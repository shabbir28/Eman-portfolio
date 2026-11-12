package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceivedRepository {
    private final Connection connection;

    public ReceivedRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public int addAuctionReceived(String itemName, int quantity, String letterNo, java.sql.Date receiveDate, String remarks) {
        String sql = "INSERT INTO AuctionReceived (ItemName, Quantity, LetterNo, ReceiveDate, Remarks) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, itemName);
            ps.setInt(2, quantity);
            ps.setString(3, letterNo);
            ps.setDate(4, receiveDate);
            ps.setString(5, remarks);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert AuctionReceived", e);
        }
    }

    public int addStoreReceived(String itemName, int quantity, int receivedFromId, java.sql.Date receiveDate, Integer unitPrice, String remarks) {
        String sql = "INSERT INTO StoreReceived (ItemName, Quantity, ReceivedFromID, ReceiveDate, UnitPrice, Remarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, itemName);
            ps.setInt(2, quantity);
            ps.setInt(3, receivedFromId);
            ps.setDate(4, receiveDate);
            if (unitPrice == null) ps.setObject(5, null); else ps.setInt(5, unitPrice);
            ps.setString(6, remarks);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert StoreReceived", e);
        }
    }

    // Helper: Ensure "UserReceived" type exists in ReceivedFrom table (ID=4)
    private void ensureUserReceivedType() {
        try {
            // Check if ID=4 exists in ReceivedFrom
            String checkSql = "SELECT COUNT(*) FROM ReceivedFrom WHERE ID = 4";
            try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        // Insert the UserReceived type
                        String insertSql = "INSERT INTO ReceivedFrom (ID, TypeName) VALUES (4, 'UserReceived')";
                        try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                            insertPs.executeUpdate();
                            System.out.println("[DEBUG] Added 'UserReceived' type to ReceivedFrom table");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[WARN] ensureUserReceivedType failed: " + e.getMessage());
            // Continue anyway - the type might already exist
        }
    }

    // New: Add StoreReceived record for User Items with reason in Remarks field
    public int addStoreReceivedForUserItem(String itemName, int quantity, java.sql.Date receiveDate, String reason) {
        // Ensure UserReceived type exists in ReceivedFrom
        ensureUserReceivedType();

        String sql = "INSERT INTO StoreReceived (ItemName, Quantity, ReceivedFromID, ReceiveDate, UnitPrice, Remarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, itemName);
            ps.setInt(2, quantity);
            ps.setInt(3, 4); // ReceivedFromID = 4 (UserReceived type)
            ps.setDate(4, receiveDate);
            ps.setNull(5, java.sql.Types.INTEGER); // No unit price for user items
            ps.setString(6, reason); // Save reason in Remarks field
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("[ERROR] addStoreReceivedForUserItem failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to insert StoreReceived for User Item: " + e.getMessage(), e);
        }
    }

    public int addMinuteSheet(int storeReceivedId, String purchaseOrderNo, String vendor) {
        String sql = "INSERT INTO MinuteSheet (PurchaseOrderNo, Vendor, StoreReceivedID) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, purchaseOrderNo);
            ps.setString(2, vendor);
            ps.setInt(3, storeReceivedId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert MinuteSheet", e);
        }
    }

    public int addPettyCash(int storeReceivedId, String voucherNo) {
        String sql = "INSERT INTO PettyCash (VoucherNo, StoreReceivedID) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, voucherNo);
            ps.setInt(2, storeReceivedId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert PettyCash", e);
        }
    }

    public int addReplacement(int storeReceivedId, int branchId) {
        String sql = "INSERT INTO Replacement (BranchID, StoreReceivedID) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, storeReceivedId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert Replacement", e);
        }
    }

    public int addUserReceived(String itemName, int quantity, String purchaseOrderNo, String vendor, java.sql.Date receiveDate,
                               String dhaId, Integer branchId, String warrantyDemand, String warrantyProvided, Integer statusId, String reason) {
        String sql = "INSERT INTO UserReceived (ItemName, Quantity, PurchaseOrderNo, Vendor, ReceiveDate, DHAID, BranchID, WarrantyDemand, WarrantyProvided, StatusID, Reason, Remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, itemName);
            ps.setInt(2, quantity);
            ps.setString(3, purchaseOrderNo);
            ps.setString(4, vendor);
            ps.setDate(5, receiveDate);
            if (dhaId == null) ps.setObject(6, null); else ps.setString(6, dhaId);
            if (branchId == null) ps.setObject(7, null); else ps.setInt(7, branchId);
            ps.setString(8, warrantyDemand);
            ps.setString(9, warrantyProvided);
            if (statusId == null) ps.setObject(10, null); else ps.setInt(10, statusId);
            // Use provided reason (initially "None" from UI) and leave Remarks empty
            ps.setString(11, reason == null ? "None" : reason);
            ps.setString(12, ""); // Remarks is empty string
            int affectedRows = ps.executeUpdate();

            // Try to return generated key
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys != null && keys.next()) {
                    return keys.getInt(1);
                }
            }

            // Fallback for MySQL: SELECT LAST_INSERT_ID()
            try (PreparedStatement psLast = connection.prepareStatement("SELECT LAST_INSERT_ID() AS id")) {
                try (ResultSet rsLast = psLast.executeQuery()) {
                    if (rsLast.next()) return rsLast.getInt("id");
                }
            } catch (SQLException ex) {
                // ignore fallback errors
            }

            // If we couldn't determine ID, return -1 to indicate unknown id
            return -1;
        } catch (SQLException e) {
            System.err.println("[ERROR] addUserReceived failed: " + e.getMessage());
            throw new RuntimeException("Failed to insert UserReceived", e);
        }
    }

    // New: update StatusID for a given UserReceived ID (returns number of rows updated)
    public int updateUserReceivedStatus(int userReceivedId, Integer statusId) {
        String sql = "UPDATE UserReceived SET StatusID = ? WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (statusId == null) ps.setObject(1, null); else ps.setInt(1, statusId);
            ps.setInt(2, userReceivedId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update UserReceived status", e);
        }
    }

    // New: update Reason for a given UserReceived ID (returns number of rows updated)
    public int updateUserReceivedReason(int userReceivedId, String reason) {
        String sql = "UPDATE UserReceived SET Reason = ? WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, userReceivedId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update UserReceived reason", e);
        }
    }

    public java.util.List<dto.store.ReceivedInventoryDTO> getAllReceivedItems() {
        String sql = "SELECT r.ItemName, r.Quantity, rf.TypeName, r.ReceiveDate, r.UnitPrice, r.Remarks, " +
                     "r.ReceivedFromID, ms.InternalPurchaseOrderNo, ms.Vendor, pc.VoucherNo, b.BranchName " +
                     "FROM StoreReceived r " +
                     "JOIN ReceivedFrom rf ON r.ReceivedFromID = rf.ID " +
                     "LEFT JOIN MinuteSheet ms ON r.ID = ms.StoreReceivedID " +
                     "LEFT JOIN PettyCash pc ON r.ID = pc.StoreReceivedID " +
                     "LEFT JOIN Replacement rep ON r.ID = rep.StoreReceivedID " +
                     "LEFT JOIN Branch b ON rep.BranchID = b.ID " +
                     "ORDER BY r.ReceiveDate DESC";
        // Check MinuteSheet column name. In inventory_db.sql: Create Table MinuteSheet (PurchaseOrderNo...)
        // So alias in SQL above should be 'ms.PurchaseOrderNo'.
        // Retrying with correct column name.

        sql = "SELECT r.ID, r.ItemName, r.Quantity, rf.TypeName, r.ReceiveDate, r.UnitPrice, r.Remarks, " +
                     "r.ReceivedFromID, ms.PurchaseOrderNo, ms.Vendor, pc.VoucherNo, b.BranchName " +
                     "FROM StoreReceived r " +
                     "JOIN ReceivedFrom rf ON r.ReceivedFromID = rf.ID " +
                     "LEFT JOIN MinuteSheet ms ON r.ID = ms.StoreReceivedID " +
                     "LEFT JOIN PettyCash pc ON r.ID = pc.StoreReceivedID " +
                     "LEFT JOIN Replacement rep ON r.ID = rep.StoreReceivedID " +
                     "LEFT JOIN Branch b ON rep.BranchID = b.ID " +
                     "WHERE r.ReceivedFromID IN (1, 2, 3) " +
                     "ORDER BY r.ReceiveDate DESC";
        
        java.util.List<dto.store.ReceivedInventoryDTO> result = new java.util.ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int typeId = rs.getInt("ReceivedFromID");
                String detail = "";
                String receivedFromDisplay = rs.getString("TypeName");

                if (typeId == 1) { // MinuteSheet
                    receivedFromDisplay = rs.getString("PurchaseOrderNo"); // Req: if 1 show PO No
                    detail = rs.getString("Vendor");
                } else if (typeId == 2) { // PettyCash
                    detail = rs.getString("VoucherNo");
                } else if (typeId == 3) { // Replacement
                    detail = rs.getString("BranchName");
                }

                result.add(new dto.store.ReceivedInventoryDTO(
                    rs.getInt("ID"),
                    rs.getInt("ReceivedFromID"),
                    rs.getString("ItemName"),
                    rs.getInt("Quantity"),
                    receivedFromDisplay,
                    detail,
                    rs.getDate("ReceiveDate"),
                    (Integer) rs.getObject("UnitPrice"),
                    rs.getString("Remarks")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load received items", e);
        }
        return result;
    }

    /**
     * Get all user-received items from StoreReceived table (ReceivedFromID = 4).
     * Used for User Inventory module to display items received from users.
     */
    public java.util.List<dto.store.ReceivedInventoryDTO> getUserReceivedItems() {
        String sql = "SELECT r.ID, r.ItemName, r.Quantity, rf.TypeName, r.ReceiveDate, r.UnitPrice, r.Remarks, " +
                     "r.ReceivedFromID, ms.PurchaseOrderNo, ms.Vendor, pc.VoucherNo, b.BranchName " +
                     "FROM StoreReceived r " +
                     "JOIN ReceivedFrom rf ON r.ReceivedFromID = rf.ID " +
                     "LEFT JOIN MinuteSheet ms ON r.ID = ms.StoreReceivedID " +
                     "LEFT JOIN PettyCash pc ON r.ID = pc.StoreReceivedID " +
                     "LEFT JOIN Replacement rep ON r.ID = rep.StoreReceivedID " +
                     "LEFT JOIN Branch b ON rep.BranchID = b.ID " +
                     "WHERE r.ReceivedFromID = 4 " +
                     "ORDER BY r.ReceiveDate DESC";

        java.util.List<dto.store.ReceivedInventoryDTO> result = new java.util.ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int typeId = rs.getInt("ReceivedFromID");
                String detail = "";
                String receivedFromDisplay = rs.getString("TypeName");

                if (typeId == 4) { // UserReceived - show the reason in remarks
                    detail = ""; // No detail for user items
                }

                result.add(new dto.store.ReceivedInventoryDTO(
                    rs.getInt("ID"),
                    rs.getInt("ReceivedFromID"),
                    rs.getString("ItemName"),
                    rs.getInt("Quantity"),
                    receivedFromDisplay,
                    detail,
                    rs.getDate("ReceiveDate"),
                    (Integer) rs.getObject("UnitPrice"),
                    rs.getString("Remarks")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load user received items", e);
        }
        return result;
    }

    public int updateStoreReceived(int id, int quantity, Integer unitPrice, String remarks, int receivedFromId, java.sql.Date receiveDate) {
        String sqlUp = "UPDATE StoreReceived SET Quantity = ?, UnitPrice = ?, Remarks = ?, ReceivedFromID = ?, ReceiveDate = ? WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUp)) {
            ps.setInt(1, quantity);
            if (unitPrice == null) ps.setObject(2, null); else ps.setInt(2, unitPrice);
            ps.setString(3, remarks);
            ps.setInt(4, receivedFromId);
            ps.setDate(5, receiveDate);
            ps.setInt(6, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update StoreReceived", e);
        }
    }

    public int deleteStoreReceived(int id) {
        // Perform transactional removal of StoreReceived and related rows.
        try {
            connection.setAutoCommit(false);

            // 1) Load the StoreReceived row to get ItemName
            String sel = "SELECT ItemName, Quantity, ReceivedFromID FROM StoreReceived WHERE ID = ?";
            String itemName = null;
            int receivedQty = 0;
            try (PreparedStatement ps = connection.prepareStatement(sel)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        itemName = rs.getString("ItemName");
                        receivedQty = rs.getInt("Quantity");
                    } else {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        return 0; // nothing to delete
                    }
                }
            }

            // 2) Delete dependent detail rows (MinuteSheet, PettyCash, Replacement)
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM MinuteSheet WHERE StoreReceivedID = ?")) {
                ps.setInt(1, id); ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM PettyCash WHERE StoreReceivedID = ?")) {
                ps.setInt(1, id); ps.executeUpdate();
            }
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Replacement WHERE StoreReceivedID = ?")) {
                ps.setInt(1, id); ps.executeUpdate();
            }

            // 3) Find StoreInventory ID for the item (ItemName is UNIQUE in StoreInventory)
            Integer storeInvId = null;
            try (PreparedStatement ps = connection.prepareStatement("SELECT ID, Quantity FROM StoreInventory WHERE ItemName = ?")) {
                ps.setString(1, itemName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        storeInvId = rs.getInt("ID");
                    }
                }
            }

            if (storeInvId != null) {
                // 4) Find StoreIssued rows referencing this inventory and remove them safely
                String selIssued = "SELECT ID, Quantity FROM StoreIssued WHERE StoreInventoryID = ?";
                try (PreparedStatement ps = connection.prepareStatement(selIssued)) {
                    ps.setInt(1, storeInvId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int issuedId = rs.getInt("ID");
                            int issuedQty = rs.getInt("Quantity");

                            // delete StoreReturn rows for this issue
                            try (PreparedStatement psr = connection.prepareStatement("DELETE FROM StoreReturn WHERE StoreIssueID = ?")) {
                                psr.setInt(1, issuedId); psr.executeUpdate();
                            }

                            // delete RemainingStore row if any
                            try {
                                try (PreparedStatement psr = connection.prepareStatement("DELETE FROM RemainingStore WHERE StoreIssuedID = ?")) {
                                    psr.setInt(1, issuedId); psr.executeUpdate();
                                }
                            } catch (SQLException exRem) {
                                // If RemainingStore table doesn't exist, ignore and continue (backwards compatibility)
                                System.err.println("[WARN] RemainingStore delete skipped: " + exRem.getMessage());
                            }

                            // before removing StoreIssued, put back issued quantity into StoreInventory
                            try (PreparedStatement psu = connection.prepareStatement("UPDATE StoreInventory SET Quantity = Quantity + ? WHERE ID = ?")) {
                                psu.setInt(1, issuedQty); psu.setInt(2, storeInvId); psu.executeUpdate();
                            }

                            // delete the StoreIssued row
                            try (PreparedStatement psd = connection.prepareStatement("DELETE FROM StoreIssued WHERE ID = ?")) {
                                psd.setInt(1, issuedId); psd.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 5) Finally delete the StoreReceived row (this will also update StoreInventory via trigger)
            int deleted;
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM StoreReceived WHERE ID = ?")) {
                ps.setInt(1, id);
                deleted = ps.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
            return deleted;
        } catch (SQLException e) {
            try { connection.rollback(); connection.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
            throw new RuntimeException("Failed to delete StoreReceived", e);
        }
    }

    /**
     * Fallback: find a UserReceived record by fields to obtain its ID.
     * Returns the latest matching ID or -1 if none found.
     */
    public int findUserReceivedId(String itemName, String purchaseOrderNo, java.sql.Date receiveDate, String dhaId) {
        String sql = "SELECT ID FROM UserReceived WHERE ItemName = ? AND PurchaseOrderNo = ? AND ReceiveDate = ? AND (DHAID = ? OR (DHAID IS NULL AND ? IS NULL)) ORDER BY ID DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemName);
            ps.setString(2, purchaseOrderNo);
            ps.setDate(3, receiveDate);
            if (dhaId == null) {
                ps.setObject(4, null);
                ps.setObject(5, null);
            } else {
                ps.setString(4, dhaId);
                ps.setString(5, dhaId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find UserReceived ID", e);
        }
        return -1;
    }
}
