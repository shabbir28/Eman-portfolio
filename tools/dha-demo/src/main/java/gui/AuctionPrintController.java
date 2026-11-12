package gui;

import dto.ReportItem;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuctionPrintController {

    @FXML private VBox printableArea;
    @FXML private Label headerLabel;
    @FXML private TableView<AuctionPrintItem> printTable;
    @FXML private TableColumn<AuctionPrintItem, Integer> colSer;
    @FXML private TableColumn<AuctionPrintItem, String> colNomenclature;
    @FXML private TableColumn<AuctionPrintItem, String> colAu;
    @FXML private TableColumn<AuctionPrintItem, Integer> colQty;
    @FXML private Button printButton;
    
    // Header Fields
    @FXML private TextField issueVoucherField;
    @FXML private TextField datedLeftField;
    @FXML private TextField branchLeftField;
    @FXML private TextField receiptVoucherField;
    @FXML private TextField datedRightField;
    @FXML private TextField branchRightField;
    
    // Footer Fields
    @FXML private TextField totalItemsField;
    @FXML private TextField issuerNameField;
    @FXML private TextField receiverNameField;
    @FXML private TextField issuerDhaField;
    @FXML private TextField receiverDhaField;
    @FXML private TextField receiverBranchField;

    private ObservableList<AuctionPrintItem> items = FXCollections.observableArrayList();

    public void initialize() {
        // Setup columns
        colSer.setCellValueFactory(data -> data.getValue().serProperty().asObject());
        colNomenclature.setCellValueFactory(data -> data.getValue().nomenclatureProperty());
        colAu.setCellValueFactory(data -> data.getValue().auProperty());
        colQty.setCellValueFactory(data -> data.getValue().qtyProperty().asObject());

        // Enable editing
        colNomenclature.setCellFactory(TextFieldTableCell.forTableColumn());
        colAu.setCellFactory(TextFieldTableCell.forTableColumn());
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        printTable.setItems(items);
        
        // Init dates to today
        String today = LocalDate.now().toString();
        if(datedLeftField != null) datedLeftField.setText(today);
        if(datedRightField != null) datedRightField.setText(today);
    }

    public void initData(List<ReportItem> reportItems, LocalDate startDate, LocalDate endDate) {
        // Set Header
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String startStr = (startDate != null) ? startDate.format(dtf) : "N/A";
        String endStr = (endDate != null) ? endDate.format(dtf) : "N/A";
        
        // "Auction List Detail DHA IT Branch ON [Starts] to [End] VIDE DHA IT OFFICE"
        String headerText = String.format("Auction List Detail DHA IT Branch ON %s to %s VIDE DHA IT OFFICE", startStr, endStr);
        headerLabel.setText(headerText);

        // Populate Table
        items.clear();
        int index = 1;
        for (ReportItem item : reportItems) {
            // Default A/U to "Nos"
            items.add(new AuctionPrintItem(index++, item.getItemName(), "Nos", item.getQuantity()));
        }
        
        // Adjust table height to show all rows + header (approximate)
        int rowHeight = 30;
        int headerHeight = 30;
        int tableHeight = headerHeight + (items.size() * rowHeight) + 10;
        printTable.setPrefHeight(Math.max(200, tableHeight)); // min height 200
        printTable.setMinHeight(Math.max(200, tableHeight)); 
    }

    @FXML
    private void handlePrint() {
        printButton.setVisible(false);
        // Clear selection to avoid blue highlight in print
        printTable.getSelectionModel().clearSelection();
        
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
        javafx.scene.control.TextField[] fields = {
            issueVoucherField, datedLeftField, branchLeftField,
            receiptVoucherField, datedRightField, branchRightField,
            totalItemsField, issuerNameField, receiverNameField, 
            issuerDhaField, receiverDhaField, receiverBranchField
        };
        
        for (javafx.scene.control.TextField field : fields) {
            if (field != null) {
                field.setEditable(editable);
                if (!editable) {
                    field.deselect();
                    field.setFocusTraversable(false);
                } else {
                    field.setFocusTraversable(true);
                }
            }
        }
        if (!editable) {
            printTable.requestFocus();
        }
    }

    // Inner class for proper property binding
    public static class AuctionPrintItem {
        private final SimpleIntegerProperty ser;
        private final SimpleStringProperty nomenclature;
        private final SimpleStringProperty au;
        private final SimpleIntegerProperty qty;

        public AuctionPrintItem(int ser, String nomenclature, String au, int qty) {
            this.ser = new SimpleIntegerProperty(ser);
            this.nomenclature = new SimpleStringProperty(nomenclature);
            this.au = new SimpleStringProperty(au);
            this.qty = new SimpleIntegerProperty(qty);
        }

        public SimpleIntegerProperty serProperty() { return ser; }
        public SimpleStringProperty nomenclatureProperty() { return nomenclature; }
        public SimpleStringProperty auProperty() { return au; }
        public SimpleIntegerProperty qtyProperty() { return qty; }
    }
}
