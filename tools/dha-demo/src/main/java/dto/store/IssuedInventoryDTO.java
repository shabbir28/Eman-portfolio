package dto.store;

import java.sql.Date;

public class IssuedInventoryDTO { private int id;
    private String dhaId;
    private String itemName;
    private int issuedQuantity;
    
    // Fields specific to Temporary (will be 0 or null for Permanent)
    private int returnQuantity;
    private int remainingQuantity;
    private Date returnDate;

    private String name;        // DHAEmployee.EmpName
    private String designation; // DHAEmployee.Designation
    private String branch;      // DHAEmployee.BranchID (mapped to branch)
    private String registerNo;
    private Date issuedDate;
    private String remarks;

    // Construtor for all fields
    public IssuedInventoryDTO(int id, String dhaId, String itemName, int issuedQuantity, int returnQuantity, int remainingQuantity,
                              String name, String designation, String branch, String registerNo,
                              Date issuedDate, Date returnDate, String remarks) {
        this.id = id;
        this.dhaId = dhaId;
        this.itemName = itemName;
        this.issuedQuantity = issuedQuantity;
        this.returnQuantity = returnQuantity;
        this.remainingQuantity = remainingQuantity;
        this.name = name;
        this.designation = designation;
        this.branch = branch;
        this.registerNo = registerNo;
        this.issuedDate = issuedDate;
        this.returnDate = returnDate;
        this.remarks = remarks;
    }

    public int getId() { return id; }
    public String getDhaId() { return dhaId; }
    public String getItemName() { return itemName; }
    public int getIssuedQuantity() { return issuedQuantity; }
    public int getReturnQuantity() { return returnQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public String getBranch() { return branch; }
    public String getRegisterNo() { return registerNo; }
    public Date getIssuedDate() { return issuedDate; }
    public Date getReturnDate() { return returnDate; }
    public String getRemarks() { return remarks; }
}
