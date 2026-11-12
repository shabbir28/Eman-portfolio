package test;

import repository.AuctionIssuedRepository;
import dto.auction.IssuedAuctionInventoryDTO;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class ReportDebug {
    public static void main(String[] args) {
        try {
            AuctionIssuedRepository repo = new AuctionIssuedRepository();
            
            // Test 1: Get All (implied by just querying a wide range)
            LocalDate start = LocalDate.of(2020, 1, 1);
            LocalDate end = LocalDate.now().plusDays(1);
            
            System.out.println("Testing Query with range: " + start + " to " + end);
            
            List<IssuedAuctionInventoryDTO> items = repo.getIssuedItemsByDateRange(
                Date.valueOf(start), 
                Date.valueOf(end)
            );
            
            System.out.println("Found " + items.size() + " items.");
            for (IssuedAuctionInventoryDTO item : items) {
                System.out.println(" - " + item.getItemName() + " | Qty: " + item.getIssuedQuantity() + " | Date: " + item.getIssuedDate());
            }

            // Test 2: Try fetching without date filter if possible (checking raw DB content)
            // Just printing to see if connection works and JOIN works.
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
