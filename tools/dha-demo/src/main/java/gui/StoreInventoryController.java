package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import repository.ReceivedRepository;
import repository.StoreInventoryRepository;
import repository.StoreIssuedRepository;
import dto.store.StoreInventoryDTO;
import dto.store.ReceivedInventoryDTO;
import dto.store.IssuedInventoryDTO;

import java.util.Date;

public class StoreInventoryController {

    // Toggle Buttons
    @FXML private ToggleButton btnStore;
    @FXML private ToggleButton btnReceived;
    @FXML private ToggleButton btnTemporary;
    @FXML private ToggleButton btnPermanent;
    private ToggleGroup tabGroup;

    // VBoxes for Views
    @FXML private VBox viewStore;
    @FXML private VBox viewReceived;
    @FXML private VBox viewTemporary;
    @FXML private VBox viewPermanent;

    // --- STORE TAB ---
    @FXML private TextField searchStore;
    @FXML private TableView<StoreInventoryDTO> tableStore;
    @FXML private TableColumn<StoreInventoryDTO, String> colStoreItem;
    @FXML private TableColumn<StoreInventoryDTO, Integer> colStoreTotal;
    @FXML private TableColumn<StoreInventoryDTO, Integer> colStoreInStore;
    @FXML private TableColumn<StoreInventoryDTO, Integer> colStoreAssigned;

    // --- RECEIVED TAB ---
    @FXML private TextField searchReceived;
    @FXML private TableView<ReceivedInventoryDTO> tableReceived;
    @FXML private TableColumn<ReceivedInventoryDTO, String> colRecItem;
    @FXML private TableColumn<ReceivedInventoryDTO, Integer> colRecQty;
    @FXML private TableColumn<ReceivedInventoryDTO, String> colRecFrom;
    @FXML private TableColumn<ReceivedInventoryDTO, String> colRecDetail;
    @FXML private TableColumn<ReceivedInventoryDTO, Date> colRecDate;
    @FXML private TableColumn<ReceivedInventoryDTO, Integer> colRecPrice;
    @FXML private TableColumn<ReceivedInventoryDTO, String> colRecRemarks;
    @FXML private TableColumn<ReceivedInventoryDTO, Void> colRecAction;

    // --- TEMPORARY TAB ---
    @FXML private TextField searchTemporary;
    @FXML private TableView<IssuedInventoryDTO> tableTemporary;
    @FXML private TableColumn<IssuedInventoryDTO, String> colTempItem;
    @FXML private TableColumn<IssuedInventoryDTO, Integer> colTempIssuedQty;
    @FXML private TableColumn<IssuedInventoryDTO, Integer> colTempReturnQty;
    @FXML private TableColumn<IssuedInventoryDTO, Integer> colTempRemainingQty;
    @FXML private TableColumn<IssuedInventoryDTO, String> colTempName;
    @FXML private TableColumn<IssuedInventoryDTO, String> colTempDesig;
    @FXML private TableColumn<IssuedInventoryDTO, String> colTempBranch;
    @FXML private TableColumn<IssuedInventoryDTO, String> colTempRegNo;
    @FXML private TableColumn<IssuedInventoryDTO, Date> colTempIssuedDate;
    @FXML private TableColumn<IssuedInventoryDTO, Date> colTempReturnDate;
    @FXML private TableColumn<IssuedInventoryDTO, String> colTempRemarks;
    @FXML private TableColumn<IssuedInventoryDTO, Void> colTempAction;

    // --- PERMANENT TAB ---
    @FXML private TextField searchPermanent;
    @FXML private TableView<IssuedInventoryDTO> tablePermanent;
    @FXML private TableColumn<IssuedInventoryDTO, String> colPermItem;
    @FXML private TableColumn<IssuedInventoryDTO, Integer> colPermQty;
    @FXML private TableColumn<IssuedInventoryDTO, String> colPermName;
    @FXML private TableColumn<IssuedInventoryDTO, String> colPermDesig;
    @FXML private TableColumn<IssuedInventoryDTO, String> colPermBranch;
    @FXML private TableColumn<IssuedInventoryDTO, String> colPermRegNo;
    @FXML private TableColumn<IssuedInventoryDTO, Date> colPermDate;
    @FXML private TableColumn<IssuedInventoryDTO, String> colPermRemarks;
    @FXML private TableColumn<IssuedInventoryDTO, Void> colPermAction;

    // Repositories
    private StoreInventoryRepository storeRepo;
    private ReceivedRepository receivedRepo;
    private StoreIssuedRepository issuedRepo;

    // Data Lists (for filtering)
    private ObservableList<StoreInventoryDTO> listStore;
    private ObservableList<ReceivedInventoryDTO> listReceived;
    private ObservableList<IssuedInventoryDTO> listTemporary;
    private ObservableList<IssuedInventoryDTO> listPermanent;

    public void initialize() {
        storeRepo = new StoreInventoryRepository();
        receivedRepo = new ReceivedRepository();
        issuedRepo = new StoreIssuedRepository();

        setupToggleGroup();
        setupColumns();
        setupSearch();
        
        // Initial load
        handleTabSwitch(null); // Load default (Store)
    }

    private void setupToggleGroup() {
        tabGroup = new ToggleGroup();
        btnStore.setToggleGroup(tabGroup);
        btnReceived.setToggleGroup(tabGroup);
        btnTemporary.setToggleGroup(tabGroup);
        btnPermanent.setToggleGroup(tabGroup);
        // Force Store selected if none
        if (tabGroup.getSelectedToggle() == null) btnStore.setSelected(true);
    }

    private void setupColumns() {
        // Store
        colStoreItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colStoreTotal.setCellValueFactory(new PropertyValueFactory<>("totalItem"));
        colStoreInStore.setCellValueFactory(new PropertyValueFactory<>("inStore"));
        colStoreAssigned.setCellValueFactory(new PropertyValueFactory<>("assigned"));

        // Received
        colRecItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colRecQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRecFrom.setCellValueFactory(new PropertyValueFactory<>("receivedFrom"));
        colRecDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));
        colRecDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colRecPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colRecRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        setupActionColumn(colRecAction, "Received");

        // Temporary
        colTempItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        // Show issued quantity as (IssuedQty + ReturnedQty) in Temporary tab
        colTempIssuedQty.setCellValueFactory(cellData -> {
            IssuedInventoryDTO dto = (IssuedInventoryDTO) cellData.getValue();
            int issued = dto == null ? 0 : dto.getIssuedQuantity();
            int returned = dto == null ? 0 : dto.getReturnQuantity();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(issued);
        });
        colTempReturnQty.setCellValueFactory(new PropertyValueFactory<>("returnQuantity"));
        // Remaining should display IssuedQty - ReturnedQty (clamped to 0)
        colTempRemainingQty.setCellValueFactory(cellData -> {
            IssuedInventoryDTO dto = (IssuedInventoryDTO) cellData.getValue();
            int issued = dto == null ? 0 : dto.getIssuedQuantity();
            int returned = dto == null ? 0 : dto.getReturnQuantity();
            int remaining = Math.max(0, issued - returned);
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(remaining);
        });
        colTempName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTempDesig.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colTempBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        colTempRegNo.setCellValueFactory(new PropertyValueFactory<>("registerNo"));
        colTempIssuedDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        colTempReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colTempRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        setupActionColumn(colTempAction, "Temporary");

        // Permanent
        colPermItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colPermQty.setCellValueFactory(new PropertyValueFactory<>("issuedQuantity")); // Use issuedQuantity field
        colPermName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPermDesig.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colPermBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        colPermRegNo.setCellValueFactory(new PropertyValueFactory<>("registerNo"));
        colPermDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        colPermRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        setupActionColumn(colPermAction, "Permanent");
    }

    private <S> void setupActionColumn(TableColumn<S, Void> column, String type) {
        column.setCellFactory(param -> new TableCell<S, Void>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(10, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("action-btn-success");
                btnDelete.getStyleClass().add("action-btn-danger");
                pane.setAlignment(javafx.geometry.Pos.CENTER);

                // Implement Edit/Delete for Received rows
                if ("Received".equalsIgnoreCase(type)) {
                    btnEdit.setOnAction(e -> {
                        try {
                            @SuppressWarnings("unchecked")
                            dto.store.ReceivedInventoryDTO dto = (dto.store.ReceivedInventoryDTO) getTableView().getItems().get(getIndex());
                            if (dto == null) return;

                            // Build dialog to edit fields (except ItemName)
                            Dialog<ButtonType> dialog = new Dialog<>();
                            dialog.setTitle("Edit Received Item");
                            dialog.setHeaderText("Edit received details for " + dto.getItemName());

                            ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                            ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

                            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                            grid.setHgap(10);
                            grid.setVgap(10);
                            grid.setPadding(new javafx.geometry.Insets(20));

                            TextField qtyField = new TextField(String.valueOf(dto.getQuantity()));
                            TextField priceField = new TextField(dto.getUnitPrice() == null ? "" : String.valueOf(dto.getUnitPrice()));
                            TextField remarksField = new TextField(dto.getRemarks() == null ? "" : dto.getRemarks());
                            DatePicker datePicker = new DatePicker(dto.getDate() == null ? null : dto.getDate().toLocalDate());

                            // Note: ReceivedFrom is not editable as requested; show as read-only label
                            Label rfLabel = new Label(dto.getReceivedFrom());

                            grid.add(new Label("Quantity:"), 0, 0);
                            grid.add(qtyField, 1, 0);
                            grid.add(new Label("Unit Price:"), 0, 1);
                            grid.add(priceField, 1, 1);
                            grid.add(new Label("Remarks:"), 0, 2);
                            grid.add(remarksField, 1, 2);
                            grid.add(new Label("Receive Date:"), 0, 3);
                            grid.add(datePicker, 1, 3);
                            grid.add(new Label("Received From:"), 0, 4);
                            grid.add(rfLabel, 1, 4);

                            dialog.getDialogPane().setContent(grid);

                            java.util.Optional<ButtonType> res = dialog.showAndWait();
                            if (res.isPresent() && res.get() == saveBtn) {
                                try {
                                    int newQty = Integer.parseInt(qtyField.getText().trim());
                                    Integer newPrice = priceField.getText().trim().isEmpty() ? null : Integer.parseInt(priceField.getText().trim());
                                    String newRemarks = remarksField.getText().trim();
                                    java.sql.Date newDate = datePicker.getValue() == null ? null : java.sql.Date.valueOf(datePicker.getValue());

                                    // ReceivedFrom is not editable; use value present in DTO
                                    int currentReceivedFromId = dto.getReceivedFromId();
                                    if (currentReceivedFromId <= 0) {
                                        DisplayAlert.showError("Error", "Invalid ReceivedFromID");
                                        return;
                                    }
                                    int updated = receivedRepo.updateStoreReceived(dto.getId(), newQty, newPrice, newRemarks, currentReceivedFromId, newDate);
                                    if (updated > 0) {
                                        DisplayAlert.showInfo("Updated", "Received row updated successfully");
                                        loadReceivedData();
                                    } else {
                                        DisplayAlert.showError("Error", "No rows were updated");
                                    }
                                } catch (NumberFormatException ex) {
                                    DisplayAlert.showError("Validation", "Quantity and Unit Price must be valid numbers");
                                } catch (Exception ex) {
                                    DisplayAlert.showError("Error", "Could not update: " + ex.getMessage());
                                }
                            }
                        } catch (Exception ex) {
                            DisplayAlert.showError("Error", "Could not open edit dialog: " + ex.getMessage());
                        }
                    });

                    btnDelete.setOnAction(e -> {
                        try {
                            @SuppressWarnings("unchecked")
                            dto.store.ReceivedInventoryDTO dto = (dto.store.ReceivedInventoryDTO) getTableView().getItems().get(getIndex());
                            if (dto == null) return;
                            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
                            conf.setTitle("Confirm Delete");
                            conf.setHeaderText(null);
                            conf.setContentText("Are you sure you want to delete received row for '" + dto.getItemName() + "'?");
                            java.util.Optional<ButtonType> r = conf.showAndWait();
                            if (r.isPresent() && r.get() == ButtonType.OK) {
                                int del = receivedRepo.deleteStoreReceived(dto.getId());
                                if (del > 0) {
                                    DisplayAlert.showInfo("Deleted", "Row deleted");
                                    loadReceivedData();
                                } else {
                                    DisplayAlert.showError("Error", "No rows deleted");
                                }
                            }
                        } catch (Exception ex) {
                            DisplayAlert.showError("Error", "Could not delete: " + ex.getMessage());
                        }
                    });

                } else if ("Temporary".equalsIgnoreCase(type) || "Permanent".equalsIgnoreCase(type)) {
                    btnEdit.setOnAction(e -> {
                        try {
                            @SuppressWarnings("unchecked")
                            dto.store.IssuedInventoryDTO dto = (dto.store.IssuedInventoryDTO) getTableView().getItems().get(getIndex());
                            if (dto == null) return;

                            // Dialog to edit issued row
                            Dialog<ButtonType> dialog = new Dialog<>();
                            dialog.setTitle("Edit Issued Item");
                            dialog.setHeaderText("Edit issued details for " + dto.getItemName());

                            ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                            ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

                            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
                            grid.setHgap(10);
                            grid.setVgap(10);
                            grid.setPadding(new javafx.geometry.Insets(20));

                            TextField qtyField = new TextField(String.valueOf(dto.getIssuedQuantity()));
                            DatePicker datePicker = new DatePicker(dto.getIssuedDate() == null ? null : dto.getIssuedDate().toLocalDate());
                            TextField regField = new TextField(dto.getRegisterNo() == null ? "" : dto.getRegisterNo());
                            TextField remarksField = new TextField(dto.getRemarks() == null ? "" : dto.getRemarks());

                            // Employee dropdown (select by DHAID)
                            repository.DHAEmployeeRepository empRepo = new repository.DHAEmployeeRepository();
                            javafx.collections.ObservableList<repository.DHAEmployeeRepository.DHAEmployeeItem> empItems = javafx.collections.FXCollections.observableArrayList(empRepo.findAll());
                            ComboBox<repository.DHAEmployeeRepository.DHAEmployeeItem> empCombo = new ComboBox<>(empItems);
                            empCombo.setPromptText("Select employee");
                            for (repository.DHAEmployeeRepository.DHAEmployeeItem it : empItems) {
                                if (it.getDhaId().equals(dto.getDhaId())) { empCombo.getSelectionModel().select(it); break; }
                            }

                            grid.add(new Label("Quantity:"), 0, 0);
                            grid.add(qtyField, 1, 0);
                            grid.add(new Label("Employee:"), 0, 1);
                            grid.add(empCombo, 1, 1);
                            grid.add(new Label("Register No:"), 0, 2);
                            grid.add(regField, 1, 2);
                            grid.add(new Label("Issued Date:"), 0, 3);
                            grid.add(datePicker, 1, 3);
                            grid.add(new Label("Remarks:"), 0, 4);
                            grid.add(remarksField, 1, 4);

                            dialog.getDialogPane().setContent(grid);

                            java.util.Optional<ButtonType> res = dialog.showAndWait();
                            if (res.isPresent() && res.get() == saveBtn) {
                                try {
                                    int newQty = Integer.parseInt(qtyField.getText().trim());
                                    repository.DHAEmployeeRepository.DHAEmployeeItem sel = empCombo.getValue();
                                    String selDhaId = sel == null ? null : sel.getDhaId();
                                    String newReg = regField.getText().trim();
                                    java.sql.Date newDate = datePicker.getValue() == null ? null : java.sql.Date.valueOf(datePicker.getValue());
                                    String newRemarks = remarksField.getText().trim();

                                    // For Temporary and Permanent: use StoreIssuedRepository.updateStoreIssued
                                    int updated = issuedRepo.updateStoreIssued(dto.getId(), newQty, selDhaId, newReg, newDate, newRemarks);
                                    if (updated > 0) {
                                        DisplayAlert.showInfo("Updated", "Issued row updated successfully");
                                        if ("Temporary".equalsIgnoreCase(type)) loadTemporaryData(); else loadPermanentData();
                                    } else {
                                        DisplayAlert.showError("Error", "No rows were updated");
                                    }
                                } catch (NumberFormatException ex) {
                                    DisplayAlert.showError("Validation", "Quantity must be a valid number");
                                } catch (Exception ex) {
                                    DisplayAlert.showError("Error", "Could not update issued row: " + ex.getMessage());
                                }
                            }
                        } catch (Exception ex) {
                            DisplayAlert.showError("Error", "Could not open edit dialog: " + ex.getMessage());
                        }
                    });

                    btnDelete.setOnAction(e -> {
                        try {
                            @SuppressWarnings("unchecked")
                            dto.store.IssuedInventoryDTO dto = (dto.store.IssuedInventoryDTO) getTableView().getItems().get(getIndex());
                            if (dto == null) return;
                            Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
                            conf.setTitle("Confirm Delete");
                            conf.setHeaderText(null);
                            conf.setContentText("Are you sure you want to delete issued row for '" + dto.getItemName() + "'?");
                            java.util.Optional<ButtonType> r = conf.showAndWait();
                            if (r.isPresent() && r.get() == ButtonType.OK) {
                                int del = issuedRepo.deleteStoreIssued(dto.getId());
                                if (del > 0) {
                                    DisplayAlert.showInfo("Deleted", "Issued row deleted");
                                    if ("Temporary".equalsIgnoreCase(type)) loadTemporaryData(); else loadPermanentData();
                                } else {
                                    DisplayAlert.showError("Error", "No rows deleted");
                                }
                            }
                        } catch (Exception ex) {
                            DisplayAlert.showError("Error", "Could not delete issued row: " + ex.getMessage());
                        }
                    });
                } else {
                    btnDelete.setOnAction(e -> DisplayAlert.showInfo("Info", "Delete not implemented yet"));
                }
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    @FXML
    private void handleTabSwitch(ActionEvent event) {
        // Reset Views
        viewStore.setVisible(false);
        viewReceived.setVisible(false);
        viewTemporary.setVisible(false);
        viewPermanent.setVisible(false);

        if (btnStore.isSelected()) {
            viewStore.setVisible(true);
            loadStoreData();
        } else if (btnReceived.isSelected()) {
            viewReceived.setVisible(true);
            loadReceivedData();
        } else if (btnTemporary.isSelected()) {
            viewTemporary.setVisible(true);
            loadTemporaryData();
        } else if (btnPermanent.isSelected()) {
            viewPermanent.setVisible(true);
            loadPermanentData();
        }
    }

    private void loadStoreData() {
        listStore = FXCollections.observableArrayList(storeRepo.getInventoryOverview());
        tableStore.setItems(listStore);
        applyFilter(searchStore, listStore, tableStore);
    }

    private void loadReceivedData() {
        listReceived = FXCollections.observableArrayList(receivedRepo.getAllReceivedItems());
        tableReceived.setItems(listReceived);
        applyFilter(searchReceived, listReceived, tableReceived);
    }

    private void loadTemporaryData() {
        // 1 = Temporary
        listTemporary = FXCollections.observableArrayList(issuedRepo.getIssuedItems(1));
        tableTemporary.setItems(listTemporary);
        applyFilter(searchTemporary, listTemporary, tableTemporary);
    }

    private void loadPermanentData() {
        // 2 = Permanent
        listPermanent = FXCollections.observableArrayList(issuedRepo.getIssuedItems(2));
        tablePermanent.setItems(listPermanent);
        applyFilter(searchPermanent, listPermanent, tablePermanent);
    }

    private <T> void setupSearch() {
        // Listeners for search fields are set in applyFilter usually? 
        // Or we can attach listeners here.
        searchStore.textProperty().addListener((o, old, val) -> { if(listStore!=null) applyFilter(searchStore, listStore, tableStore); });
        searchReceived.textProperty().addListener((o, old, val) -> { if(listReceived!=null) applyFilter(searchReceived, listReceived, tableReceived); });
        searchTemporary.textProperty().addListener((o, old, val) -> { if(listTemporary!=null) applyFilter(searchTemporary, listTemporary, tableTemporary); });
        searchPermanent.textProperty().addListener((o, old, val) -> { if(listPermanent!=null) applyFilter(searchPermanent, listPermanent, tablePermanent); });
    }

    private <T> void applyFilter(TextField field, ObservableList<T> masterList, TableView<T> table) {
        FilteredList<T> filteredData = new FilteredList<>(masterList, p -> true);
        String lowerCaseFilter = (field.getText() == null) ? "" : field.getText().toLowerCase();
        
        filteredData.setPredicate(item -> {
            if (lowerCaseFilter.isEmpty()) return true;
            
            // Generic simple search (toString or specific fields)
            // Ideally we check specific fields.
            // For simplicity, converting DTO to lowercase string
            
            String content = "";
            if (item instanceof StoreInventoryDTO) {
                content = ((StoreInventoryDTO) item).getItemName();
            } else if (item instanceof ReceivedInventoryDTO) {
                ReceivedInventoryDTO dto = (ReceivedInventoryDTO) item;
                content = dto.getItemName() + " " + dto.getDetail() + " " + dto.getReceivedFrom();
            } else if (item instanceof IssuedInventoryDTO) {
                IssuedInventoryDTO dto = (IssuedInventoryDTO) item;
                content = dto.getItemName() + " " + dto.getName() + " " + dto.getRegisterNo();
            }
            
            return content != null && content.toLowerCase().contains(lowerCaseFilter);
        });
        
        table.setItems(filteredData);
    }

}
