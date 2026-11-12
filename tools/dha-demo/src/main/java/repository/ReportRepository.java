package repository;

import database.ConnectionFactory;
import dto.report.ReportIssuedDTO;
import dto.report.ReportReceivedDTO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository {
    private final Connection connection;

    public ReportRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public List<ReportIssuedDTO> getIssuedReport(LocalDate startDate, LocalDate endDate) {
        List<ReportIssuedDTO> list = new ArrayList<>();
        Date sqlStart = Date.valueOf(startDate);
        Date sqlEnd = Date.valueOf(endDate);

        // 1. Auction Issued
        String auctionSql = "SELECT ai.ItemName, ais.Quantity, ais.Remarks, ais.IssuedDate " +
                            "FROM AuctionIssued ais " +
                            "JOIN AuctionInventory ai ON ais.AuctionID = ai.ID " +
                            "WHERE ais.IssuedDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(auctionSql)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReportIssuedDTO(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("Remarks"),
                        rs.getDate("IssuedDate").toLocalDate(),
                        "Auction"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or rethrow
        }

        // 2. Store Issued
        String storeSql = "SELECT si.ItemName, s.Quantity, s.Remarks, s.IssuedDate " +
                          "FROM StoreIssued s " +
                          "JOIN StoreInventory si ON s.StoreInventoryID = si.ID " +
                          "WHERE s.IssuedDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(storeSql)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReportIssuedDTO(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("Remarks"),
                        rs.getDate("IssuedDate").toLocalDate(),
                        "Store"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. User Issued
        String userSql = "SELECT ur.ItemName, ui.IssuedDate, ui.Remarks " +
                         "FROM UserIssued ui " +
                         "JOIN UserReceived ur ON ui.UserID = ur.ID " +
                         "WHERE ui.IssuedDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(userSql)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // UserIssued uses the original quantity from UserReceived? 
                // Wait, UserIssued table doesn't have Quantity column in schema I viewed.
                // Let's check schema again. `UserIssued` has `UserID`. `UserReceived` has `Quantity`.
                // Does issuing a user item mean issuing the *whole* item? Yes, usually.
                // I'll fetch Quantity from UserReceived.
                // Re-checking schema if UserIssued has Quantity.
                // `CREATE TABLE UserIssued (ID ..., UserID INT, IssuedDate ..., Remarks ...)` -> No Quantity.
                // So I use quantity from UserReceived.
                // Need to subquery or join to get quantity.
                
                // Fetch quantity from UserReceived
                int quantity = 0; // Default or fetch
                // The query above joins UserReceived (ur), so I can get ur.Quantity.
                // Let's modify the query string below.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Corrected User Issued Query
        String userSqlCorrect = "SELECT ur.ItemName, ur.Quantity, ui.Remarks, ui.IssuedDate " +
                                "FROM UserIssued ui " +
                                "JOIN UserReceived ur ON ui.UserID = ur.ID " +
                                "WHERE ui.IssuedDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(userSqlCorrect)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReportIssuedDTO(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("Remarks"),
                        rs.getDate("IssuedDate").toLocalDate(),
                        "User"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<ReportReceivedDTO> getReceivedReport(LocalDate startDate, LocalDate endDate) {
        List<ReportReceivedDTO> list = new ArrayList<>();
        Date sqlStart = Date.valueOf(startDate);
        Date sqlEnd = Date.valueOf(endDate);

        // 1. Auction Received
        // "in Received From for AuctionReceived item it shows 'Branch' and in details it shows LetterNo"
        String auctionSql = "SELECT ItemName, Quantity, LetterNo, Remarks, ReceiveDate FROM AuctionReceived " +
                            "WHERE ReceiveDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(auctionSql)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReportReceivedDTO(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        "Branch", // Received From
                        rs.getString("LetterNo"), // Detail
                        rs.getString("Remarks"),
                        rs.getDate("ReceiveDate").toLocalDate(),
                        "Auction"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Store Received
        // Minute Sheet: shows PurchaseOrderNo (ReceivedFrom), Vendor (Detail)
        // Petty Cash: shows 'Petty Cash' (ReceivedFrom), VoucherNo (Detail)
        // Replacement: shows 'Replacement' (ReceivedFrom), Branch (Detail)
        String storeSql = "SELECT sr.ItemName, sr.Quantity, sr.Remarks, sr.ReceiveDate, rf.TypeName, " +
                          "ms.PurchaseOrderNo, ms.Vendor, " +
                          "pc.VoucherNo, " +
                          "b.BranchName " +
                          "FROM StoreReceived sr " +
                          "JOIN ReceivedFrom rf ON sr.ReceivedFromID = rf.ID " +
                          "LEFT JOIN MinuteSheet ms ON sr.ID = ms.StoreReceivedID " +
                          "LEFT JOIN PettyCash pc ON sr.ID = pc.StoreReceivedID " +
                          "LEFT JOIN Replacement rep ON sr.ID = rep.StoreReceivedID " +
                          "LEFT JOIN Branch b ON rep.BranchID = b.ID " +
                          "WHERE sr.ReceiveDate BETWEEN ? AND ? " +
                          "AND sr.ReceivedFromID IN (1, 2, 3)"; // Exclude UserReceived (4) if handled separately
        
        try (PreparedStatement ps = connection.prepareStatement(storeSql)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String typeName = rs.getString("TypeName");
                String receivedFrom = "";
                String detail = "";

                if ("MinuteSheet".equalsIgnoreCase(typeName)) {
                    receivedFrom = rs.getString("PurchaseOrderNo"); // Req: PurchaseOrderNo
                    detail = rs.getString("Vendor"); // Req: Vendor
                } else if ("PettyCash".equalsIgnoreCase(typeName)) {
                    receivedFrom = "Petty Cash"; // Req: 'Petty Cash'
                    detail = rs.getString("VoucherNo"); // Req: VoucherNo
                } else if ("Replacement".equalsIgnoreCase(typeName)) {
                    receivedFrom = "Replacement"; // Req: 'Replacement' (fixed typo in prompt 'Replacment')
                    detail = rs.getString("BranchName"); // Req: Branch
                }

                list.add(new ReportReceivedDTO(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        receivedFrom,
                        detail,
                        rs.getString("Remarks"),
                        rs.getDate("ReceiveDate").toLocalDate(),
                        "Store"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. User Received
        // "Received From ... show PurchaseOrderNo ... in detail it show Vendor"
        String userSql = "SELECT ItemName, Quantity, PurchaseOrderNo, Vendor, Remarks, ReceiveDate " +
                         "FROM UserReceived " +
                         "WHERE ReceiveDate BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(userSql)) {
            ps.setDate(1, sqlStart);
            ps.setDate(2, sqlEnd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReportReceivedDTO(
                        rs.getString("ItemName"),
                        rs.getInt("Quantity"),
                        rs.getString("PurchaseOrderNo"),
                        rs.getString("Vendor"),
                        rs.getString("Remarks"),
                        rs.getDate("ReceiveDate").toLocalDate(),
                        "User"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
