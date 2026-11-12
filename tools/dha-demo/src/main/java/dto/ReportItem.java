package dto;

import java.time.LocalDate;

public class ReportItem {
    private String itemName;
    private int quantity;
    private LocalDate issuedDate;

    public ReportItem(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public ReportItem(String itemName, int quantity, LocalDate issuedDate) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.issuedDate = issuedDate;
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

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }
}
