package gui;

import dto.UserInventoryDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import repository.UserInventoryRepository;
import java.util.function.Predicate;
import java.sql.Date;

public class UserInventoryController {

    // Toggle Buttons
    @FXML
    private ToggleButton btnTotal;
    @FXML
    private ToggleButton btnIssued;
    @FXML
    private ToggleButton btnPending;

    // VBox Views
    @FXML
    private VBox viewTotal;
    @FXML
    private VBox viewIssued;
    @FXML
    private VBox viewPending;

    // Search Fields
    @FXML
    private TextField searchTotal;
    @FXML
    private TextField searchIssued;
    @FXML
    private TextField searchPending;

    // Tables
    @FXML
    private TableView<UserInventoryDTO> tableTotal;
    @FXML
    private TableView<UserInventoryDTO> tableIssued;
    @FXML
    private TableView<UserInventoryDTO> tablePending;

    // Total Columns
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalItem;
    @FXML
    private TableColumn<UserInventoryDTO, Integer> colTotalQty;
    @FXML
    private TableColumn<UserInventoryDTO, Date> colTotalRecDate;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalPO;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalVendor;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalDHAID;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalName;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalBranch;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalWDem;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalWProv;
    @FXML
    private TableColumn<UserInventoryDTO, Date> colTotalIssuedDate;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalReason;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalStatus;
    @FXML
    private TableColumn<UserInventoryDTO, String> colTotalRemarks;

    // Issued Columns
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedItem;
    @FXML
    private TableColumn<UserInventoryDTO, Integer> colIssuedQty;
    @FXML
    private TableColumn<UserInventoryDTO, Date> colIssuedRecDate;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedPO;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedVendor;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedDHAID;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedName;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedBranch;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedWDem;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedWProv;
    @FXML
    private TableColumn<UserInventoryDTO, Date> colIssuedDate;
    @FXML
    private TableColumn<UserInventoryDTO, String> colIssuedRemarks;
    @FXML
    private TableColumn<UserInventoryDTO, Void> colIssuedAction;

    // Pending Columns
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingItem;
    @FXML
    private TableColumn<UserInventoryDTO, Integer> colPendingQty;
    @FXML
    private TableColumn<UserInventoryDTO, Date> colPendingRecDate;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingPO;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingVendor;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingDHAID;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingName;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingBranch;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingWDem;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingWProv;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingReason;
    @FXML
    private TableColumn<UserInventoryDTO, String> colPendingRemarks;
    @FXML
    private TableColumn<UserInventoryDTO, Void> colPendingAction;

    private final UserInventoryRepository repository = new UserInventoryRepository();
    private ObservableList<UserInventoryDTO> totalList = FXCollections.observableArrayList();
    private ObservableList<UserInventoryDTO> issuedList = FXCollections.observableArrayList();
    private ObservableList<UserInventoryDTO> pendingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
        handleTabSwitch(null); // Load default view (Total)
    }

    private void setupColumns() {
        // Total
        colTotalItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colTotalQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotalRecDate.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        colTotalPO.setCellValueFactory(new PropertyValueFactory<>("purchaseOrderNo"));
        colTotalVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        colTotalDHAID.setCellValueFactory(new PropertyValueFactory<>("dhaId"));
        setupDhaIdColumn(colTotalDHAID);
        colTotalName.setCellValueFactory(new PropertyValueFactory<>("empName"));
        colTotalBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        colTotalWDem.setCellValueFactory(new PropertyValueFactory<>("warrantyDemand"));
        colTotalWProv.setCellValueFactory(new PropertyValueFactory<>("warrantyProvided"));
        colTotalIssuedDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        colTotalReason.setCellValueFactory(new PropertyValueFactory<>("pendingReason"));
        colTotalStatus.setCellValueFactory(new PropertyValueFactory<>("statusName"));
        colTotalRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // Issued
        colIssuedItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colIssuedQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colIssuedRecDate.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        colIssuedPO.setCellValueFactory(new PropertyValueFactory<>("purchaseOrderNo"));
        colIssuedVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        colIssuedDHAID.setCellValueFactory(new PropertyValueFactory<>("dhaId"));
        setupDhaIdColumn(colIssuedDHAID);
        colIssuedName.setCellValueFactory(new PropertyValueFactory<>("empName"));
        colIssuedBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        colIssuedWDem.setCellValueFactory(new PropertyValueFactory<>("warrantyDemand"));
        colIssuedWProv.setCellValueFactory(new PropertyValueFactory<>("warrantyProvided"));
        colIssuedDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        colIssuedRemarks.setCellValueFactory(new PropertyValueFactory<>("issuedRemarks"));
        setupActionColumn(colIssuedAction, true);

        // Pending
        colPendingItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colPendingQty.setCellValueFactory(new PropertyValueFactory<>("quantity")); // UserReceived formatted Quantity
        colPendingRecDate.setCellValueFactory(new PropertyValueFactory<>("receivedDate"));
        colPendingPO.setCellValueFactory(new PropertyValueFactory<>("purchaseOrderNo"));
        colPendingVendor.setCellValueFactory(new PropertyValueFactory<>("vendor"));
        colPendingDHAID.setCellValueFactory(new PropertyValueFactory<>("dhaId"));
        setupDhaIdColumn(colPendingDHAID);
        colPendingName.setCellValueFactory(new PropertyValueFactory<>("empName"));
        colPendingBranch.setCellValueFactory(new PropertyValueFactory<>("branch"));
        colPendingWDem.setCellValueFactory(new PropertyValueFactory<>("warrantyDemand"));
        colPendingWProv.setCellValueFactory(new PropertyValueFactory<>("warrantyProvided"));
        colPendingReason.setCellValueFactory(new PropertyValueFactory<>("pendingReason"));
        colPendingRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        setupActionColumn(colPendingAction, false);
    }

    private void setupDhaIdColumn(TableColumn<UserInventoryDTO, String> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void setupActionColumn(TableColumn<UserInventoryDTO, Void> column, boolean isIssued) {
        Callback<TableColumn<UserInventoryDTO, Void>, TableCell<UserInventoryDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("action-btn-success");
                btnDelete.getStyleClass().add("action-btn-danger");

                btnEdit.setOnAction(event -> {
                    UserInventoryDTO dto = getTableView().getItems().get(getIndex());
                    handleEdit(dto, isIssued);
                });

                btnDelete.setOnAction(event -> {
                    UserInventoryDTO dto = getTableView().getItems().get(getIndex());
                    handleDelete(dto, isIssued);
                });
                pane.setAlignment(javafx.geometry.Pos.CENTER);
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
        };
        column.setCellFactory(cellFactory);
    }

    private void handleEdit(UserInventoryDTO dto, boolean isIssued) {
        // Placeholder for edit dialog. Real implementation would open a dialog to edit
        // fields.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit Item");
        alert.setHeaderText("Edit functionality not fully implemented");
        alert.setContentText("This would open a dialog to edit: " + dto.getItemName());
        alert.show();
    }

    private void handleDelete(UserInventoryDTO dto, boolean isIssued) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Inventory");
        alert.setHeaderText("Are you sure you want to delete this item?");
        alert.setContentText(dto.getItemName());

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (isIssued) {
                    repository.deleteIssued(dto.getUserIssuedId(), dto.getUserReceivedId());
                    loadIssued();
                } else {
                    repository.deletePending(dto.getPendingId(), dto.getUserReceivedId());
                    loadPending();
                }
            } catch (Exception e) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Error");
                err.setHeaderText("Could not delete item");
                err.setContentText(e.getMessage());
                err.show();
            }
        }
    }

    @FXML
    private void handleTabSwitch(javafx.event.ActionEvent event) {
        // Reset styles (relying on toggle group behavior or manual)
        // If they are toggle buttons in a group, it's easier. But FXML didn't specify
        // ToggleGroup.
        // I'll manually manage visibility.

        boolean showTotal = false;
        boolean showIssued = false;
        boolean showPending = false;

        // Determine which button was clicked or default
        if (event == null || event.getSource() == btnTotal) {
            showTotal = true;
            btnTotal.setSelected(true);
            btnIssued.setSelected(false);
            btnPending.setSelected(false);
            loadTotal();
        } else if (event.getSource() == btnIssued) {
            showIssued = true;
            btnTotal.setSelected(false);
            btnIssued.setSelected(true);
            btnPending.setSelected(false);
            loadIssued();
        } else if (event.getSource() == btnPending) {
            showPending = true;
            btnTotal.setSelected(false);
            btnIssued.setSelected(false);
            btnPending.setSelected(true);
            loadPending();
        }

        viewTotal.setVisible(showTotal);
        viewIssued.setVisible(showIssued);
        viewPending.setVisible(showPending);
    }

    private void loadTotal() {
        try {
            totalList.setAll(repository.getTotalInventory(searchTotal.getText()));
            tableTotal.setItems(totalList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIssued() {
        try {
            issuedList.setAll(repository.getIssuedInventory(searchIssued.getText()));
            tableIssued.setItems(issuedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPending() {
        try {
            pendingList.setAll(repository.getPendingInventory(searchPending.getText()));
            tablePending.setItems(pendingList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        searchTotal.textProperty().addListener((o, oldVal, newVal) -> loadTotal());
        searchIssued.textProperty().addListener((o, oldVal, newVal) -> loadIssued());
        searchPending.textProperty().addListener((o, oldVal, newVal) -> loadPending());
    }
}
