package dto.report;

import java.time.LocalDate;

public class ReportIssuedDTO {
    private String itemName;
    private int quantity;
    private String remarks;
    private LocalDate issuedDate;
    private String sourceType; // "Auction", "Store", "User"

    public ReportIssuedDTO(String itemName, int quantity, String remarks, LocalDate issuedDate, String sourceType) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.remarks = remarks;
        this.issuedDate = issuedDate;
        this.sourceType = sourceType;
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
