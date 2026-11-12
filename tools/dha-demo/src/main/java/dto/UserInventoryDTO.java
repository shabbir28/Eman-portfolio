package dto;

import java.sql.Date;

public class UserInventoryDTO {
    // Shared fields from UserReceived
    private int userReceivedId;
    private String itemName;
    private int quantity;
    private Date receivedDate;
    private String purchaseOrderNo;
    private String vendor;
    private String dhaId;
    private String warrantyDemand;
    private String warrantyProvided;
    private String pendingReason; // Reason from UserReceived
    private String remarks; // Remarks from UserReceived (or overridden by View specific)

    // DHAEmployee
    private String empName;
    private String branch;

    // View Specific
    // Total View
    private String statusName;
    
    // Issued View
    private int userIssuedId;
    private Date issuedDate; // From UserIssued
    private String issuedRemarks; // From UserIssued

    // Pending View
    private int pendingId;
    private int pendingQuantity; // From Pending table

    public UserInventoryDTO(int userReceivedId, String itemName, int quantity, Date receivedDate, String purchaseOrderNo,
                            String vendor, String dhaId, String warrantyDemand, String warrantyProvided, String pendingReason,
                            String remarks, String empName, String branch, String statusName, int userIssuedId,
                            Date issuedDate, String issuedRemarks, int pendingId, int pendingQuantity) {
        this.userReceivedId = userReceivedId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.receivedDate = receivedDate;
        this.purchaseOrderNo = purchaseOrderNo;
        this.vendor = vendor;
        this.dhaId = dhaId;
        this.warrantyDemand = warrantyDemand;
        this.warrantyProvided = warrantyProvided;
        this.pendingReason = pendingReason;
        this.remarks = remarks;
        this.empName = empName;
        this.branch = branch;
        this.statusName = statusName;
        this.userIssuedId = userIssuedId;
        this.issuedDate = issuedDate;
        this.issuedRemarks = issuedRemarks;
        this.pendingId = pendingId;
        this.pendingQuantity = pendingQuantity;
    }
    
    // Getters and Setters
    public int getUserReceivedId() { return userReceivedId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public Date getReceivedDate() { return receivedDate; }
    public String getPurchaseOrderNo() { return purchaseOrderNo; }
    public String getVendor() { return vendor; }
    public String getDhaId() { return dhaId; }
    public String getWarrantyDemand() { return warrantyDemand; }
    public String getWarrantyProvided() { return warrantyProvided; }
    public String getPendingReason() { return pendingReason; }
    public String getRemarks() { return remarks; }
    public String getEmpName() { return empName; }
    public String getBranch() { return branch; }
    public String getStatusName() { return statusName; }
    public int getUserIssuedId() { return userIssuedId; }
    public Date getIssuedDate() { return issuedDate; }
    public String getIssuedRemarks() { return issuedRemarks; }
    public int getPendingId() { return pendingId; }
    public int getPendingQuantity() { return pendingQuantity; }

     // setters... omitted to save space if not needed for mutation, but useful for construction if using setters. 
     // Using constructor for main population.
}
