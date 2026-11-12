package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import repository.StoreInventoryRepository;
import repository.LowStockRepository;

public class LowStockViewController {
    @FXML
    private TableView<repository.LowStockRepository.LowStockItem> tableView;
    @FXML
    private TableColumn<repository.LowStockRepository.LowStockItem, String> colItem;
    @FXML
    private TableColumn<repository.LowStockRepository.LowStockItem, Integer> colLimit;
    @FXML
    private TableColumn<repository.LowStockRepository.LowStockItem, Void> colAction;
    @FXML
    private javafx.scene.control.TextField searchField;

    private final repository.LowStockRepository lowStockRepository = new repository.LowStockRepository();
    private final StoreInventoryRepository inventoryRepository = new StoreInventoryRepository();

    public void initialize() {
        colItem.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colLimit.setCellValueFactory(new PropertyValueFactory<>("limit"));

        loadData();

        // Search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue));

        Callback<TableColumn<repository.LowStockRepository.LowStockItem, Void>, TableCell<repository.LowStockRepository.LowStockItem, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnAdd = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            {
                btnAdd.getStyleClass().add("action-btn-success");
                btnDelete.getStyleClass().add("action-btn-danger");

                btnAdd.setOnAction((ActionEvent event) -> {
                    repository.LowStockRepository.LowStockItem data = getTableView().getItems().get(getIndex());
                    try {
                        AnchorPane content = (AnchorPane) getTableView().getScene().lookup("#contentPane");
                        if (content != null && content.getUserData() instanceof LowStockController) {
                            ((LowStockController) content.getUserData()).openAdd(data.getItemName(), data.getLimit());
                        } else {
                            DisplayAlert.showInfo("Edit", "Could not open editor for: " + data.getItemName());
                        }
                    } catch (Exception ex) {
                        DisplayAlert.showError("Error", "Could not open edit page: " + ex.getMessage());
                    }
                });

                btnDelete.setOnAction((ActionEvent event) -> {
                    repository.LowStockRepository.LowStockItem data = getTableView().getItems().get(getIndex());
                    try {
                        lowStockRepository.deleteByItemName(data.getItemName());
                        loadData();
                    } catch (Exception e) {
                        DisplayAlert.showError("Error", "Could not delete: " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, btnAdd, btnDelete);
                    box.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(box);
                }
            }
        };

        colAction.setCellFactory(cellFactory);
    }

    @FXML
    private javafx.scene.control.Button backButton;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            AnchorPane content = (AnchorPane) tableView.getScene().lookup("#contentPane");
            if (content != null && content.getUserData() instanceof LowStockController) {
                ((LowStockController) content.getUserData()).showSelection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // No-arg handler overload to satisfy FXML references that call the method without passing ActionEvent
    @FXML
    private void handleBack() {
        handleBack((ActionEvent) null);
    }

    private ObservableList<repository.LowStockRepository.LowStockItem> masterData = FXCollections.observableArrayList();

    private void loadData() {
        masterData.setAll(lowStockRepository.findAllWithNames());
        tableView.setItems(masterData);
        filterData(searchField.getText());
    }

    private void filterData(String query) {
        if (query == null || query.isEmpty()) {
            tableView.setItems(masterData);
            return;
        }
        String lowerQuery = query.toLowerCase();
        ObservableList<repository.LowStockRepository.LowStockItem> filtered = masterData.filtered(item ->
            item.getItemName().toLowerCase().contains(lowerQuery) ||
            String.valueOf(item.getLimit()).contains(lowerQuery)
        );
        tableView.setItems(filtered);
    }
}
