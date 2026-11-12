package dto.report;

import java.time.LocalDate;

public class ReportReceivedDTO {
    private String itemName;
    private int quantity;
    private String receivedFrom;
    private String detail;
    private String remarks;
    private LocalDate receivedDate;
    private String sourceType; // "Auction", "Store", "User"

    public ReportReceivedDTO(String itemName, int quantity, String receivedFrom, String detail, String remarks, LocalDate receivedDate, String sourceType) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.receivedFrom = receivedFrom;
        this.detail = detail;
        this.remarks = remarks;
        this.receivedDate = receivedDate;
        this.sourceType = sourceType;
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getReceivedFrom() { return receivedFrom; }
    public void setReceivedFrom(String receivedFrom) { this.receivedFrom = receivedFrom; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
