package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.AuctionItem;
import model.InventoryStats;
import model.LowStockItem;
import model.PendingItem;
import service.InventoryService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    private enum ViewMode { STORE, USER, AUCTION }
    @FXML
    private Button storeButton;
    @FXML
    private Button userButton;
    @FXML
    private Button auctionButton;

    @FXML
    private TableView<Object> lowStockTable;
    @FXML
    private TableColumn<Object, String> itemNameColumn;
    @FXML
    private TableColumn<Object, String> quantityColumn;

    @FXML
    private Label totalStoreItemsLabel;
    @FXML
    private Label totalInStoreLabel;
    @FXML
    private Label totalTemporaryAssignedLabel;
    @FXML
    private Label totalPermanentAssignedLabel;
    @FXML
    private Label cardTitleLabel;
    @FXML
    private VBox metricCard1;
    @FXML
    private VBox metricCard2;
    @FXML
    private VBox metricCard3;
    @FXML
    private VBox metricCard4;

    private InventoryService inventoryService;
    private ViewMode currentMode = ViewMode.STORE;

    public void initialize() {
        inventoryService = new InventoryService();
        configureLowStockTable();
        configureMetricCards();
        applyView(ViewMode.STORE);
        setActiveFilter(storeButton);
    }

    private void configureLowStockTable() {
        lowStockTable.setColumnResizePolicy(resolveConstrainedPolicy());
        lowStockTable.setPlaceholder(new Label("All items are sufficiently stocked."));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
    }

    // Add inline styles to ensure metric cards look boxed and colored even if CSS isn't applied.
    private void configureMetricCards() {
        String base = "-fx-background-radius: 20; -fx-padding: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10,0,0,6);";
        String[] gradients = new String[]{
                // Gradient A: dark -> light diagonal
                "-fx-background-color: linear-gradient(to bottom right, #0E035F 0%, #A4A0C3 100%);",
                // Gradient B: light -> dark diagonal (reverse)
                "-fx-background-color: linear-gradient(to bottom right, #A4A0C3 0%, #0E035F 100%);",
                // Gradient C: dark center highlight
                "-fx-background-color: linear-gradient(to bottom right, #0E035F 0%, #0E035F 55%, #A4A0C3 100%);",
                // Gradient D: soft banding from light to dark to light
                "-fx-background-color: linear-gradient(to bottom right, #A4A0C3 0%, #0E035F 50%, #A4A0C3 100%);"
        };

        List<VBox> cards = Arrays.asList(metricCard1, metricCard2, metricCard3, metricCard4);
        for (int i = 0; i < cards.size(); i++) {
            VBox card = cards.get(i);
            if (card == null) continue; // defensive
            card.setStyle(base + gradients[i % gradients.length]);

            // style the numeric value (first child) and label (second child) if present
            if (card.getChildren().size() > 0 && card.getChildren().get(0) instanceof Label) {
                Label value = (Label) card.getChildren().get(0);
                value.setStyle("-fx-font-size:36px; -fx-font-weight:700; -fx-text-fill: white;");
            }
            if (card.getChildren().size() > 1 && card.getChildren().get(1) instanceof Label) {
                Label title = (Label) card.getChildren().get(1);
                title.setStyle("-fx-font-size:14px; -fx-text-fill: white;");
            }
        }
    }

    private void applyView(ViewMode mode) {
        currentMode = mode;
        switch (mode) {
            case STORE -> {
                cardTitleLabel.setText("Low Stock");
                itemNameColumn.setText("Item Name");
                quantityColumn.setText("Quantity");
                quantityColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
                itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                List<LowStockItem> lowStockItems = inventoryService.getLowStockItems();
                System.out.println("[DEBUG] lowStockItems size=" + lowStockItems.size());
                ObservableList<Object> tableItems = FXCollections.observableArrayList(lowStockItems);
                lowStockTable.setItems(tableItems);

                InventoryStats stats = inventoryService.getInventoryStats();
                System.out.println("[DEBUG] InventoryStats: totalStore=" + stats.getTotalStoreItems() + " totalInStore=" + stats.getTotalInStoreItems() + " temp=" + stats.getTotalTemporaryAssigned() + " perm=" + stats.getTotalPermanentAssigned());
                updateMetrics(
                        "Total Store Item", stats.getTotalStoreItems(),
                        "Total In Store Item", stats.getTotalInStoreItems(),
                        "Total Temporary Assigned Item", stats.getTotalTemporaryAssigned(),
                        "Total Permanent Assigned Item", stats.getTotalPermanentAssigned(),
                        true
                );
            }
            case USER -> {
                cardTitleLabel.setText("Pending Item");
                itemNameColumn.setText("Item Name");
                quantityColumn.setText("Quantity");
                quantityColumn.setStyle("-fx-alignment: CENTER_LEFT;");
                itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                List<PendingItem> pendingItems = inventoryService.getPendingUserItems();
                ObservableList<Object> tableItems = FXCollections.observableArrayList(pendingItems);
                lowStockTable.setItems(tableItems);

                InventoryStats stats = inventoryService.getUserStats();
                updateMetrics(
                        "Total User Item", stats.getTotalStoreItems(),
                        "Total Issued Item", stats.getTotalInStoreItems(),
                        "Total Pending Item", stats.getTotalTemporaryAssigned(),
                        "", 0,
                        false
                );
            }
            case AUCTION -> {
                cardTitleLabel.setText("In Store Auction");
                itemNameColumn.setText("Item Name");
                quantityColumn.setText("Quantity");
                quantityColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
                itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
                quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                List<AuctionItem> auctionItems = inventoryService.getAuctionItems();
                ObservableList<Object> tableItems = FXCollections.observableArrayList(auctionItems);
                lowStockTable.setItems(tableItems);

                InventoryStats stats = inventoryService.getAuctionStats();
                updateMetrics(
                        "Total Auction Item", stats.getTotalStoreItems(),
                        "Total Auction In Store Item", stats.getTotalInStoreItems(),
                        "Total Auction Issued Item", stats.getTotalTemporaryAssigned(),
                        "", 0,
                        false
                );
            }
        }
    }

    private void updateMetrics(String label1, int value1,
                               String label2, int value2,
                               String label3, int value3,
                               String label4, int value4,
                               boolean showFourth) {
        totalStoreItemsLabel.setText(String.valueOf(value1));
        totalInStoreLabel.setText(String.valueOf(value2));
        totalTemporaryAssignedLabel.setText(String.valueOf(value3));
        totalPermanentAssignedLabel.setText(String.valueOf(value4));

        ((Label) metricCard1.getChildren().get(1)).setText(label1);
        ((Label) metricCard2.getChildren().get(1)).setText(label2);
        ((Label) metricCard3.getChildren().get(1)).setText(label3);
        ((Label) metricCard4.getChildren().get(1)).setText(label4);

        metricCard4.setManaged(showFourth);
        metricCard4.setVisible(showFourth);
    }

    @FXML
    private void handleFilterSelection(ActionEvent event) {
        Button selected = (Button) event.getSource();
        setActiveFilter(selected);
        if (selected == storeButton) {
            applyView(ViewMode.STORE);
        } else if (selected == userButton) {
            applyView(ViewMode.USER);
        } else if (selected == auctionButton) {
            applyView(ViewMode.AUCTION);
        }
    }

    private void setActiveFilter(Button activeButton) {
        List<Button> filters = Arrays.asList(storeButton, userButton, auctionButton);
        filters.forEach(button -> button.getStyleClass().remove("filter-button-active"));
        if (!activeButton.getStyleClass().contains("filter-button-active")) {
            activeButton.getStyleClass().add("filter-button-active");
        }
    }

    private Callback<TableView.ResizeFeatures, Boolean> resolveConstrainedPolicy() {
        try {
            Field field = TableView.class.getField("CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN");
            Object value = field.get(null);
            if (value instanceof Callback) {
                @SuppressWarnings("unchecked")
                Callback<TableView.ResizeFeatures, Boolean> policy =
                        (Callback<TableView.ResizeFeatures, Boolean>) value;
                return policy;
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // fallback to legacy policy below
        }
        @SuppressWarnings("deprecation")
        Callback<TableView.ResizeFeatures, Boolean> legacyPolicy = TableView.CONSTRAINED_RESIZE_POLICY;
        return legacyPolicy;
    }
}
