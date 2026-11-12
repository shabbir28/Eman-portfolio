package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import repository.DHAEmployeeRepository;
import repository.DHAEmployeeRepository.DHAEmployeeItem;

public class DHAEmployeeViewController {

    @FXML private TableView<DHAEmployeeItem> tableView;
    @FXML private TableColumn<DHAEmployeeItem, String> colDhaId; // Changed from Integer to String
    @FXML private TableColumn<DHAEmployeeItem, String> colName;
    @FXML private TableColumn<DHAEmployeeItem, String> colBranch;
    @FXML private TableColumn<DHAEmployeeItem, String> colDesignation;
    @FXML private TableColumn<DHAEmployeeItem, Integer> colScale;
    @FXML private TableColumn<DHAEmployeeItem, Void> colAction;
    @FXML private TextField searchField;

    private final DHAEmployeeRepository repository = new DHAEmployeeRepository();
    private ObservableList<DHAEmployeeItem> masterData = FXCollections.observableArrayList();

    public void initialize() {
        colDhaId.setCellValueFactory(new PropertyValueFactory<>("dhaId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("empName"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colScale.setCellValueFactory(new PropertyValueFactory<>("scale"));

        // Action Buttons
        Callback<TableColumn<DHAEmployeeItem, Void>, TableCell<DHAEmployeeItem, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit"); 
            private final Button btnDelete = new Button("Delete");
            
            {
                btnEdit.getStyleClass().add("action-btn-success");
                btnDelete.getStyleClass().add("action-btn-danger");

                btnEdit.setOnAction((ActionEvent event) -> {
                    DHAEmployeeItem data = getTableView().getItems().get(getIndex());
                    handleEdit(data);
                });

                btnDelete.setOnAction((ActionEvent event) -> {
                    DHAEmployeeItem data = getTableView().getItems().get(getIndex());
                    handleDelete(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, btnEdit, btnDelete);
                    box.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(box);
                }
            }
        };
        colAction.setCellFactory(cellFactory);

        // Search
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData(newVal));

        loadData();
    }

    private void loadData() {
        masterData.setAll(repository.findAll());
        tableView.setItems(masterData);
        filterData(searchField.getText());
    }

    private void filterData(String query) {
        if (query == null || query.isEmpty()) {
            tableView.setItems(masterData);
            return;
        }
        String lower = query.toLowerCase();
        ObservableList<DHAEmployeeItem> filtered = masterData.filtered(item ->
            (item.getDhaId() != null && item.getDhaId().toLowerCase().contains(lower)) ||
            (item.getEmpName() != null && item.getEmpName().toLowerCase().contains(lower)) ||
            (item.getBranchName() != null && item.getBranchName().toLowerCase().contains(lower)) ||
            (item.getDesignation() != null && item.getDesignation().toLowerCase().contains(lower)) ||
            String.valueOf(item.getScale()).contains(lower)
        );
        tableView.setItems(filtered);
    }

    private void handleEdit(DHAEmployeeItem item) {
        try {
            AnchorPane content = (AnchorPane) tableView.getScene().lookup("#contentPane");
            if (content != null && content.getUserData() instanceof DHAEmployeeController) {
                ((DHAEmployeeController) content.getUserData()).openAdd(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(DHAEmployeeItem item) {
        if (DisplayAlert.showConfirmation("Delete", "Are you sure you want to delete Employee ID: " + item.getDhaId() + "?")) {
            repository.delete(item.getDhaId());
            loadData();
        }
    }

    @FXML
    private void handleBack() {
         try {
            AnchorPane content = (AnchorPane) tableView.getScene().lookup("#contentPane");
            if (content != null && content.getUserData() instanceof DHAEmployeeController) {
                ((DHAEmployeeController) content.getUserData()).showSelection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
