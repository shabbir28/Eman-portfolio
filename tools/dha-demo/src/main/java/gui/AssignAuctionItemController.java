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
import repository.AuctionInventoryRepository;
import repository.IssueRepository;
import repository.InventoryRepository;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Controller for the "Assign Auction Item" screen.
 * Uses AuctionRemaining as the source of available quantities.
 */
public class AssignAuctionItemController {

    @FXML
    private ComboBox<AuctionItem> itemNameComboBox;

    @FXML
    private TextField totalQuantityField;

    @FXML
    private TextField quantityField;

    @FXML
    private DatePicker issueDatePicker;

    @FXML
    private TextField remarksField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final InventoryRepository inventoryRepository = new InventoryRepository();
    private final AuctionInventoryRepository auctionInventoryRepository = new AuctionInventoryRepository();
    private final IssueRepository issueRepository = new IssueRepository();

    public void initialize() {
        try {
            ObservableList<AuctionItem> items = FXCollections.observableArrayList(inventoryRepository.fetchAuctionRemainingItems());
            itemNameComboBox.setItems(items);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load auction items: " + e.getMessage());
        }

        // default issue date = today
        issueDatePicker.setValue(LocalDate.now());

        // update total quantity when selection changes
        itemNameComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                totalQuantityField.setText(String.valueOf(newVal.getQuantity()));
            } else {
                totalQuantityField.clear();
            }
        });
    }

    @FXML
    private void handleSave() {
        try {
            AuctionItem selected = itemNameComboBox.getValue();
            if (selected == null) {
                DisplayAlert.showError("Validation Error", "Item Name is required");
                return;
            }
            if (quantityField.getText().trim().isEmpty()) {
                DisplayAlert.showError("Validation Error", "Quantity is required");
                return;
            }
            if (issueDatePicker.getValue() == null) {
                DisplayAlert.showError("Validation Error", "Issue Date is required");
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

            if (quantity > selected.getQuantity()) {
                DisplayAlert.showError("Validation Error", "Quantity cannot be greater than available quantity (" + selected.getQuantity() + ")");
                return;
            }

            // Resolve AuctionInventory ID by item name
            Integer auctionId = inventoryRepository.findAuctionIdByName(selected.getItemName());
            if (auctionId == null) {
                DisplayAlert.showError("Error", "Could not find auction inventory id for item: " + selected.getItemName());
                return;
            }

            Date issueDate = Date.valueOf(issueDatePicker.getValue());
            String remarks = remarksField.getText() == null ? "" : remarksField.getText().trim();

            int id = issueRepository.addAuctionIssued(auctionId, quantity, issueDate, remarks);
            if (id <= 0) {
                DisplayAlert.showError("Error", "Could not save auction issued (database error)");
                return;
            }

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Saved");
            info.setHeaderText(null);
            info.setContentText("Auction item issued successfully");
            info.showAndWait();

            handleCancel();
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not save auction issued: " + e.getMessage());
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
