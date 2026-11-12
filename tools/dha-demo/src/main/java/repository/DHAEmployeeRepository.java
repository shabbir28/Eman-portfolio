package repository;

import database.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DHAEmployeeRepository {
    private final Connection connection;

    public DHAEmployeeRepository() {
        this.connection = ConnectionFactory.getInstance().getConnection();
    }

    public DHAEmployeeItem findById(String dhaId) {
        // BranchID replaced Department in DB; fetch BranchName via BranchRepository
        String query = "SELECT DHAID, EmpName, BranchID, Designation, Scale FROM DHAEmployee WHERE DHAID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dhaId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int branchId = rs.getObject("BranchID") == null ? -1 : rs.getInt("BranchID");
                String branchName = "";
                if (branchId > 0) {
                    BranchRepository br = new BranchRepository();
                    BranchRepository.BranchItem bi = br.findById(branchId);
                    branchName = bi == null ? "" : bi.getBranchName();
                }
                return new DHAEmployeeItem(
                    rs.getString("DHAID"),
                    rs.getString("EmpName"),
                    branchId,
                    branchName,
                    rs.getString("Designation"),
                    rs.getInt("Scale")
                );
            }
        } catch (SQLException e) {
            // Log and return null so callers can fall back gracefully
            System.err.println("DHAEmployeeRepository.findById failed: " + e.getMessage());
            return null;
        }
        return null;
    }

    public boolean exists(String dhaId) {
        return findById(dhaId) != null;
    }

    public List<DHAEmployeeItem> findAll() {
        String sql = "SELECT DHAID, EmpName, BranchID, Designation, Scale FROM DHAEmployee ORDER BY EmpName";
        List<DHAEmployeeItem> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int branchId = rs.getObject("BranchID") == null ? -1 : rs.getInt("BranchID");
                String branchName = "";
                if (branchId > 0) {
                    BranchRepository br = new BranchRepository();
                    BranchRepository.BranchItem bi = br.findById(branchId);
                    branchName = bi == null ? "" : bi.getBranchName();
                }
                result.add(new DHAEmployeeItem(
                    rs.getString("DHAID"),
                    rs.getString("EmpName"),
                    branchId,
                    branchName,
                    rs.getString("Designation"),
                    rs.getInt("Scale")
                ));
            }
        } catch (SQLException e) {
            System.err.println("DHAEmployeeRepository.findAll failed: " + e.getMessage());
            return result;
        }
        return result;
    }

    public void save(DHAEmployeeItem item) {
        String query = "INSERT INTO DHAEmployee (DHAID, EmpName, BranchID, Designation, Scale) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, item.getDhaId());
            ps.setString(2, item.getEmpName());
            ps.setInt(3, item.getBranchId());
            ps.setString(4, item.getDesignation());
            ps.setInt(5, item.getScale());
            // Debug logging
            System.out.println("DHAEmployeeRepository.save: Executing SQL: " + query);
            System.out.println("Params: dhaId=" + item.getDhaId() + ", name=" + item.getEmpName() + ", branchId=" + item.getBranchId() + ", designation=" + item.getDesignation() + ", scale=" + item.getScale());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save employee: " + e.getMessage(), e);
        }
    }

    public void update(DHAEmployeeItem item) {
        String query = "UPDATE DHAEmployee SET EmpName = ?, BranchID = ?, Designation = ?, Scale = ? WHERE DHAID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, item.getEmpName());
            ps.setInt(2, item.getBranchId());
            ps.setString(3, item.getDesignation());
            ps.setInt(4, item.getScale());
            ps.setString(5, item.getDhaId());
            // Debug logging
            System.out.println("DHAEmployeeRepository.update: Executing SQL: " + query);
            System.out.println("Params: name=" + item.getEmpName() + ", branchId=" + item.getBranchId() + ", designation=" + item.getDesignation() + ", scale=" + item.getScale() + ", dhaId=" + item.getDhaId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee: " + e.getMessage(), e);
        }
    }

    public void delete(String dhaId) {
        String query = "DELETE FROM DHAEmployee WHERE DHAID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dhaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    public static class DHAEmployeeItem {
        private final String dhaId;
        private final String empName;
        private final int branchId;
        private final String branchName;
        private final String designation;
        private final int scale;

        public DHAEmployeeItem(String dhaId, String empName, int branchId, String branchName, String designation, int scale) {
            this.dhaId = dhaId;
            this.empName = empName;
            this.branchId = branchId;
            this.branchName = branchName;
            this.designation = designation;
            this.scale = scale;
        }

        public String getDhaId() {
            return dhaId;
        }

        public String getEmpName() {
            return empName;
        }

        public int getBranchId() { return branchId; }
        public String getBranchName() { return branchName; }

        public String getDesignation() {
            return designation;
        }

        public int getScale() {
            return scale;
        }

        @Override
        public String toString() {
            String name = (empName == null || empName.isEmpty()) ? "(No name)" : empName;
            String desig = (designation == null || designation.isEmpty()) ? "" : " (" + designation + ")";
            String branch = (branchName == null || branchName.isEmpty()) ? "" : " [" + branchName + "]";
            return dhaId + " - " + name + desig + branch;
        }
    }
 }
