package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import repository.StoreInventoryRepository;
import repository.LowStockRepository;

public class LowStockAddController {

    @FXML
    private ComboBox<StoreInventoryRepository.StoreInventoryItem> itemComboBox;

    @FXML
    private TextField limitField;

    private final StoreInventoryRepository inventoryRepository = new StoreInventoryRepository();
    private final LowStockRepository lowStockRepository = new LowStockRepository();

    public void initialize() {
        ObservableList<StoreInventoryRepository.StoreInventoryItem> items = FXCollections.observableArrayList(inventoryRepository.findAll());
        itemComboBox.setItems(items);
    }

    public void setInitialData(String itemName, Integer limit) {
        if (itemName != null) {
            for (StoreInventoryRepository.StoreInventoryItem it : itemComboBox.getItems()) {
                if (it.getItemName().equals(itemName)) {
                    itemComboBox.getSelectionModel().select(it);
                    break;
                }
            }
        }
        if (limit != null) limitField.setText(String.valueOf(limit));
    }

    @FXML
    private void handleSave() {
        try {
            var selected = itemComboBox.getValue();
            if (selected == null) { DisplayAlert.showError("Validation", "Select an item"); return; }
            int limit;
            try { limit = Integer.parseInt(limitField.getText().trim()); } catch (NumberFormatException e) { DisplayAlert.showError("Validation", "Enter valid limit"); return; }

            lowStockRepository.upsertLowStock(selected.getItemName(), limit);
            DisplayAlert.showInfo("Saved", "Low stock limit saved");
            
            // Navigate to View List
            navigateToView();
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not save low stock: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        navigateToView();
    }

    private void navigateToView() {
        try {
            // contentPane is the parent of the root of this view
            // We can also lookup by ID as before
            AnchorPane root = (AnchorPane) itemComboBox.getScene().lookup("#contentPane");
            if (root != null && root.getUserData() instanceof LowStockController) {
                ((LowStockController) root.getUserData()).showSelection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
