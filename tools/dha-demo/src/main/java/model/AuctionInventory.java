package model;

public class AuctionInventory {
    private Long id;
    private String itemName;
    private String status;

    public AuctionInventory() {
    }

    public AuctionInventory(Long id, String itemName, String status) {
        this.id = id;
        this.itemName = itemName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

