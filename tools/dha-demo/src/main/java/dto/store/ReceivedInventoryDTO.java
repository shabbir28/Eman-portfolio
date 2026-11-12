package dto.store;

import java.sql.Date;

public class ReceivedInventoryDTO {
    private int id;
    private String itemName;
    private int quantity;
    private int receivedFromId;
    private String receivedFrom; // TypeName
    private String detail;       // Vendor / Voucher / Branch
    private Date date;
    private Integer unitPrice;
    private String remarks;

    public ReceivedInventoryDTO(int id, int receivedFromId, String itemName, int quantity, String receivedFrom, String detail, Date date, Integer unitPrice, String remarks) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.receivedFromId = receivedFromId;
        this.receivedFrom = receivedFrom;
        this.detail = detail;
        this.date = date;
        this.unitPrice = unitPrice;
        this.remarks = remarks;
    }

    public int getId() { return id; }
    public int getReceivedFromId() { return receivedFromId; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public String getReceivedFrom() { return receivedFrom; }
    public String getDetail() { return detail; }
    public Date getDate() { return date; }
    public Integer getUnitPrice() { return unitPrice; }
    public String getRemarks() { return remarks; }
}
