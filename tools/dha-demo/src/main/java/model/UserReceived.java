package model;

import java.sql.Date;

public class UserReceived {
    private int id; // UserReceivedID
    private String itemName;
    private int quantity;
    private Date receiveDate;
    private String purchaseOrderNo;
    private String vendor;
    private int dhaId;
    private String warrantyDemand;
    private String warrantyProvided;
    private String reason; // Pending Reason
    private String remarks;
    private int statusId;

    public UserReceived() {}

    public UserReceived(int id, String itemName, int quantity, Date receiveDate, String purchaseOrderNo, String vendor, int dhaId, String warrantyDemand, String warrantyProvided, String reason, String remarks, int statusId) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.receiveDate = receiveDate;
        this.purchaseOrderNo = purchaseOrderNo;
        this.vendor = vendor;
        this.dhaId = dhaId;
        this.warrantyDemand = warrantyDemand;
        this.warrantyProvided = warrantyProvided;
        this.reason = reason;
        this.remarks = remarks;
        this.statusId = statusId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Date getReceiveDate() { return receiveDate; }
    public void setReceiveDate(Date receiveDate) { this.receiveDate = receiveDate; }
    public String getPurchaseOrderNo() { return purchaseOrderNo; }
    public void setPurchaseOrderNo(String purchaseOrderNo) { this.purchaseOrderNo = purchaseOrderNo; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public int getDhaId() { return dhaId; }
    public void setDhaId(int dhaId) { this.dhaId = dhaId; }
    public String getWarrantyDemand() { return warrantyDemand; }
    public void setWarrantyDemand(String warrantyDemand) { this.warrantyDemand = warrantyDemand; }
    public String getWarrantyProvided() { return warrantyProvided; }
    public void setWarrantyProvided(String warrantyProvided) { this.warrantyProvided = warrantyProvided; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }
}
