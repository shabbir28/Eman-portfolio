package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.List;

public class PrintVoucherController {

    @FXML private VBox printableArea;
    @FXML private Label issueVoucherNoLabel;
    @FXML private TextField brDeptLeftField;
    @FXML private TextField datedLeftField;
    @FXML private TextField brDeptRightField;
    @FXML private TextField datedRightField;
    
    @FXML private TableView<VoucherItem> itemsTable;
    @FXML private TableColumn<VoucherItem, Integer> srColumn;
    @FXML private TableColumn<VoucherItem, String> descColumn;
    @FXML private TableColumn<VoucherItem, Integer> qtyColumn;
    @FXML private TableColumn<VoucherItem, String> remarksColumn;
    
    @FXML private TextField issuerNameField;
    @FXML private TextField receiverNameField;
    @FXML private TextField issuerDhaField;
    @FXML private TextField receiverDhaField;
    
    @FXML private Button printButton;

    public void initialize() {
        // Setup table columns
        srColumn.setCellValueFactory(new PropertyValueFactory<>("srNo"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        remarksColumn.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        
        // Default dates
        String today = LocalDate.now().toString();
        datedLeftField.setText(today);
        datedRightField.setText(today);
    }

    public void setSocketData(int issueId, String poNo, String itemName, int qty, String remarks, 
                             String receiverName, String receiverDhaId, String receiverBranch) {
        // Delegate to list method for backward compatibility / single item use
        VoucherItem item = new VoucherItem(1, itemName, qty, remarks);
        setSocketDataList(issueId, poNo, List.of(item), receiverName, receiverDhaId, receiverBranch);
    }
    
    public void setSocketDataList(int issueId, String poNo, List<VoucherItem> items, 
                             String receiverName, String receiverDhaId, String receiverBranch) {
        // User requested to display Purchase Order No as the "Issue Voucher No"
        String displayText = (poNo == null || poNo.isEmpty()) ? "" : poNo;
        issueVoucherNoLabel.setText(displayText);
        
        // Pre-fill receiver info if available
        if (receiverName != null) receiverNameField.setText(receiverName);
        if (receiverDhaId != null && !receiverDhaId.isEmpty()) receiverDhaField.setText(receiverDhaId);
        if (receiverBranch != null) {
            brDeptLeftField.setText(receiverBranch);
            brDeptRightField.setText(receiverBranch);
        }
        
        // Populate table
        itemsTable.setItems(FXCollections.observableArrayList(items));
        
        // Auto-adjust table height
        int rowCount = items.size();
        itemsTable.setFixedCellSize(30);
        itemsTable.setPrefHeight(30 * (rowCount + 1) + 10); // header + rows + buffer
    }

    @FXML
    private void handlePrint() {
        printButton.setVisible(false);
        // Clear selection to avoid blue highlight in print
        if (itemsTable.getSelectionModel() != null) {
            itemsTable.getSelectionModel().clearSelection();
        }
        
        // Disable editing to remove carets/cursors
        toggleEditable(false);
        
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            // Set A4 Page Layout
            javafx.print.Printer printer = job.getPrinter();
            javafx.print.PageLayout pageLayout = printer.createPageLayout(
                    javafx.print.Paper.A4,
                    javafx.print.PageOrientation.PORTRAIT,
                    javafx.print.Printer.MarginType.DEFAULT
            );
            job.getJobSettings().setPageLayout(pageLayout);

            if (job.showPrintDialog(printableArea.getScene().getWindow())) {
                // Get the actual printable width from the selected printer/setup
                double printableWidth = job.getJobSettings().getPageLayout().getPrintableWidth();
                double contentWidth = printableArea.getWidth();
                
                // Calculate scale to fit width
                double scale = 1.0;
                if (contentWidth > printableWidth) {
                    scale = printableWidth / contentWidth;
                }
                
                // Scale down slightly more to ensure margins are safe (95% of calculated scale)
                scale = scale * 0.95;

                javafx.scene.transform.Scale scaleTransform = new javafx.scene.transform.Scale(scale, scale);
                printableArea.getTransforms().add(scaleTransform);

                boolean success = job.printPage(printableArea);
                if (success) {
                    job.endJob();
                }
                
                printableArea.getTransforms().remove(scaleTransform);
            }
        }
        
        // Re-enable editing
        toggleEditable(true);
        printButton.setVisible(true);
    }
    
    private void toggleEditable(boolean editable) {
        TextField[] fields = {
            brDeptLeftField, datedLeftField, brDeptRightField, datedRightField,
            issuerNameField, receiverNameField, issuerDhaField, receiverDhaField
        };
        
        for (TextField field : fields) {
            if (field != null) {
                field.setEditable(editable);
                if (!editable) {
                    field.deselect(); // Also clear any text selection
                    field.setFocusTraversable(false); // remove from focus cycle temporarily
                } else {
                    field.setFocusTraversable(true);
                }
            }
        }
        // Force focus to table to ensure no field retains focus if possible
        if (!editable) {
             itemsTable.requestFocus();
        }
    }

    public static class VoucherItem {
        private int srNo;
        private String description;
        private int quantity;
        private String remarks;

        public VoucherItem(int srNo, String description, int quantity, String remarks) {
            this.srNo = srNo;
            this.description = description;
            this.quantity = quantity;
            this.remarks = remarks;
        }

        public int getSrNo() { return srNo; }
        public String getDescription() { return description; }
        public int getQuantity() { return quantity; }
        public String getRemarks() { return remarks; }
    }
}
