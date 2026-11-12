package gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import repository.DHAEmployeeRepository;
import repository.IssueRepository;
import repository.ReceivedRepository;
import repository.UserReceivedRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the "Assign User Item" screen.
 * Refactored to support PO-based filtering and bulk assignment.
 */
public class AssignUserItemController {

    @FXML
    private ComboBox<String> poComboBox;

    @FXML
    private TableView<UserItemRow> itemsTable;
    @FXML
    private TableColumn<UserItemRow, Boolean> selectCol;
    @FXML
    private TableColumn<UserItemRow, String> itemCol;
    @FXML
    private TableColumn<UserItemRow, Integer> qtyCol;
    @FXML
    private TableColumn<UserItemRow, String> empCol;
    @FXML
    private TableColumn<UserItemRow, String> dhaCol;
    @FXML
    private TableColumn<UserItemRow, String> poCol;

    @FXML
    private DatePicker issueDatePicker;

    @FXML
    private TextField remarksField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final UserReceivedRepository userReceivedRepository = new UserReceivedRepository();
    private final IssueRepository issueRepository = new IssueRepository();
    private final ReceivedRepository receivedRepository = new ReceivedRepository();
    private final DHAEmployeeRepository dhaEmployeeRepository = new DHAEmployeeRepository();
    private final repository.BranchRepository branchRepository = new repository.BranchRepository();

    private ObservableList<UserItemRow> tableData = FXCollections.observableArrayList();

    public void initialize() {
        try {
            // Load distinct POs
            List<String> pos = userReceivedRepository.findDistinctPendingPOs();
            poComboBox.setItems(FXCollections.observableArrayList(pos));
            
            // Allow typing/search in ComboBox? Standard ComboBox is fine for now.
            // Setup Table
            selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
            selectCol.setEditable(true);

            itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            empCol.setCellValueFactory(new PropertyValueFactory<>("empName"));
            dhaCol.setCellValueFactory(new PropertyValueFactory<>("dhaId"));
            poCol.setCellValueFactory(new PropertyValueFactory<>("poNo"));

            itemsTable.setItems(tableData);
            itemsTable.setEditable(true); // Needed for CheckBox

            // Listener for PO selection
            poComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadItemsForPO(newVal);
                } else {
                    tableData.clear();
                }
            });

        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not initialize data: " + e.getMessage());
            e.printStackTrace();
        }

        issueDatePicker.setValue(LocalDate.now());
    }

    private void loadItemsForPO(String poNo) {
        tableData.clear();
        try {
            List<UserReceivedRepository.UserItem> items = userReceivedRepository.findPendingByPO(poNo);
            
            for (UserReceivedRepository.UserItem item : items) {
                String empName = "";
                
                // Determine display name (Employee or Branch)
                if (item.getDhaId() != null) {
                    empName = "DHA:" + item.getDhaId();
                    try {
                        var emp = dhaEmployeeRepository.findById(item.getDhaId());
                        if (emp != null) {
                            empName = emp.getEmpName();
                            if (emp.getDesignation() != null && !emp.getDesignation().isEmpty()) {
                                empName += " (" + emp.getDesignation() + ")";
                            }
                        }
                    } catch (Exception ex) { /* ignore */ }
                } else if (item.getBranchId() != null) {
                    try {
                        var branch = branchRepository.findById(item.getBranchId());
                        if (branch != null) {
                            empName = "Branch: " + branch.getBranchName();
                        } else {
                            empName = "Branch ID: " + item.getBranchId();
                        }
                    } catch (Exception ex) { /* ignore */ }
                } else {
                    empName = "Unknown Recipient";
                }
                
                tableData.add(new UserItemRow(item, empName));
            }
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load items for PO: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (issueDatePicker.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Issue Date is required");
                return;
            }
            Date issueDate = Date.valueOf(issueDatePicker.getValue());
            String remarks = remarksField.getText() == null ? "" : remarksField.getText().trim();

            List<UserItemRow> selectedRows = tableData.stream()
                    .filter(UserItemRow::isSelected)
                    .collect(Collectors.toList());

            if (selectedRows.isEmpty()) {
                DisplayAlert.showError("Validation Error", "No items selected for assignment");
                return;
            }

            // Process Items
            List<UserItemRow> processedRows = new ArrayList<>();
            for (UserItemRow row : selectedRows) {
                boolean success = processAssignment(row, issueDate, remarks);
                if (success) {
                    processedRows.add(row);
                }
            }

            if (processedRows.isEmpty()) {
                return; // Errors happened and shown, or nothing done
            }

            // Success Message
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Saved");
            info.setHeaderText(null);
            info.setContentText("Issued " + processedRows.size() + " items successfully.");
            info.showAndWait();

            // Generate Vouchers (Group by Effective ID)
            Map<String, List<UserItemRow>> byUser = processedRows.stream()
                    .collect(Collectors.groupingBy(UserItemRow::getGroupingKey));

            for (Map.Entry<String, List<UserItemRow>> entry : byUser.entrySet()) {
                openPrintViewForGroup(entry.getKey(), entry.getValue(), issueDate, remarks);
            }

            // Refresh List (Remove processed items from table)
            // We can reload from DB or removing from list. Reloading is safer to ensure state consistency.
            String currentPO = poComboBox.getValue();
            if (currentPO != null) {
                loadItemsForPO(currentPO);
            }

            // Clear remarks? Maybe keep date.
            remarksField.clear();

        } catch (Exception e) {
            DisplayAlert.showError("Error", "Error during save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean processAssignment(UserItemRow row, Date issueDate, String remarks) {
        try {
            int issuedId = issueRepository.addUserIssued(row.getData().getId(), issueDate, remarks);
            if (issuedId <= 0) {
                DisplayAlert.showError("Error", "Could not save user issued for item: " + row.getItemName());
                return false;
            }
            int updated = receivedRepository.updateUserReceivedStatus(row.getData().getId(), 1);
            if (updated <= 0) {
                issueRepository.deleteUserIssuedById(issuedId); // Rollback
                DisplayAlert.showError("Error", "Could not update status for item: " + row.getItemName());
                return false;
            }
            row.setIssuedId(issuedId); // Store for print
            return true;
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Exception saving item " + row.getItemName() + ": " + e.getMessage());
            return false;
        }
    }

    private void openPrintViewForGroup(String groupingKey, List<UserItemRow> rows, Date issueDate, String remarks) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/print_voucher.fxml"));
            AnchorPane pane = loader.load();
            PrintVoucherController controller = loader.getController();

            // Prepare Data
            String poNo = rows.get(0).getPoNo();
            String cleanName = "";
            String dhaId = "";
            String branch = "";
            
            if (groupingKey.startsWith("BRANCH:")) {
                // Handle Branch
                try {
                    int branchId = Integer.parseInt(groupingKey.substring(7));
                    var br = branchRepository.findById(branchId);
                    if (br != null) {
                        cleanName = "Branch User"; // Generic name for branch
                        dhaId = br.getBranchName(); // Show Branch name in DHA ID field? Or Branch field? 
                        // The print controller expects receiverDha and branchName.
                        // Usually DHA ID is for employee ID. 
                        // Let's passed Branch Name as "dhaId" slot if allowed, or put in Branch Name slot.
                        branch = br.getBranchName();
                        dhaId = br.getBranchName(); 
                    }
                } catch (Exception e) { /* ignore */ }
            } else {
                // Handle Employee (DHA ID)
                dhaId = groupingKey;
                try {
                    var emp = dhaEmployeeRepository.findById(dhaId);
                    if (emp != null) {
                        cleanName = emp.getEmpName();
                        branch = emp.getBranchName();
                    } else {
                         // Fallback to row name if repo find fails?
                         cleanName = rows.get(0).getEmpName().split("\\(")[0].trim();
                    }
                } catch (Exception e) { /* ignore */ }
            }

            List<PrintVoucherController.VoucherItem> voucherItems = new ArrayList<>();
            int sr = 1;
            for (UserItemRow r : rows) {
                voucherItems.add(new PrintVoucherController.VoucherItem(
                        sr++, 
                        r.getItemName(), 
                        r.getQuantity(), 
                        remarks // Using the batch remarks
                ));
            }

            int refId = rows.get(0).getIssuedId();

            controller.setSocketDataList(refId, poNo, voucherItems, cleanName, dhaId, branch);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Print Voucher - " + dhaId);
            stage.setScene(new Scene(pane));
            stage.show();

        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not open print view for " + groupingKey + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/assign_item.fxml")); // Back to menu
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
            DisplayAlert.showError("Error", "Could not return to menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Row Model
    public static class UserItemRow {
        private final UserReceivedRepository.UserItem data;
        private final BooleanProperty selected;
        private final String empName;
        private int issuedId;

        public UserItemRow(UserReceivedRepository.UserItem data, String empName) {
            this.data = data;
            this.selected = new SimpleBooleanProperty(false);
            this.empName = empName;
        }

        public UserReceivedRepository.UserItem getData() { return data; }
        
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean v) { selected.set(v); }
        public BooleanProperty selectedProperty() { return selected; }

        public String getItemName() { return data.getItemName(); }
        public int getQuantity() { return data.getQuantity(); }
        public String getDhaId() { return data.getDhaId(); }
        public String getPoNo() { return data.getPurchaseOrderNo(); }
        public String getEmpName() { return empName; }

        public void setIssuedId(int id) { this.issuedId = id; }
        public int getIssuedId() { return issuedId; }
        
        // Key for grouping vouchers
        public String getGroupingKey() {
            if (data.getDhaId() != null) return data.getDhaId();
            if (data.getBranchId() != null) return "BRANCH:" + data.getBranchId();
            return "UNKNOWN";
        }
    }
}
