package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import repository.AuctionInventoryRepository;
import repository.AuctionReceivedRepository;
import repository.AuctionIssuedRepository;
import dto.auction.TotalAuctionInventoryDTO;
import dto.auction.ReceivedAuctionInventoryDTO;
import dto.auction.IssuedAuctionInventoryDTO;

import java.sql.Date;
import java.util.Optional;

public class AuctionInventoryController {

    // Toggle Buttons
    @FXML private ToggleButton btnTotal;
    @FXML private ToggleButton btnReceived;
    @FXML private ToggleButton btnIssued;
    private ToggleGroup tabGroup;

    // VBoxes for Views
    @FXML private VBox viewTotal;
    @FXML private VBox viewReceived;
    @FXML private VBox viewIssued;

    // --- TOTAL TAB ---
    @FXML private TextField searchTotal;
    @FXML private TableView<TotalAuctionInventoryDTO> tableTotal;
    @FXML private TableColumn<TotalAuctionInventoryDTO, String> colTotalItem;
    @FXML private TableColumn<TotalAuctionInventoryDTO, Integer> colTotalRecQty;
    @FXML private TableColumn<TotalAuctionInventoryDTO, Integer> colTotalIssQty;
    @FXML private TableColumn<TotalAuctionInventoryDTO, Integer> colTotalRemQty;

    // --- RECEIVED TAB ---
    @FXML private TextField searchReceived;
    @FXML private TableView<ReceivedAuctionInventoryDTO> tableReceived;
    @FXML private TableColumn<ReceivedAuctionInventoryDTO, String> colRecItem;
    @FXML private TableColumn<ReceivedAuctionInventoryDTO, Integer> colRecQty;
    @FXML private TableColumn<ReceivedAuctionInventoryDTO, String> colRecLetterNo;
    @FXML private TableColumn<ReceivedAuctionInventoryDTO, Date> colRecDate;
    @FXML private TableColumn<ReceivedAuctionInventoryDTO, String> colRecRemarks;
    @FXML private TableColumn<ReceivedAuctionInventoryDTO, Void> colRecAction;

    // --- ISSUED TAB ---
    @FXML private TextField searchIssued;
    @FXML private TableView<IssuedAuctionInventoryDTO> tableIssued;
    @FXML private TableColumn<IssuedAuctionInventoryDTO, String> colIssItem;
    @FXML private TableColumn<IssuedAuctionInventoryDTO, Integer> colIssQty;
    @FXML private TableColumn<IssuedAuctionInventoryDTO, Date> colIssDate;
    @FXML private TableColumn<IssuedAuctionInventoryDTO, String> colIssRemarks;
    @FXML private TableColumn<IssuedAuctionInventoryDTO, Void> colIssAction;

    // Repositories
    private AuctionInventoryRepository auctionRepo;
    private AuctionReceivedRepository receivedRepo;
    private AuctionIssuedRepository issuedRepo;

    // Data Lists
    private ObservableList<TotalAuctionInventoryDTO> listTotal;
    private ObservableList<ReceivedAuctionInventoryDTO> listReceived;
    private ObservableList<IssuedAuctionInventoryDTO> listIssued;

    public void initialize() {
        auctionRepo = new AuctionInventoryRepository();
        receivedRepo = new AuctionReceivedRepository();
        issuedRepo = new AuctionIssuedRepository();

        setupToggleGroup();
        setupColumns();
        setupSearch();

        // Initial load
        handleTabSwitch(null);
    }

    private void setupToggleGroup() {
        tabGroup = new ToggleGroup();
        btnTotal.setToggleGroup(tabGroup);
        btnReceived.setToggleGroup(tabGroup);
        btnIssued.setToggleGroup(tabGroup);
        
        if (tabGroup.getSelectedToggle() == null) btnTotal.setSelected(true);
    }

    private void setupColumns() {
        // Total
        colTotalItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colTotalRecQty.setCellValueFactory(new PropertyValueFactory<>("receivedQuantity"));
        colTotalIssQty.setCellValueFactory(new PropertyValueFactory<>("issuedQuantity"));
        colTotalRemQty.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));

        // Received
        colRecItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colRecQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRecLetterNo.setCellValueFactory(new PropertyValueFactory<>("letterNo"));
        colRecDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colRecRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        setupActionColumn(colRecAction, "Received");

        // Issued
        colIssItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colIssQty.setCellValueFactory(new PropertyValueFactory<>("issuedQuantity"));
        colIssDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        colIssRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        setupActionColumn(colIssAction, "Issued");
    }

    private <S> void setupActionColumn(TableColumn<S, Void> column, String type) {
        column.setCellFactory(param -> new TableCell<S, Void>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                // Styling buttons (copying from StoreInventory)
                // Assuming "action-btn-success" and "action-btn-danger" are global.css classes
                // If not, we saw them used in StoreInventoryController.
                btnEdit.getStyleClass().add("action-btn-success");
                btnDelete.getStyleClass().add("action-btn-danger");
                pane.setAlignment(Pos.CENTER);

                if ("Received".equals(type)) {
                    btnEdit.setOnAction(e -> editReceived((ReceivedAuctionInventoryDTO) getTableView().getItems().get(getIndex())));
                    btnDelete.setOnAction(e -> deleteReceived((ReceivedAuctionInventoryDTO) getTableView().getItems().get(getIndex())));
                } else if ("Issued".equals(type)) {
                    btnEdit.setOnAction(e -> editIssued((IssuedAuctionInventoryDTO) getTableView().getItems().get(getIndex())));
                    btnDelete.setOnAction(e -> deleteIssued((IssuedAuctionInventoryDTO) getTableView().getItems().get(getIndex())));
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null); else setGraphic(pane);
            }
        });
    }

    @FXML
    private void handleTabSwitch(ActionEvent event) {
        viewTotal.setVisible(false);
        viewReceived.setVisible(false);
        viewIssued.setVisible(false);

        if (btnTotal.isSelected()) {
            viewTotal.setVisible(true);
            loadTotal();
        } else if (btnReceived.isSelected()) {
            viewReceived.setVisible(true);
            loadReceived();
        } else if (btnIssued.isSelected()) {
            viewIssued.setVisible(true);
            loadIssued();
        }
    }

    private void loadTotal() {
        listTotal = FXCollections.observableArrayList(auctionRepo.getAuctionInventoryOverview());
        tableTotal.setItems(listTotal);
        applyFilter(searchTotal, listTotal);
    }

    private void loadReceived() {
        listReceived = FXCollections.observableArrayList(receivedRepo.getAllReceivedItems());
        tableReceived.setItems(listReceived);
        applyFilter(searchReceived, listReceived);
    }

    private void loadIssued() {
        listIssued = FXCollections.observableArrayList(issuedRepo.getAllIssuedItems());
        tableIssued.setItems(listIssued);
        applyFilter(searchIssued, listIssued);
    }

    private <T> void setupSearch() {
        searchTotal.textProperty().addListener((o, old, val) -> { if(listTotal!=null) applyFilter(searchTotal, listTotal); });
        searchReceived.textProperty().addListener((o, old, val) -> { if(listReceived!=null) applyFilter(searchReceived, listReceived); });
        searchIssued.textProperty().addListener((o, old, val) -> { if(listIssued!=null) applyFilter(searchIssued, listIssued); });
    }

    private <T> void applyFilter(TextField field, ObservableList<T> masterList) {
        FilteredList<T> filteredData = new FilteredList<>(masterList, p -> true);
        String filter = (field.getText() == null) ? "" : field.getText().toLowerCase();
        
        filteredData.setPredicate(item -> {
            if (filter.isEmpty()) return true;
            String content = "";
            if (item instanceof TotalAuctionInventoryDTO) {
                content = ((TotalAuctionInventoryDTO) item).getItemName();
            } else if (item instanceof ReceivedAuctionInventoryDTO) {
                ReceivedAuctionInventoryDTO dto = (ReceivedAuctionInventoryDTO) item;
                content = dto.getItemName() + " " + dto.getLetterNo() + " " + dto.getRemarks();
            } else if (item instanceof IssuedAuctionInventoryDTO) {
                IssuedAuctionInventoryDTO dto = (IssuedAuctionInventoryDTO) item;
                content = dto.getItemName() + " " + dto.getRemarks();
            }
            return content != null && content.toLowerCase().contains(filter);
        });
        
        if (masterList == listTotal) tableTotal.setItems((ObservableList<TotalAuctionInventoryDTO>) filteredData);
        else if (masterList == listReceived) tableReceived.setItems((ObservableList<ReceivedAuctionInventoryDTO>) filteredData);
        else if (masterList == listIssued) tableIssued.setItems((ObservableList<IssuedAuctionInventoryDTO>) filteredData);
    }

    private void editReceived(ReceivedAuctionInventoryDTO dto) {
        if (dto == null) return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Received Item");
        dialog.setHeaderText("Edit details for " + dto.getItemName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new javafx.geometry.Insets(20));

        TextField qtyField = new TextField(String.valueOf(dto.getQuantity()));
        TextField letterField = new TextField(dto.getLetterNo() == null ? "" : dto.getLetterNo());
        DatePicker datePicker = new DatePicker(dto.getDate() == null ? null : dto.getDate().toLocalDate());
        TextField remarksField = new TextField(dto.getRemarks() == null ? "" : dto.getRemarks());

        grid.add(new Label("Quantity:"), 0, 0); grid.add(qtyField, 1, 0);
        grid.add(new Label("Letter No:"), 0, 1); grid.add(letterField, 1, 1);
        grid.add(new Label("Date:"), 0, 2); grid.add(datePicker, 1, 2);
        grid.add(new Label("Remarks:"), 0, 3); grid.add(remarksField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                int q = Integer.parseInt(qtyField.getText().trim());
                Date d = datePicker.getValue() == null ? null : Date.valueOf(datePicker.getValue());
                receivedRepo.updateAuctionReceived(dto.getId(), q, letterField.getText().trim(), d, remarksField.getText().trim());
                DisplayAlert.showInfo("Success", "Updated successfully");
                loadReceived();
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Update failed: " + e.getMessage());
            }
        }
    }

    private void deleteReceived(ReceivedAuctionInventoryDTO dto) {
        if (dto == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete received item: " + dto.getItemName() + "?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                int count = receivedRepo.deleteAuctionReceived(dto.getId());
                if (count > 0) {
                    DisplayAlert.showInfo("Success", "Deleted successfully");
                    loadReceived();
                } else {
                    DisplayAlert.showError("Error", "Could not delete");
                }
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Delete failed: " + e.getMessage());
            }
        }
    }

    private void editIssued(IssuedAuctionInventoryDTO dto) {
        if (dto == null) return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Issued Item");
        dialog.setHeaderText("Edit details for " + dto.getItemName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new javafx.geometry.Insets(20));

        TextField qtyField = new TextField(String.valueOf(dto.getIssuedQuantity()));
        DatePicker datePicker = new DatePicker(dto.getIssuedDate() == null ? null : dto.getIssuedDate().toLocalDate());
        TextField remarksField = new TextField(dto.getRemarks() == null ? "" : dto.getRemarks());

        grid.add(new Label("Quantity:"), 0, 0); grid.add(qtyField, 1, 0);
        grid.add(new Label("Issued Date:"), 0, 1); grid.add(datePicker, 1, 1);
        grid.add(new Label("Remarks:"), 0, 2); grid.add(remarksField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                int q = Integer.parseInt(qtyField.getText().trim());
                Date d = datePicker.getValue() == null ? null : Date.valueOf(datePicker.getValue());
                issuedRepo.updateAuctionIssued(dto.getId(), q, d, remarksField.getText().trim());
                DisplayAlert.showInfo("Success", "Updated successfully");
                loadIssued();
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Update failed: " + e.getMessage());
            }
        }
    }

    private void deleteIssued(IssuedAuctionInventoryDTO dto) {
        if (dto == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete issued item: " + dto.getItemName() + "?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                int count = issuedRepo.deleteAuctionIssued(dto.getId());
                if (count > 0) {
                    DisplayAlert.showInfo("Success", "Deleted successfully");
                    loadIssued();
                } else {
                    DisplayAlert.showError("Error", "Could not delete");
                }
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Delete failed: " + e.getMessage());
            }
        }
    }
}
