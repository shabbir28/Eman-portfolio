package dto.auction;

public class TotalAuctionInventoryDTO {
    private String itemName;
    private int receivedQuantity;
    private int issuedQuantity;
    private int remainingQuantity;

    public TotalAuctionInventoryDTO(String itemName, int receivedQuantity, int issuedQuantity, int remainingQuantity) {
        this.itemName = itemName;
        this.receivedQuantity = receivedQuantity;
        this.issuedQuantity = issuedQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public int getIssuedQuantity() {
        return issuedQuantity;
    }

    public void setIssuedQuantity(int issuedQuantity) {
        this.issuedQuantity = issuedQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }
}
