package dto.store;

public class StoreInventoryDTO {
    private String itemName;
    private int totalItem;
    private int inStore;
    private int assigned;

    public StoreInventoryDTO(String itemName, int totalItem, int inStore, int assigned) {
        this.itemName = itemName;
        this.totalItem = totalItem;
        this.inStore = inStore;
        this.assigned = assigned;
    }

    public String getItemName() { return itemName; }
    public int getTotalItem() { return totalItem; }
    public int getInStore() { return inStore; }
    public int getAssigned() { return assigned; }
}
