package gui;

import dto.ReportItem;
import dto.auction.IssuedAuctionInventoryDTO;
import dto.report.ReportIssuedDTO;
import dto.report.ReportReceivedDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import repository.AuctionIssuedRepository;
import repository.ReportRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportController {

    @FXML private HBox selectionView;
    @FXML private VBox dateSelectionView;
    @FXML private VBox resultView; // Auction Result View
    @FXML private VBox addItemView; // Auction Add View

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label lblSelectionTitle;

    // --- Auction Report Components ---
    @FXML private TableView<ReportItem> reportTable;
    @FXML private TableColumn<ReportItem, String> colItemName;
    @FXML private TableColumn<ReportItem, Integer> colQuantity;
    @FXML private TableColumn<ReportItem, java.time.LocalDate> colIssuedDate;
    @FXML private TableColumn<ReportItem, Void> colAction;

    @FXML private TextField inputItemName;
    @FXML private TextField inputQuantity;

    // --- Overall Report Components ---
    @FXML private VBox overallResultView;
    @FXML private HBox overallTablesContainer;
    @FXML private VBox issuedSectionBox;
    @FXML private VBox receivedSectionBox;

    // Overall Issued Table
    @FXML private TableView<ReportIssuedDTO> overallIssuedTable;
    @FXML private TableColumn<ReportIssuedDTO, String> colOvIssuedName;
    @FXML private TableColumn<ReportIssuedDTO, Integer> colOvIssuedQty;
    @FXML private TableColumn<ReportIssuedDTO, String> colOvIssuedRemarks;
    @FXML private TableColumn<ReportIssuedDTO, Void> colOvIssuedAction;

    // Overall Received Table
    @FXML private TableView<ReportReceivedDTO> overallReceivedTable;
    @FXML private TableColumn<ReportReceivedDTO, String> colOvReceivedName;
    @FXML private TableColumn<ReportReceivedDTO, Integer> colOvReceivedQty;
    @FXML private TableColumn<ReportReceivedDTO, String> colOvReceivedFrom;
    @FXML private TableColumn<ReportReceivedDTO, String> colOvReceivedDetail;
    @FXML private TableColumn<ReportReceivedDTO, String> colOvReceivedRemarks;
    @FXML private TableColumn<ReportReceivedDTO, Void> colOvReceivedAction;

    // Add Issued View
    @FXML private VBox addIssuedView;
    @FXML private TextField inputIssuedName;
    @FXML private TextField inputIssuedQty;
    @FXML private TextField inputIssuedRemarks;

    // Add Received View
    @FXML private VBox addReceivedView;
    @FXML private TextField inputReceivedName;
    @FXML private TextField inputReceivedQty;
    @FXML private TextField inputReceivedFrom;
    @FXML private TextField inputReceivedDetail;
    @FXML private TextField inputReceivedRemarks;


    private final AuctionIssuedRepository auctionIssuedRepository = new AuctionIssuedRepository();
    private final ReportRepository reportRepository = new ReportRepository();
    private final ObservableList<ReportItem> reportData = FXCollections.observableArrayList();
    
    // Overall Data
    private final ObservableList<ReportIssuedDTO> overallIssuedData = FXCollections.observableArrayList();
    private final ObservableList<ReportReceivedDTO> overallReceivedData = FXCollections.observableArrayList();

    private boolean isOverallMode = false;
    private ReportIssuedDTO editingIssuedItem = null;
    private ReportReceivedDTO editingReceivedItem = null;

    @FXML
    public void initialize() {
        setupAuctionTable();
        setupOverallTables();
        setupDatePickers();

        // Ensure both overall section boxes have equal width inside the overallTablesContainer
        if (overallTablesContainer != null && issuedSectionBox != null && receivedSectionBox != null) {
            // Bind each section box prefWidth to half of the overallTablesContainer width minus spacing/padding
            overallTablesContainer.widthProperty().addListener((obs, oldW, newW) -> {
                double totalWidth = newW.doubleValue();
                double spacing = overallTablesContainer.getSpacing();
                javafx.geometry.Insets pad = overallTablesContainer.getPadding();
                double hPadding = (pad == null) ? 0 : (pad.getLeft() + pad.getRight());
                double available = Math.max(0, totalWidth - spacing - hPadding);
                double each = available / 2.0;
                issuedSectionBox.setPrefWidth(each);
                receivedSectionBox.setPrefWidth(each);
            });
        }
    }

    private void setupDatePickers() {
        if (startDatePicker != null) startDatePicker.valueProperty().addListener((obs, oldV, newV) -> onDateRangeChanged());
        if (endDatePicker != null) endDatePicker.valueProperty().addListener((obs, oldV, newV) -> onDateRangeChanged());

        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        javafx.util.StringConverter<LocalDate> robustConverter = new javafx.util.StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : date.format(isoFormatter);
            }
            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                try { return LocalDate.parse(string.trim()); } catch (Exception e) { return null; }
            }
        };
        if (startDatePicker != null) startDatePicker.setConverter(robustConverter);
        if (endDatePicker != null) endDatePicker.setConverter(robustConverter);
    }

    private void setupAuctionTable() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colIssuedDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));

        // Prefer wider name column and unconstrained resize so horizontal scrolling is used instead of truncation
        colItemName.setPrefWidth(400);
        colQuantity.setPrefWidth(120);
        colIssuedDate.setPrefWidth(160);
        reportTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Use Text node in cells to allow wrapping and show full content
        colItemName.setCellFactory(col -> new TableCell<ReportItem, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            {
                text.getStyleClass().add("table-text");
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });

        // Quantity uses default cell, IssuedDate formatted earlier

        colIssuedDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.toString());
            }
        });

        colAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<ReportItem, Void> call(final TableColumn<ReportItem, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = new Button("Delete");
                    {
                        btnDelete.setStyle("-fx-background-color: #db2d2d; -fx-text-fill: white; -fx-background-radius: 5;");
                        btnDelete.setOnAction(event -> reportData.remove(getTableView().getItems().get(getIndex())));
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btnDelete);
                    }
                };
            }
        });
        reportTable.setItems(reportData);
    }

    private void setupOverallTables() {
        // Overall Issued
        colOvIssuedName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colOvIssuedQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colOvIssuedRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // Set preferred widths and unconstrained resize so horizontal scrollbar appears
        colOvIssuedName.setPrefWidth(360);
        colOvIssuedQty.setPrefWidth(120);
        colOvIssuedRemarks.setPrefWidth(300);
        overallIssuedTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Use Text-based cell factories for string columns to avoid truncation and allow wrapping
        colOvIssuedName.setCellFactory(col -> new TableCell<ReportIssuedDTO, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { text.setText(item); setGraphic(text); }
            }
        });
        colOvIssuedRemarks.setCellFactory(col -> new TableCell<ReportIssuedDTO, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            { text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { text.setText(item); setGraphic(text); }
            }
        });

        colOvIssuedAction.setCellFactory(createActionCellFactory(overallIssuedData, true));
        overallIssuedTable.setItems(overallIssuedData);

        // Overall Received
        colOvReceivedName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colOvReceivedQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colOvReceivedFrom.setCellValueFactory(new PropertyValueFactory<>("receivedFrom"));
        colOvReceivedDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));
        colOvReceivedRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // Pref widths
        colOvReceivedName.setPrefWidth(320);
        colOvReceivedQty.setPrefWidth(100);
        colOvReceivedFrom.setPrefWidth(180);
        colOvReceivedDetail.setPrefWidth(300);
        colOvReceivedRemarks.setPrefWidth(260);
        overallReceivedTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // String columns use Text cell factory for wrapping and full content
        colOvReceivedName.setCellFactory(col -> new TableCell<ReportReceivedDTO, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            { text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { text.setText(item); setGraphic(text); }
            }
        });
        colOvReceivedFrom.setCellFactory(col -> new TableCell<ReportReceivedDTO, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            { text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { text.setText(item); setGraphic(text); }
            }
        });
        colOvReceivedDetail.setCellFactory(col -> new TableCell<ReportReceivedDTO, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            { text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { text.setText(item); setGraphic(text); }
            }
        });
        colOvReceivedRemarks.setCellFactory(col -> new TableCell<ReportReceivedDTO, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
            { text.wrappingWidthProperty().bind(col.widthProperty().subtract(10)); }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else { text.setText(item); setGraphic(text); }
            }
        });

        colOvReceivedAction.setCellFactory(createActionCellFactory(overallReceivedData, false));
        overallReceivedTable.setItems(overallReceivedData);
    }

    private <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> createActionCellFactory(ObservableList<T> list, boolean isIssued) {
        return param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("action-btn-success");
                btnDelete.getStyleClass().add("action-btn-danger");
                pane.setAlignment(javafx.geometry.Pos.CENTER);

                btnEdit.setOnAction(event -> {
                    T item = list.get(getIndex());
                    if (isIssued) prepareEditIssued((ReportIssuedDTO) item);
                    else prepareEditReceived((ReportReceivedDTO) item);
                });

                btnDelete.setOnAction(event -> {
                    T item = list.get(getIndex());
                    list.remove(item);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private void prepareEditIssued(ReportIssuedDTO item) {
        editingIssuedItem = item;
        inputIssuedName.setText(item.getItemName());
        inputIssuedQty.setText(String.valueOf(item.getQuantity()));
        inputIssuedRemarks.setText(item.getRemarks());
        showView(addIssuedView);
    }

    private void prepareEditReceived(ReportReceivedDTO item) {
        editingReceivedItem = item;
        inputReceivedName.setText(item.getItemName());
        inputReceivedQty.setText(String.valueOf(item.getQuantity()));
        inputReceivedFrom.setText(item.getReceivedFrom());
        inputReceivedDetail.setText(item.getDetail());
        inputReceivedRemarks.setText(item.getRemarks());
        showView(addReceivedView);
    }

    private void onDateRangeChanged() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start == null || end == null) return;

        if (start.isAfter(end)) {
            showAlert("Start date cannot be after end date.");
            return;
        }

        if (isOverallMode) {
            refreshOverallReport(start, end);
        } else {
            refreshAuctionReport(start, end);
        }
    }

    private void refreshAuctionReport(LocalDate start, LocalDate end) {
        try {
            List<IssuedAuctionInventoryDTO> issuedItems = auctionIssuedRepository.getIssuedItemsByDateRange(
                    Date.valueOf(start), Date.valueOf(end)
            );
            reportData.clear();
            for (IssuedAuctionInventoryDTO dto : issuedItems) {
                LocalDate issued = dto.getIssuedDate() == null ? null : dto.getIssuedDate().toLocalDate();
                reportData.add(new ReportItem(dto.getItemName(), dto.getIssuedQuantity(), issued));
            }
            if (reportData.isEmpty()) {
                showAlert("No issued auction items found for the selected date range.");
            }
            showView(resultView);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading report data: " + e.getMessage());
        }
    }

    private void refreshOverallReport(LocalDate start, LocalDate end) {
        try {
            overallIssuedData.setAll(reportRepository.getIssuedReport(start, end));
            overallReceivedData.setAll(reportRepository.getReceivedReport(start, end));
            showView(overallResultView);
            
            if (overallIssuedData.isEmpty() && overallReceivedData.isEmpty()) {
                showAlert("No data found for the selected date range.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading overall report: " + e.getMessage());
        }
    }

    // --- Navigation Handlers ---

    @FXML
    private void handleAuctionSelect(ActionEvent event) {
        isOverallMode = false;
        if (lblSelectionTitle != null) lblSelectionTitle.setText("Auction");
        showView(dateSelectionView);
    }

    @FXML
    private void handleOverallSelect(ActionEvent event) {
        isOverallMode = true;
        if (lblSelectionTitle != null) lblSelectionTitle.setText("Overall Report");
        showView(dateSelectionView);
    }

    @FXML
    private void handleCancelToSelection(ActionEvent event) {
        showView(selectionView);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        onDateRangeChanged();
    }

    @FXML
    private void handleAuctionPrint(ActionEvent event) {
        if (reportData.isEmpty()) {
            showAlert("No data to print.");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/auction_print_view.fxml"));
            javafx.scene.Parent root = loader.load();
            AuctionPrintController controller = loader.getController();

            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
             if (start == null) start = LocalDate.now();
             if (end == null) end = LocalDate.now();
            
            controller.initData(reportData, start, end);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Print Auction Report");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error opening print view: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrint(ActionEvent event) {
        if (!isOverallMode) {
            showAlert("Printing is only available for Overall Report.");
            return;
        }

        if (overallIssuedData.isEmpty() && overallReceivedData.isEmpty()) {
            showAlert("No data to print.");
            return;
        }

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start == null || end == null) {
            start = LocalDate.now();
            end = LocalDate.now();
        }

        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job != null) {
            javafx.print.Printer printer = job.getPrinter();
            javafx.print.PageLayout landscapeLayout = printer.createPageLayout(
                    javafx.print.Paper.A4,
                    javafx.print.PageOrientation.LANDSCAPE,
                    javafx.print.Printer.MarginType.DEFAULT
            );
            job.getJobSettings().setPageLayout(landscapeLayout);

            if (job.showPrintDialog(overallResultView.getScene().getWindow())) {
                javafx.print.PageLayout pageLayout = job.getJobSettings().getPageLayout();
                double pageWidth = pageLayout.getPrintableWidth();
                
                Node printableNode = createPrintNode(start, end, pageWidth);
                
                boolean success = job.printPage(printableNode);
                if (success) {
                    job.endJob();
                }
            }
        }
    }

    private Node createPrintNode(LocalDate start, LocalDate end, double pageWidth) {
        VBox root = new VBox(5); // Increased spacing slightly
        root.setPrefWidth(pageWidth);
        root.setMaxWidth(pageWidth);
        root.setStyle("-fx-background-color: white; -fx-padding: 0;"); // Remove padding as printable width usually accounts for margins

        // Header
        Label header = new Label("DHA IT BRANCH (Print Shop)");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-font-family: 'Times New Roman';");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(javafx.geometry.Pos.CENTER);

        // Date
        String dateStr;
        if (start.equals(end)) {
            dateStr = "Date: " + start.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        } else {
            dateStr = "Date: " + start.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + " to " + end.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        }
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-font-family: 'Times New Roman';");
        dateLabel.setMaxWidth(Double.MAX_VALUE);
        dateLabel.setAlignment(javafx.geometry.Pos.CENTER);
        
        root.getChildren().addAll(header, dateLabel);

        // Table Grid
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-grid-lines-visible: true;");
        grid.setMaxWidth(pageWidth);
        
        // Calculate dynamic widths based on pageWidth
        // We have 10 columns. We divide pageWidth into percentages.
        // Issued (50%): Sr(5%), Item(20%), Qty(8%), Remarks(17%)
        // Received (50%): Sr(5%), Item(15%), Qty(8%), From(8%), Detail(7%), Remarks(7%)
        
        double p = pageWidth;
        double[] widths = {
            p * 0.05, p * 0.20, p * 0.08, p * 0.17, // Issued
            p * 0.05, p * 0.15, p * 0.08, p * 0.08, p * 0.07, p * 0.07  // Received
        };
        
        // --- Headers ---
        // Row 0: "Issued Item Detail" (Col 0-3) | "Received Item Detail" (Col 4-9)
        addMergedHeader(grid, "Issued Item Detail", 0, 0, 4);
        addMergedHeader(grid, "Received Item Detail", 4, 0, 6); // Changed colspan to 6 for 6 columns

        // Row 1: Column Headers
        String[] headers = {
            "Sr#", "Item Name", "Qty", "Remarks",
            "Sr#", "Item Name", "Qty", "Received From", "Detail", "Remarks"
        };

        for (int i = 0; i < headers.length; i++) {
            addCell(grid, headers[i], i, 1, true, widths[i]);
        }

        // --- Data Rows ---
        int maxRows = Math.max(overallIssuedData.size(), overallReceivedData.size());
        int currentRow = 2; // Start from row 2
        
        int totalIssuedQty = 0;
        int totalReceivedQty = 0;

        for (int i = 0; i < maxRows; i++) {
            // Issued Data
            if (i < overallIssuedData.size()) {
                ReportIssuedDTO item = overallIssuedData.get(i);
                addCell(grid, String.valueOf(i + 1), 0, currentRow, false, widths[0]);
                addCell(grid, item.getItemName(), 1, currentRow, false, widths[1]);
                addCell(grid, String.valueOf(item.getQuantity()), 2, currentRow, false, widths[2]);
                addCell(grid, item.getRemarks(), 3, currentRow, false, widths[3]);
                totalIssuedQty += item.getQuantity();
            } else {
                addEmptyRow(grid, 0, currentRow, 4, widths);
            }

            // Received Data
            if (i < overallReceivedData.size()) {
                ReportReceivedDTO item = overallReceivedData.get(i);
                addCell(grid, String.valueOf(i + 1), 4, currentRow, false, widths[4]);
                addCell(grid, item.getItemName(), 5, currentRow, false, widths[5]);
                addCell(grid, String.valueOf(item.getQuantity()), 6, currentRow, false, widths[6]);
                addCell(grid, item.getReceivedFrom(), 7, currentRow, false, widths[7]);
                addCell(grid, item.getDetail(), 8, currentRow, false, widths[8]);
                addCell(grid, item.getRemarks(), 9, currentRow, false, widths[9]);
                totalReceivedQty += item.getQuantity();
            } else {
                 addEmptyRow(grid, 4, currentRow, 6, widths, 4); // offset 4 for widths index
            }
            currentRow++;
        }

        // Totals Row
        // For Merged cells like "Total" spanning across, we need to handle widths carefully.
        // Issued Total: "Total" under Item Name.
        // We put "Total" in Col 1 (Item Name). Col 0 (Sr) is empty.
        // Col 2 (Qty) has the total. Col 3 (Remarks) is empty.
        
        addCell(grid, "", 0, currentRow, false, widths[0]);
        addCell(grid, "Total", 1, currentRow, true, widths[1]);
        addCell(grid, String.valueOf(totalIssuedQty), 2, currentRow, true, widths[2]);
        addCell(grid, "", 3, currentRow, false, widths[3]);

        // Received Total: "Total" under Item Name (Col 5). 
        // Col 4 (Sr) empty.
        // Col 6 (Qty) has total.
        // Col 7,8,9 empty.
        addCell(grid, "", 4, currentRow, false, widths[4]);
        addCell(grid, "Total", 5, currentRow, true, widths[5]);
        addCell(grid, String.valueOf(totalReceivedQty), 6, currentRow, true, widths[6]);
        addCell(grid, "", 7, currentRow, false, widths[7]);
        addCell(grid, "", 8, currentRow, false, widths[8]);
        addCell(grid, "", 9, currentRow, false, widths[9]);

        root.getChildren().add(grid);
        
        return root;
    }

    private void addMergedHeader(javafx.scene.layout.GridPane grid, String text, int col, int row, int colSpan) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-font-family: 'Times New Roman'; -fx-font-size: 14px; -fx-alignment: center; -fx-padding: 2;");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        
        javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane(label);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 0.5;");
        
        grid.add(pane, col, row, colSpan, 1);
    }

    private void addCell(javafx.scene.layout.GridPane grid, String text, int col, int row, boolean bold, double width) {
        Label label = new Label(text);
        label.setWrapText(true);
        // reduce font size slightly to fit A4 better
        label.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 10px; -fx-padding: 2;" + (bold ? " -fx-font-weight: bold;" : ""));
        label.setMaxWidth(Double.MAX_VALUE);
        if (text != null && (text.length() < 5 || Character.isDigit(text.charAt(0)))) {
             label.setAlignment(javafx.geometry.Pos.CENTER);
        } else {
             label.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        }

        javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane(label);
        pane.setStyle("-fx-border-color: black; -fx-border-width: 0.5;");
        pane.setPrefWidth(width);
        
        grid.add(pane, col, row);
    }

    private void addEmptyRow(javafx.scene.layout.GridPane grid, int startCol, int row, int colCount, double[] widths) {
        addEmptyRow(grid, startCol, row, colCount, widths, startCol);
    }

    private void addEmptyRow(javafx.scene.layout.GridPane grid, int startCol, int row, int colCount, double[] widths, int widthOffset) {
        for (int i = 0; i < colCount; i++) {
             addCell(grid, "", startCol + i, row, false, widths[widthOffset + i]);
        }
    }

    // --- Add/Edit Handlers for Auction ---
    @FXML
    private void handleAddItemView(ActionEvent event) {
        inputItemName.clear();
        inputQuantity.clear();
        showView(addItemView);
    }

    @FXML
    private void handleSaveItem(ActionEvent event) {
        // ... (Existing logic for Auction Add, which adds to reportData only)
        String name = inputItemName.getText();
        String qtyStr = inputQuantity.getText();
        if (name.isEmpty() || qtyStr.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            reportData.add(new ReportItem(name, qty));
            showView(resultView);
        } catch (NumberFormatException e) {
            showAlert("Quantity must be a valid number.");
        }
    }

    @FXML
    private void handleCancelAddItem(ActionEvent event) {
        showView(resultView);
    }

    // --- Add/Edit Handlers for Overall Issued ---
    @FXML
    private void handleAddIssued(ActionEvent event) {
        editingIssuedItem = null;
        inputIssuedName.clear();
        inputIssuedQty.clear();
        inputIssuedRemarks.clear();
        showView(addIssuedView);
    }

    @FXML
    private void handleSaveIssued(ActionEvent event) {
        String name = inputIssuedName.getText();
        String qtyStr = inputIssuedQty.getText();
        String remarks = inputIssuedRemarks.getText();

        if (name.isEmpty() || qtyStr.isEmpty()) {
            showAlert("Name and Quantity are required.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            if (editingIssuedItem != null) {
                // Edit existing
                editingIssuedItem.setItemName(name);
                editingIssuedItem.setQuantity(qty);
                editingIssuedItem.setRemarks(remarks);
                overallIssuedTable.refresh(); // Refresh table view to show updates
            } else {
                 // Add new (SourceType manually set to "User" or "Manual"?)
                 // Requirement: "Add item name and quantity in the table page not the database"
                overallIssuedData.add(new ReportIssuedDTO(name, qty, remarks, LocalDate.now(), "Manual"));
            }
            showView(overallResultView);
        } catch (NumberFormatException e) {
            showAlert("Quantity must be a valid number.");
        }
    }

    // --- Add/Edit Handlers for Overall Received ---
    @FXML
    private void handleAddReceived(ActionEvent event) {
        editingReceivedItem = null;
        inputReceivedName.clear();
        inputReceivedQty.clear();
        inputReceivedFrom.clear();
        inputReceivedDetail.clear();
        inputReceivedRemarks.clear();
        showView(addReceivedView);
    }

    @FXML
    private void handleSaveReceived(ActionEvent event) {
        String name = inputReceivedName.getText();
        String qtyStr = inputReceivedQty.getText();
        String from = inputReceivedFrom.getText();
        String detail = inputReceivedDetail.getText();
        String remarks = inputReceivedRemarks.getText();

         if (name.isEmpty() || qtyStr.isEmpty()) {
            showAlert("Name and Quantity are required.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            if (editingReceivedItem != null) {
                // Edit
                editingReceivedItem.setItemName(name);
                editingReceivedItem.setQuantity(qty);
                editingReceivedItem.setReceivedFrom(from);
                editingReceivedItem.setDetail(detail);
                editingReceivedItem.setRemarks(remarks);
                overallReceivedTable.refresh();
            } else {
                overallReceivedData.add(new ReportReceivedDTO(name, qty, from, detail, remarks, LocalDate.now(), "Manual"));
            }
            showView(overallResultView);
        } catch (NumberFormatException e) {
             showAlert("Quantity must be a valid number.");
        }
    }

    @FXML
    private void handleCancelAdd(ActionEvent event) {
        if (isOverallMode) showView(overallResultView);
        else showView(resultView);
    }

    private void showView(Node view) {
        selectionView.setVisible(false);
        dateSelectionView.setVisible(false);
        resultView.setVisible(false);
        addItemView.setVisible(false);
        if (overallResultView != null) overallResultView.setVisible(false);
        if (addIssuedView != null) addIssuedView.setVisible(false);
        if (addReceivedView != null) addReceivedView.setVisible(false);

        view.setVisible(true);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }
}
