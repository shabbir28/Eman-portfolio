package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import repository.AuctionInventoryRepository;
import repository.ReceivedRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AddAuctionItemController {
    @FXML
    private ComboBox<String> itemNameComboBox;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField letterNoField;
    @FXML
    private DatePicker receiveDatePicker;
    @FXML
    private TextField remarksField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private ReceivedRepository receivedRepository = new ReceivedRepository();
    private AuctionInventoryRepository auctionInventoryRepository = new AuctionInventoryRepository();

    public void initialize() {
        // Set default date to today
        receiveDatePicker.setValue(LocalDate.now());

        // Load Item Names from AuctionInventory dropdown
        try {
            ObservableList<String> itemNames =
                FXCollections.observableArrayList(
                    auctionInventoryRepository.findAll()
                        .stream()
                        .map(item -> item.getItemName())
                        .collect(Collectors.toList())
                );
            itemNameComboBox.setItems(itemNames);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load Item Names: " + e.getMessage());
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
            if (receiveDatePicker.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Receive Date is required");
                return;
            }

            // Parse inputs
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String letterNo = letterNoField.getText().trim();
            Date receiveDate = Date.valueOf(receiveDatePicker.getValue());
            String remarks = remarksField.getText().trim();

            // Save to AuctionReceived table
            int auctionId = receivedRepository.addAuctionReceived(itemName, quantity, letterNo, receiveDate, remarks);
            
            if (auctionId <= 0) {
                DisplayAlert.showError("Error", "Could not save auction received (database error)");
                return;
            }

            // Show success message
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Saved");
            info.setHeaderText(null);
            info.setContentText("Auction received saved successfully");
            info.showAndWait();
            
            // Return to add item page
            handleCancel();

        } catch (NumberFormatException e) {
            DisplayAlert.showError("Validation Error", "Quantity must be a valid number");
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not save auction received: " + e.getMessage());
            e.printStackTrace();
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

