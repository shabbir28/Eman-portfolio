package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StoreReceivedPrintController {

    @FXML private VBox printableArea;
    @FXML private Button printButton;

    // Fields
    @FXML private TextField dateField;
    @FXML private TextField descriptionField;
    @FXML private TextField officerNameField;
    @FXML private TextField againstField;
    @FXML private TextField branchField;
    @FXML private TextField reasonField;
    @FXML private TextField techName1Field;
    @FXML private TextField techName2Field;
    @FXML private TextField storeInChargeNameField;
    @FXML private TextField storeInChargeEmpNoField;

    public void initialize() {
        // Set Date to Today
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
    }

    public void setItemName(String itemName) {
        if (descriptionField != null) {
            descriptionField.setText(itemName);
        }
    }

    @FXML
    private void handlePrint() {
        printButton.setVisible(false);
        
        // Hide carets/cursors by disabling editing temporarily
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
            dateField, descriptionField, officerNameField, againstField, branchField,
            reasonField, techName1Field, techName2Field, 
            storeInChargeNameField, storeInChargeEmpNoField
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
            printableArea.requestFocus();
        }
    }
}
