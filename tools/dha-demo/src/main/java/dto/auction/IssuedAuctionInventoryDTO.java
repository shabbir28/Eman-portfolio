package dto.auction;

import java.sql.Date;

public class IssuedAuctionInventoryDTO {
    private int id;
    private String itemName;
    private int issuedQuantity;
    private Date issuedDate;
    private String remarks;
    private int auctionInventoryId;

    public IssuedAuctionInventoryDTO(int id, String itemName, int issuedQuantity, Date issuedDate, String remarks, int auctionInventoryId) {
        this.id = id;
        this.itemName = itemName;
        this.issuedQuantity = issuedQuantity;
        this.issuedDate = issuedDate;
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

    public int getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(int issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
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

    public int getAuctionInventoryId() {
        return auctionInventoryId;
    }

    public void setAuctionInventoryId(int auctionInventoryId) {
        this.auctionInventoryId = auctionInventoryId;
    }
}
