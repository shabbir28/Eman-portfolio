package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import repository.BranchRepository;
import repository.ReceivedFromRepository;
import repository.ReceivedRepository;
import repository.StoreInventoryRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddStoreItemController {
    @FXML
    private ComboBox<String> itemNameComboBox;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<ReceivedFromRepository.ReceivedFromItem> receivedFromComboBox;
    @FXML
    private DatePicker receiveDatePicker;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField remarksField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private ReceivedRepository receivedRepository = new ReceivedRepository();
    private ReceivedFromRepository receivedFromRepository = new ReceivedFromRepository();
    private BranchRepository branchRepository = new BranchRepository();
    private StoreInventoryRepository storeInventoryRepository = new StoreInventoryRepository();

    public void initialize() {
        // Set default date to today
        receiveDatePicker.setValue(LocalDate.now());

        // Load Item Names from StoreInventory dropdown
        try {
            ObservableList<String> itemNames = FXCollections.observableArrayList(
                    storeInventoryRepository.findAll()
                            .stream()
                            .map(item -> item.getItemName())
                            .collect(Collectors.toList()));
            itemNameComboBox.setItems(itemNames);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load Item Names: " + e.getMessage());
        }

        // Load ReceivedFrom dropdown
        try {
            ObservableList<ReceivedFromRepository.ReceivedFromItem> receivedFromItems = FXCollections
                    .observableArrayList(receivedFromRepository.findAll());
            receivedFromComboBox.setItems(receivedFromItems);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load Received From options: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Get item name from ComboBox (can be selected or manually entered)
            String itemName = itemNameComboBox.getValue();
            if (itemName == null || itemName.trim().isEmpty()) {
                // If no value selected, get from editor
                itemName = itemNameComboBox.getEditor().getText().trim();
            } else {
                itemName = itemName.trim();
            }

            // Validate required fields
            if (itemName.isEmpty()) {
                DisplayAlert.showError("Validation Error", "Item Name is required");
                return;
            }
            if (quantityField.getText().trim().isEmpty()) {
                DisplayAlert.showError("Validation Error", "Quantity is required");
                return;
            }
            if (receivedFromComboBox.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Received Form is required");
                return;
            }
            if (receiveDatePicker.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Receive Date is required");
                return;
            }

            // Parse inputs
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int receivedFromId = receivedFromComboBox.getValue().getId();
            Date receiveDate = Date.valueOf(receiveDatePicker.getValue());
            Integer unitPrice = unitPriceField.getText().trim().isEmpty() ? null
                    : Integer.parseInt(unitPriceField.getText().trim());
            String remarks = remarksField.getText().trim();

            // Save to StoreReceived table
            int storeReceivedId = receivedRepository.addStoreReceived(itemName, quantity, receivedFromId, receiveDate,
                    unitPrice, remarks);

            if (storeReceivedId <= 0) {
                DisplayAlert.showError("Error", "Could not save store received (database error)");
                return;
            }

            // Show conditional form based on ReceivedFrom type
            String typeName = receivedFromComboBox.getValue().getTypeName();
            if ("MinuteSheet".equalsIgnoreCase(typeName)) {
                showMinuteSheetForm(storeReceivedId);
            } else if ("PettyCash".equalsIgnoreCase(typeName)) {
                showPettyCashForm(storeReceivedId, itemName);
            } else if ("Replacement".equalsIgnoreCase(typeName)) {
                showReplacementForm(storeReceivedId);
            } else {
                // Success message if no additional form needed
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Saved");
                info.setHeaderText(null);
                info.setContentText("Store received saved successfully");
                info.showAndWait();
                handleCancel(); // Return to add item page
            }

        } catch (NumberFormatException e) {
            DisplayAlert.showError("Validation Error", "Quantity and Unit Price must be valid numbers");
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not save store received: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showMinuteSheetForm(int storeReceivedId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Store Item");
        dialog.setHeaderText("Minute-sheet");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        TextField poNoField = new TextField();
        poNoField.setPromptText("Enter purchase order #");
        TextField vendorField = new TextField();
        vendorField.setPromptText("Enter vendor detail");

        grid.add(new Label("PO No.:"), 0, 0);
        grid.add(poNoField, 1, 0);
        grid.add(new Label("Vendor:"), 0, 1);
        grid.add(vendorField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveBtn) {
            try {
                if (poNoField.getText().trim().isEmpty() || vendorField.getText().trim().isEmpty()) {
                    DisplayAlert.showError("Validation Error", "PO No. and Vendor are required");
                    return;
                }
                receivedRepository.addMinuteSheet(storeReceivedId, poNoField.getText().trim(),
                        vendorField.getText().trim());
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Saved");
                info.setHeaderText(null);
                info.setContentText("Store received saved successfully");
                info.showAndWait();
                handleCancel(); // Return to add item page
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Could not save minute sheet details: " + e.getMessage());
            }
        }
    }

    private void showPettyCashForm(int storeReceivedId, String itemName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Store Item");
        dialog.setHeaderText("Petty Cash");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        TextField voucherNoField = new TextField();
        voucherNoField.setPromptText("Enter voucher No.");

        grid.add(new Label("Voucher No.:"), 0, 0);
        grid.add(voucherNoField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveBtn) {
            try {
                if (voucherNoField.getText().trim().isEmpty()) {
                    DisplayAlert.showError("Validation Error", "Voucher No. is required");
                    return;
                }
                receivedRepository.addPettyCash(storeReceivedId, voucherNoField.getText().trim());

                // Success - Open Print Form
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/store_received_print.fxml"));
                    javafx.scene.Parent root = loader.load();

                    StoreReceivedPrintController controller = loader.getController();
                    controller.setItemName(itemName);

                    // Use javafx.stage.Stage (not javafx.scene.Stage)
                    javafx.stage.Stage stage = new javafx.stage.Stage();
                    stage.setTitle("Store Received Form");
                    stage.setScene(new Scene(root));
                    stage.show();

                } catch (Exception e) {
                    DisplayAlert.showError("Error", "Could not open print form: " + e.getMessage());
                    e.printStackTrace();
                }

                handleCancel(); // Return to add item page
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Could not save petty cash details: " + e.getMessage());
            }
        }
    }

    private void showReplacementForm(int storeReceivedId) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Store Item");
        dialog.setHeaderText("Replacement");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        ComboBox<BranchRepository.BranchItem> branchComboBox = new ComboBox<>();
        try {
            ObservableList<BranchRepository.BranchItem> branchItems = FXCollections
                    .observableArrayList(branchRepository.findAll());
            branchComboBox.setItems(branchItems);
            ComboBoxAutoComplete.install(branchComboBox);
            branchComboBox.setPromptText("Select branch");
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load branches: " + e.getMessage());
        }

        grid.add(new Label("Branch:"), 0, 0);
        grid.add(branchComboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveBtn) {
            try {
                if (branchComboBox.getValue() == null) {
                    DisplayAlert.showError("Validation Error", "Branch is required");
                    return;
                }
                int branchId = branchComboBox.getValue().getId();
                receivedRepository.addReplacement(storeReceivedId, branchId);
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Saved");
                info.setHeaderText(null);
                info.setContentText("Store received saved successfully");
                info.showAndWait();
                handleCancel(); // Return to add item page
            } catch (Exception e) {
                DisplayAlert.showError("Error", "Could not save replacement details: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Load the add_item.fxml back
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_item.fxml"));
            AnchorPane newContent = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            Scene scene = cancelButton.getScene();
            if (scene != null) {
                AnchorPane centerPane = (AnchorPane) scene.lookup("#centerPane");
                if (centerPane != null) {
                    // Replace content in centerPane
                    centerPane.getChildren().clear();
                    newContent.setPrefHeight(centerPane.getHeight());
                    newContent.setPrefWidth(centerPane.getWidth());
                    centerPane.getChildren().add(newContent);
                } else {
                    DisplayAlert.showError("Error", "Could not find centerPane in the scene");
                }
            }
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not return to Add Item page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
