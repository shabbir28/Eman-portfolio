package service;

import model.AuctionItem;
import model.InventoryStats;
import model.LowStockItem;
import model.PendingItem;
import repository.InventoryRepository;

import java.util.Collections;
import java.util.List;

public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService() {
        this(new InventoryRepository());
    }

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<LowStockItem> getLowStockItems() {
        try {
            return inventoryRepository.fetchLowStockItems(10);
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] getLowStockItems failed: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public InventoryStats getInventoryStats() {
        try {
            // Total in-store should be sum of quantities in InStore (or computed fallback inside repository)
            int totalInStore = inventoryRepository.fetchTotalInStoreQuantity();
            // Total temporary/permanent assigned come from StoreIssued by IssueType
            int totalTemporary = inventoryRepository.fetchStoreIssuedQuantityByStatus("Temporary");
            int totalPermanent = inventoryRepository.fetchStoreIssuedQuantityByStatus("Permanent");

            // Total store items = in-store + temporary assigned + permanent assigned
            int totalStoreItems = Math.max(0, totalInStore + totalTemporary + totalPermanent);

            System.out.println("[DEBUG] InventoryService.getInventoryStats computed: totalStoreItems=" + totalStoreItems + ", totalInStore=" + totalInStore + ", temp=" + totalTemporary + ", perm=" + totalPermanent);

            return new InventoryStats(totalStoreItems, totalInStore, totalTemporary, totalPermanent);
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] getInventoryStats failed: " + ex.getMessage());
            ex.printStackTrace();
            return new InventoryStats(0, 0, 0, 0);
        }
    }

    public List<PendingItem> getPendingUserItems() {
        try {
            return inventoryRepository.fetchPendingUserItems();
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] getPendingUserItems failed: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public InventoryStats getUserStats() {
        try {
            return inventoryRepository.fetchUserStats();
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] getUserStats failed: " + ex.getMessage());
            ex.printStackTrace();
            return new InventoryStats(0, 0, 0, 0);
        }
    }

    public List<AuctionItem> getAuctionItems() {
        try {
            return inventoryRepository.fetchAuctionRemainingItems();
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] getAuctionItems failed: " + ex.getMessage());
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public InventoryStats getAuctionStats() {
        try {
            return inventoryRepository.fetchAuctionStats();
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] getAuctionStats failed: " + ex.getMessage());
            ex.printStackTrace();
            return new InventoryStats(0, 0, 0, 0);
        }
    }
}
