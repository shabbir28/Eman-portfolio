package model;

import java.sql.Date;

public class AuctionIssued {
    private int id;
    private int auctionInventoryId;
    private int quantity;
    private Date issuedDate;
    private String remarks;

    public AuctionIssued(int id, int auctionInventoryId, int quantity, Date issuedDate, String remarks) {
        this.id = id;
        this.auctionInventoryId = auctionInventoryId;
        this.quantity = quantity;
        this.issuedDate = issuedDate;
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

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
