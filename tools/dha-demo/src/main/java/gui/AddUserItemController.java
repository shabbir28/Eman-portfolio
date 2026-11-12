package gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import repository.BranchRepository;
import repository.DHAEmployeeRepository;
import repository.ReceivedRepository;
import repository.UserStatusRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddUserItemController {

    // Header Fields
    @FXML
    private RadioButton rbDhaId;
    @FXML
    private RadioButton rbBranch;
    @FXML
    private Label sourceLabel;

    @FXML
    private TextField dhaIdField;
    @FXML
    private ComboBox<BranchRepository.BranchItem> branchComboBox;

    @FXML
    private TextField poNoField;
    @FXML
    private TextField vendorField;
    @FXML
    private DatePicker receiveDatePicker;
    @FXML
    private ComboBox<UserStatusRepository.UserStatusItem> statusComboBox;

    // Items Details
    @FXML
    private TableView<UserItemDTO> itemsTable;
    @FXML
    private TableColumn<UserItemDTO, String> colItemName;
    @FXML
    private TableColumn<UserItemDTO, Integer> colQuantity;
    @FXML
    private TableColumn<UserItemDTO, String> colWarrantyDemand;
    @FXML
    private TableColumn<UserItemDTO, String> colWarrantyProvided;
    @FXML
    private TableColumn<UserItemDTO, String> colRemarks;
    @FXML
    private TableColumn<UserItemDTO, Void> colAction;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final ReceivedRepository receivedRepository = new ReceivedRepository();
    private final UserStatusRepository userStatusRepository = new UserStatusRepository();
    private final DHAEmployeeRepository dhaEmployeeRepository = new DHAEmployeeRepository();
    private final BranchRepository branchRepository = new BranchRepository();

    private final ObservableList<UserItemDTO> itemsList = FXCollections.observableArrayList();
    private final ToggleGroup sourceGroup = new ToggleGroup();

    public void initialize() {
        // Source Selection Logic
        rbDhaId.setToggleGroup(sourceGroup);
        rbBranch.setToggleGroup(sourceGroup);

        // Initial State (RB selected in FXML is rbDhaId, ensuring listener sync)
        handleSourceChange();
        sourceGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> handleSourceChange());

        // Load Branches
        try {
            List<BranchRepository.BranchItem> branches = branchRepository.findAll();
            branchComboBox.setItems(FXCollections.observableArrayList(branches));
            ComboBoxAutoComplete.install(branchComboBox);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load branches: " + e.getMessage());
        }

        // Set default date to today
        receiveDatePicker.setValue(LocalDate.now());

        // Load UserStatus dropdown
        loadStatusOptions();

        // Setup Table
        setupTableColumns();
        itemsTable.setItems(itemsList);
    }

    private void handleSourceChange() {
        boolean isBranch = rbBranch.isSelected();
        dhaIdField.setVisible(!isBranch);
        branchComboBox.setVisible(isBranch);

        if (isBranch) {
            sourceLabel.setText("Branch:");
            // Clear DHA ID field so it doesn't cause confusion, but maybe keep it if user
            // switches back?
            // dhaIdField.clear();
        } else {
            sourceLabel.setText("DHA ID:");
            // branchComboBox.getSelectionModel().clearSelection();
        }
    }

    private void loadStatusOptions() {
        try {
            ObservableList<UserStatusRepository.UserStatusItem> statusItems = FXCollections
                    .observableArrayList(userStatusRepository.findAll());
            statusComboBox.setItems(statusItems);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load Status options: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colWarrantyDemand.setCellValueFactory(new PropertyValueFactory<>("warrantyDemand"));
        colWarrantyProvided.setCellValueFactory(new PropertyValueFactory<>("warrantyProvided"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        // Delete Action
        Callback<TableColumn<UserItemDTO, Void>, TableCell<UserItemDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnDelete = new Button("❌");
            {
                btnDelete.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold; -fx-cursor: hand;");
                btnDelete.setTooltip(new Tooltip("Remove Item"));
                btnDelete.setOnAction(event -> {
                    UserItemDTO item = getTableView().getItems().get(getIndex());
                    itemsList.remove(item);
                    updateHeaderLockState();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    @FXML
    private void handleAddItem() {
        Dialog<UserItemDTO> dialog = new Dialog<>();
        dialog.setTitle("Add Item Details");
        dialog.setHeaderText("Enter Item Details");

        ButtonType addBtnType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        TextField wDemField = new TextField();
        wDemField.setPromptText("Warranty Demand");
        TextField wProvField = new TextField();
        wProvField.setPromptText("Warranty Provided");
        TextField remField = new TextField();
        remField.setPromptText("Remarks (Optional)");

        grid.add(new Label("Item Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(qtyField, 1, 1);
        grid.add(new Label("Warranty Demand:"), 0, 2);
        grid.add(wDemField, 1, 2);
        grid.add(new Label("Warranty Provided:"), 0, 3);
        grid.add(wProvField, 1, 3);
        grid.add(new Label("Remarks:"), 0, 4);
        grid.add(remField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtnType) {
                String name = nameField.getText().trim();
                String qtyStr = qtyField.getText().trim();
                if (name.isEmpty() || qtyStr.isEmpty()) {
                    return null;
                }
                try {
                    int qty = Integer.parseInt(qtyStr);
                    if (qty <= 0)
                        return null;
                    return new UserItemDTO(name, qty, wDemField.getText().trim(), wProvField.getText().trim(),
                            remField.getText().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<UserItemDTO> result = dialog.showAndWait();
        result.ifPresent(itemsList::add);

        updateHeaderLockState();
    }

    private void updateHeaderLockState() {
        boolean hasItems = !itemsList.isEmpty();
        rbDhaId.setDisable(hasItems);
        rbBranch.setDisable(hasItems);
        dhaIdField.setDisable(hasItems);
        branchComboBox.setDisable(hasItems);
        poNoField.setDisable(hasItems);
        vendorField.setDisable(hasItems);
        receiveDatePicker.setDisable(hasItems);
        statusComboBox.setDisable(hasItems);
    }

    @FXML
    private void handleSave() {
        try {
            // 1. Validate Header
            if (!validateHeader())
                return;

            // 2. Validate Items
            if (itemsList.isEmpty()) {
                DisplayAlert.showError("Validation Error", "At least one item must be added.");
                return;
            }

            // 3. Resolve and Validate Destination (DHA ID or Branch)
            String finalDhaId = null;
            Integer finalBranchId = null;
            DHAEmployeeRepository.DHAEmployeeItem employee = null; // For print view info

            if (rbBranch.isSelected()) {
                BranchRepository.BranchItem selectedBranch = branchComboBox.getValue();
                if (selectedBranch == null) {
                    DisplayAlert.showError("Validation Error", "Please select a Branch.");
                    return;
                }
                finalBranchId = selectedBranch.getId();

                // For printing, we mock an employee object or handle differently
                // We'll create a dummy employee object just for passing to print view clearly
                employee = new DHAEmployeeRepository.DHAEmployeeItem(
                        selectedBranch.getBranchName(), // dhaId displayed as Branch Name
                        "Branch User", // Name
                        selectedBranch.getId(),
                        selectedBranch.getBranchName(),
                        "Branch",
                        0);
            } else {
                String dhaId = dhaIdField.getText().trim();
                if (dhaId.isEmpty()) {
                    DisplayAlert.showError("Validation Error", "DHA ID is required.");
                    return;
                }
                employee = dhaEmployeeRepository.findById(dhaId);
                if (employee == null) {
                    DisplayAlert.showError("Error", "DHA ID Incorrect or not found");
                    return;
                }
                finalDhaId = dhaId;
            }

            // 4. Capture Header Data
            String poNo = poNoField.getText().trim();
            String vendor = vendorField.getText().trim();
            Date receiveDate = Date.valueOf(receiveDatePicker.getValue());
            int statusId = statusComboBox.getValue().getId();
            String statusName = statusComboBox.getValue().getStatusName();

            // 5. Final Step logic (Issued Date vs Reason)
            Date issuedDate = null;
            String pendingReason = "None";

            if ("Issued".equalsIgnoreCase(statusName)) {
                issuedDate = promptForIssuedDate();
                if (issuedDate == null)
                    return;
            } else if ("Pending".equalsIgnoreCase(statusName)) {
                pendingReason = promptForReason();
                if (pendingReason == null)
                    return;
            }

            // 6. Loop and Save
            List<Integer> savedIds = new ArrayList<>();

            for (UserItemDTO item : itemsList) {
                int userId = receivedRepository.addUserReceived(
                        item.getItemName(), item.getQuantity(), poNo, vendor, receiveDate,
                        finalDhaId, finalBranchId, item.getWarrantyDemand(), item.getWarrantyProvided(), statusId,
                        pendingReason);

                if (userId > 0) {
                    savedIds.add(userId);
                    if ("Issued".equalsIgnoreCase(statusName) && issuedDate != null) {
                        saveToIssuedTable(userId, issuedDate);
                    }
                }
            }

            if (savedIds.size() == itemsList.size()) {
                if ("Issued".equalsIgnoreCase(statusName) && issuedDate != null && !savedIds.isEmpty()) {
                    int firstIssueId = savedIds.get(0);
                    openPrintView(firstIssueId, poNo, itemsList, employee);
                }

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Saved");
                info.setHeaderText(null);
                info.setContentText("All items saved successfully!");
                info.showAndWait();

                handleCancel();
            } else {
                DisplayAlert.showError("Warning",
                        "Some items may not have been saved. Saved count: " + savedIds.size() + "/" + itemsList.size());
            }

        } catch (Exception e) {
            DisplayAlert.showError("Error", "Save failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateHeader() {
        // Source validation handled inside handleSave for specificity
        if (poNoField.getText().trim().isEmpty()) {
            DisplayAlert.showError("Validation Error", "PO No. is required");
            return false;
        }
        if (vendorField.getText().trim().isEmpty()) {
            DisplayAlert.showError("Validation Error", "Vendor is required");
            return false;
        }
        if (receiveDatePicker.getValue() == null) {
            DisplayAlert.showError("Validation Error", "Receiving Date is required");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            DisplayAlert.showError("Validation Error", "Status is required");
            return false;
        }
        return true;
    }

    private Date promptForIssuedDate() {
        Dialog<Date> dialog = new Dialog<>();
        dialog.setTitle("Issue Date");
        dialog.setHeaderText("Enter Issue Date for All Items");

        ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        DatePicker dp = new DatePicker(LocalDate.now());
        dialog.getDialogPane().setContent(new HBox(10, new Label("Issue Date:"), dp));

        dialog.setResultConverter(b -> {
            if (b == okBtn && dp.getValue() != null)
                return Date.valueOf(dp.getValue());
            return null;
        });

        Optional<Date> res = dialog.showAndWait();
        return res.orElse(null);
    }

    private String promptForReason() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Pending Reason");
        dialog.setHeaderText("Enter Pending Reason for All Items");
        dialog.setContentText("Reason:");
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent() && !res.get().trim().isEmpty())
            return res.get().trim();
        return null;
    }

    private int saveToIssuedTable(int userId, Date issuedDate) {
        String sql = "INSERT INTO Issued (UserID, IssuedDate) VALUES (?, ?)";
        Connection conn = database.ConnectionFactory.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDate(2, issuedDate);
            ps.executeUpdate();
            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    return keys.getInt(1);
            }
            return -1;
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert into Issued table", e);
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_item.fxml"));
            AnchorPane newContent = loader.load();
            Scene scene = cancelButton.getScene();
            if (scene != null) {
                AnchorPane centerPane = (AnchorPane) scene.lookup("#centerPane");
                if (centerPane != null) {
                    centerPane.getChildren().clear();
                    newContent.setPrefHeight(centerPane.getHeight());
                    newContent.setPrefWidth(centerPane.getWidth());
                    centerPane.getChildren().add(newContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPrintView(int issueId, String poNo, List<UserItemDTO> items,
            DHAEmployeeRepository.DHAEmployeeItem employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/print_voucher.fxml"));
            AnchorPane pane = loader.load();

            PrintVoucherController controller = loader.getController();

            String receiverName = employee != null ? employee.getEmpName() : "";
            // If it's a branch "fake" employee, ID might be the Branch Name.
            String receiverDha = employee != null ? employee.getDhaId() : "";
            String branchName = employee != null ? employee.getBranchName() : "";

            List<PrintVoucherController.VoucherItem> voucherItems = new ArrayList<>();
            int sr = 1;
            for (UserItemDTO uItem : items) {
                voucherItems.add(new PrintVoucherController.VoucherItem(
                        sr++,
                        uItem.getItemName(),
                        uItem.getQuantity(),
                        uItem.getRemarks() == null ? "" : uItem.getRemarks()));
            }

            controller.setSocketDataList(issueId, poNo, voucherItems, receiverName, receiverDha, branchName);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Print Voucher");
            stage.setScene(new Scene(pane));
            stage.show();

        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not open print view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inner DTO Class
    public static class UserItemDTO {
        private final SimpleStringProperty itemName;
        private final SimpleIntegerProperty quantity;
        private final SimpleStringProperty warrantyDemand;
        private final SimpleStringProperty warrantyProvided;
        private final SimpleStringProperty remarks;

        public UserItemDTO(String itemName, int quantity, String warrantyDemand, String warrantyProvided,
                String remarks) {
            this.itemName = new SimpleStringProperty(itemName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.warrantyDemand = new SimpleStringProperty(warrantyDemand);
            this.warrantyProvided = new SimpleStringProperty(warrantyProvided);
            this.remarks = new SimpleStringProperty(remarks);
        }

        public String getItemName() {
            return itemName.get();
        }

        public int getQuantity() {
            return quantity.get();
        }

        public String getWarrantyDemand() {
            return warrantyDemand.get();
        }

        public String getWarrantyProvided() {
            return warrantyProvided.get();
        }

        public String getRemarks() {
            return remarks.get();
        }
    }
}
