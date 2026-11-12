package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import repository.DHAEmployeeRepository;
import repository.StoreIssuedRepository;
import repository.StoreReturnRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReturnItemController {
    @FXML
    private ComboBox<String> itemNameComboBox;

    @FXML
    private ComboBox<DHAEmployeeRepository.DHAEmployeeItem> branchComboBox;

    @FXML
    private TextField issueQuantityField;

    @FXML
    private TextField returnQuantityField;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private TextField remarksField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final StoreIssuedRepository storeIssuedRepository = new StoreIssuedRepository();
    private final StoreReturnRepository storeReturnRepository = new StoreReturnRepository();
    private final DHAEmployeeRepository dhaEmployeeRepository = new DHAEmployeeRepository();

    private StoreIssuedRepository.StoreIssuedItem selectedStoreIssuedItem;

    // map the ComboBox entry wrapper -> StoreIssuedItem so selection reliably maps even when DHAID is missing
    private final Map<DHAEmployeeRepository.DHAEmployeeItem, StoreIssuedRepository.StoreIssuedItem> dhaToIssuedMap = new HashMap<>();

    public void initialize() {
        // Set default date to today
        returnDatePicker.setValue(LocalDate.now());

        // Load Item Names from StoreIssued with IssueType='Temporary'
        try {
            ObservableList<String> itemNames = FXCollections.observableArrayList(
                    storeIssuedRepository.getTemporaryItemNames());
            itemNameComboBox.setItems(itemNames);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load item names: " + e.getMessage());
        }

        // When Item Name is selected, load employees for that item
        itemNameComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadEmployeesForItem(newVal);
            } else {
                branchComboBox.getItems().clear();
                issueQuantityField.clear();
                returnQuantityField.clear();
                selectedStoreIssuedItem = null;
                dhaToIssuedMap.clear();
            }
        });

        // When employee is selected, show the quantity
        branchComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                StoreIssuedRepository.StoreIssuedItem it = dhaToIssuedMap.get(newVal);
                if (it != null) {
                    selectedStoreIssuedItem = it;
                    issueQuantityField.setText(String.valueOf(it.getQuantity()));
                    returnQuantityField.clear();
                } else {
                    selectedStoreIssuedItem = null;
                    issueQuantityField.clear();
                    returnQuantityField.clear();
                }
            } else {
                issueQuantityField.clear();
                returnQuantityField.clear();
                selectedStoreIssuedItem = null;
            }
        });
    }

    private void loadEmployeesForItem(String itemName) {
        try {
            ObservableList<StoreIssuedRepository.StoreIssuedItem> issued =
                    FXCollections.observableArrayList(
                            storeIssuedRepository.getTemporaryItemsByItemName(itemName));

            // Diagnostic log: print issued rows
            System.out.println("loadEmployeesForItem: found " + issued.size() + " issued rows for item '" + itemName + "'");
            for (StoreIssuedRepository.StoreIssuedItem si : issued) {
                System.out.println("  StoreIssued ID=" + si.getId() + ", DHAID=" + si.getDhaId() + ", EmpName='" + si.getEmpName() + "', RegisterNo='" + si.getRegisterNo() + "', Qty=" + si.getQuantity());
            }

            // Build map from DHAID to the latest StoreIssuedItem (or first) and build employee list
            dhaToIssuedMap.clear();
            ObservableList<DHAEmployeeRepository.DHAEmployeeItem> employees = FXCollections.observableArrayList();
            for (StoreIssuedRepository.StoreIssuedItem si : issued) {
                String dhaId = si.getDhaId();
                String empName = si.getEmpName();
                String desig = si.getDesignation();
                String displayName;
                if (empName != null && !empName.isEmpty()) {
                    displayName = dhaId + " - " + empName + (desig != null && !desig.isEmpty() ? " (" + desig + ")" : "");
                } else {
                    // fallback: use registerNo or Issued# id
                    displayName = si.getRegisterNo() == null ? ("Issued#" + si.getId()) : si.getRegisterNo();
                }
                // create DHAEmployeeItem wrapper (empName may be empty). Use displayName as empName when needed.
                // StoreIssuedItem now returns String for dhaId.
                String wrapDhaId = (dhaId == null || dhaId.isEmpty()) ? "ISSUED-" + si.getId() : dhaId;
                String wrapName = (empName == null || empName.isEmpty()) ? displayName : empName;
                String wrapDesig = desig == null ? "" : desig;
                // Branch info not available on StoreIssuedItem; use placeholder branchId=-1 and empty branchName
                DHAEmployeeRepository.DHAEmployeeItem wrap = new DHAEmployeeRepository.DHAEmployeeItem(wrapDhaId, wrapName, -1, "", wrapDesig, 0);
                employees.add(wrap);
                // map wrapper object to the store issued item for reliable selection mapping
                dhaToIssuedMap.put(wrap, si);
            }

            // Set a simple cell factory to ensure the ComboBox shows the toString() (name + designation)
            branchComboBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(DHAEmployeeRepository.DHAEmployeeItem item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            });
            branchComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DHAEmployeeRepository.DHAEmployeeItem item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            });

            branchComboBox.setItems(employees);
            branchComboBox.getSelectionModel().clearSelection();
            if (employees.isEmpty()) {
                branchComboBox.setPromptText("No employees found");
            } else {
                branchComboBox.setPromptText("Select employee");
            }
            issueQuantityField.clear();
            returnQuantityField.clear();
            selectedStoreIssuedItem = null;
        } catch (Exception e) {
            // Log and fallback to empty list instead of showing an error dialog
            System.err.println("ReturnItemController.loadEmployeesForItem failed: " + e.getMessage());
            branchComboBox.setItems(FXCollections.observableArrayList());
            branchComboBox.getSelectionModel().clearSelection();
            issueQuantityField.clear();
            returnQuantityField.clear();
            selectedStoreIssuedItem = null;
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validate required fields
            if (itemNameComboBox.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Item Name is required");
                return;
            }
            if (branchComboBox.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Employee is required");
                return;
            }
            if (returnQuantityField.getText().trim().isEmpty()) {
                DisplayAlert.showError("Validation Error", "Return Quantity is required");
                return;
            }
            if (returnDatePicker.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Return Date is required");
                return;
            }

            // Validate return quantity
            int returnQuantity;
            try {
                returnQuantity = Integer.parseInt(returnQuantityField.getText().trim());
            } catch (NumberFormatException ex) {
                DisplayAlert.showError("Validation Error", "Return Quantity must be a valid number");
                return;
            }

            if (returnQuantity <= 0) {
                DisplayAlert.showError("Validation Error", "Return Quantity must be greater than zero");
                return;
            }

            if (selectedStoreIssuedItem == null) {
                DisplayAlert.showError("Validation Error", "Please select an employee");
                return;
            }

            int issueQuantity = selectedStoreIssuedItem.getQuantity();
            if (returnQuantity > issueQuantity) {
                DisplayAlert.showError("Validation Error",
                        "Return Quantity cannot be greater than Issue Quantity (" + issueQuantity + ")");
                return;
            }

            // Get values
            int storeIssueId = selectedStoreIssuedItem.getId();
            Date returnDate = Date.valueOf(returnDatePicker.getValue());
            String remarks = remarksField.getText() == null ? "" : remarksField.getText().trim();

            // Save to StoreReturn/Reture table
            int id = storeReturnRepository.addReturnWithRemarks(
                    storeIssueId,
                    returnQuantity,
                    returnDate,
                    remarks
            );

            if (id <= 0) {
                DisplayAlert.showError("Error", "Could not save return (database error)");
                return;
            }

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Saved");
            info.setHeaderText(null);
            info.setContentText("Item returned successfully");
            info.showAndWait();

            handleCancel();
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not save return: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Clear all fields
            itemNameComboBox.getSelectionModel().clearSelection();
            branchComboBox.getItems().clear();
            issueQuantityField.clear();
            returnQuantityField.clear();
            returnDatePicker.setValue(LocalDate.now());
            remarksField.clear();
            selectedStoreIssuedItem = null;
            dhaToIssuedMap.clear();
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not clear form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
