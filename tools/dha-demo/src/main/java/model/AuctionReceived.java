package model;

import java.sql.Date;

public class AuctionReceived {
    private int id;
    private int auctionInventoryId;
    private int quantity;
    private String letterNo;
    private Date receivedDate;
    private String remarks;

    public AuctionReceived(int id, int auctionInventoryId, int quantity, String letterNo, Date receivedDate, String remarks) {
        this.id = id;
        this.auctionInventoryId = auctionInventoryId;
        this.quantity = quantity;
        this.letterNo = letterNo;
        this.receivedDate = receivedDate;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuctionInventoryId() {
        return auctionInventoryId;
    }

    public void setAuctionInventoryId(int auctionInventoryId) {
        this.auctionInventoryId = auctionInventoryId;
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

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
