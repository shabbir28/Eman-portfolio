package dto.auction;

import java.sql.Date;

public class ReceivedAuctionInventoryDTO {
    private int id;
    private String itemName;
    private int quantity;
    private String letterNo;
    private Date date;
    private String remarks;
    // We might need auctionInventoryId if we need to update related items, but ID of this row is most important.
    private int auctionInventoryId; 

    public ReceivedAuctionInventoryDTO(int id, String itemName, int quantity, String letterNo, Date date, String remarks, int auctionInventoryId) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.letterNo = letterNo;
        this.date = date;
        this.remarks = remarks;
        this.auctionInventoryId = auctionInventoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLetterNo() {
        return letterNo;
    }

    public void setLetterNo(String letterNo) {
        this.letterNo = letterNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getAuctionInventoryId() {
        return auctionInventoryId;
    }

    public void setAuctionInventoryId(int auctionInventoryId) {
        this.auctionInventoryId = auctionInventoryId;
    }
}
