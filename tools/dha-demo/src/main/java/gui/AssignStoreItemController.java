package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.AuctionItem;
import repository.DHAEmployeeRepository;
import repository.IssueRepository;
import repository.IssueTypeRepository;
import repository.InventoryRepository;
import repository.StoreInventoryRepository;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Controller for the "Assign Store Item" screen.
 * Uses StoreRemaining as the source of available quantities.
 */
public class AssignStoreItemController {

    @FXML
    private ComboBox<AuctionItem> itemNameComboBox; // reuse AuctionItem model to show name+quantity

    @FXML
    private TextField totalQuantityField;

    @FXML
    private TextField quantityField;

    @FXML
    private ComboBox<DHAEmployeeRepository.DHAEmployeeItem> dhaComboBox;

    @FXML
    private TextField registerNoField;

    @FXML
    private DatePicker issueDatePicker;

    @FXML
    private ComboBox<IssueTypeRepository.IssueTypeItem> issueTypeComboBox;

    @FXML
    private TextField remarksField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField dhaIdField;

    @FXML
    private TextField dhaInfoField;

    private final StoreInventoryRepository storeInventoryRepository = new StoreInventoryRepository();
    private final DHAEmployeeRepository dhaEmployeeRepository = new DHAEmployeeRepository();
    private final IssueTypeRepository issueTypeRepository = new IssueTypeRepository();
    private final IssueRepository issueRepository = new IssueRepository();
    private final InventoryRepository inventoryRepository = new InventoryRepository();

    // Track the currently available quantity (from StoreRemaining)
    private int currentAvailableQuantity = 0;

    public void initialize() {
        try {
            var remaining = inventoryRepository.fetchStoreRemainingItems();
            ObservableList<AuctionItem> availableItems = FXCollections.observableArrayList(remaining);
            itemNameComboBox.setItems(availableItems);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load store remaining items: " + e.getMessage());
        }

        try {
            ObservableList<DHAEmployeeRepository.DHAEmployeeItem> dhas =
                    FXCollections.observableArrayList(dhaEmployeeRepository.findAll());
            dhaComboBox.setItems(dhas);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load DHA employees: " + e.getMessage());
        }

        try {
            ObservableList<IssueTypeRepository.IssueTypeItem> issueTypes =
                    FXCollections.observableArrayList(issueTypeRepository.findAll());
            issueTypeComboBox.setItems(issueTypes);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load issue types: " + e.getMessage());
        }

        issueDatePicker.setValue(LocalDate.now());

        itemNameComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentAvailableQuantity = newVal.getQuantity();
                totalQuantityField.setText(String.valueOf(currentAvailableQuantity));
            } else {
                totalQuantityField.clear();
                currentAvailableQuantity = 0;
            }
        });

        // When user types a DHAID, look up employee and show name/designation; also try to select in combo
        dhaIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            dhaInfoField.clear();
            if (newVal == null || newVal.trim().isEmpty()) return;
            try {
                String dhaId = newVal.trim();
                var emp = dhaEmployeeRepository.findById(dhaId);
                if (emp != null) {
                    dhaInfoField.setText(emp.getEmpName() + (emp.getDesignation() != null && !emp.getDesignation().isEmpty() ? " (" + emp.getDesignation() + ")" : ""));
                    // try to select matching entry in combo
                    for (DHAEmployeeRepository.DHAEmployeeItem item : dhaComboBox.getItems()) {
                        if (item.getDhaId().equalsIgnoreCase(dhaId)) {
                            dhaComboBox.getSelectionModel().select(item);
                            break;
                        }
                    }
                } else {
                    dhaInfoField.setText("(Not found)");
                    dhaComboBox.getSelectionModel().clearSelection();
                }
            } catch (Exception ex) {
                dhaInfoField.setText("(Error)");
            }
        });

        // When a DHA is selected from combo, populate dhaIdField and dhaInfoField
        dhaComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                dhaIdField.setText(String.valueOf(newV.getDhaId()));
                dhaInfoField.setText(newV.getEmpName() + (newV.getDesignation() != null && !newV.getDesignation().isEmpty() ? " (" + newV.getDesignation() + ")" : ""));
            }
        });
    }

    @FXML
    private void handleSave() {
        try {
            AuctionItem selectedItem = itemNameComboBox.getValue();
            DHAEmployeeRepository.DHAEmployeeItem selectedDha = dhaComboBox.getValue();
            IssueTypeRepository.IssueTypeItem selectedIssueType = issueTypeComboBox.getValue();

            if (selectedItem == null) {
                DisplayAlert.showError("Validation Error", "Item Name is required");
                return;
            }
            if (quantityField.getText().trim().isEmpty()) {
                DisplayAlert.showError("Validation Error", "Quantity is required");
                return;
            }
            // Determine DHA ID: prefer typed DHAID if valid and exists, otherwise use combo selection
            String dhaIdToUse = null;
            String dhaIdText = dhaIdField.getText() == null ? "" : dhaIdField.getText().trim();
            if (!dhaIdText.isEmpty()) {
                if (dhaEmployeeRepository.exists(dhaIdText)) dhaIdToUse = dhaIdText;
                else {
                    DisplayAlert.showError("Validation Error", "Entered DHAID not found");
                    return;
                }
            } else if (selectedDha != null) {
                dhaIdToUse = selectedDha.getDhaId();
            }

            if (dhaIdToUse == null) {
                DisplayAlert.showError("Validation Error", "DHA is required");
                return;
            }
            if (registerNoField.getText().trim().isEmpty()) {
                DisplayAlert.showError("Validation Error", "Register No. is required");
                return;
            }
            if (issueDatePicker.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Issue Date is required");
                return;
            }
            if (selectedIssueType == null) {
                DisplayAlert.showError("Validation Error", "Issue Type is required");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException ex) {
                DisplayAlert.showError("Validation Error", "Quantity must be a valid number");
                return;
            }

            if (quantity <= 0) {
                DisplayAlert.showError("Validation Error", "Quantity must be greater than zero");
                return;
            }

            if (quantity > currentAvailableQuantity) {
                DisplayAlert.showError("Validation Error", "Quantity cannot be greater than available quantity (" + currentAvailableQuantity + ")");
                return;
            }

            Date issueDate = Date.valueOf(issueDatePicker.getValue());
            String remarks = remarksField.getText() == null ? "" : remarksField.getText().trim();

            // Resolve StoreInventory ID by item name
            Integer storeInvId = storeInventoryRepository.findIdByName(selectedItem.getItemName());
            if (storeInvId == null) {
                DisplayAlert.showError("Error", "Could not find StoreInventory id for item: " + selectedItem.getItemName());
                return;
            }

            int id = issueRepository.addStoreIssued(
                    storeInvId,
                    quantity,
                    dhaIdToUse,
                    issueDate,
                    selectedIssueType.getId(),
                    registerNoField.getText().trim(),
                    remarks
            );

            if (id <= 0) {
                DisplayAlert.showError("Error", "Could not save store issued (database error)");
                return;
            }

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Saved");
            info.setHeaderText(null);
            info.setContentText("Store item issued successfully");
            info.showAndWait();

            handleCancel();
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not save store issued: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/assign_item.fxml"));
            AnchorPane newContent = loader.load();

            Scene scene = cancelButton.getScene();
            if (scene != null) {
                AnchorPane centerPane = (AnchorPane) scene.lookup("#centerPane");
                if (centerPane != null) {
                    centerPane.getChildren().clear();
                    newContent.setPrefHeight(centerPane.getHeight());
                    newContent.setPrefWidth(centerPane.getWidth());
                    centerPane.getChildren().add(newContent);
                } else {
                    DisplayAlert.showError("Error", "Could not find centerPane in the scene");
                }
            }
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not return to Assign Item page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

