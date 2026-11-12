package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class LowStockController {
    @FXML
    private AnchorPane contentPane;

    @FXML
    private javafx.scene.layout.HBox selectionPane;

    @FXML
    private AnchorPane root;

    public void initialize() {
        // Start with selection view visible, content hidden
        selectionPane.setVisible(true);
        contentPane.setVisible(false);
    }

    @FXML
    private void handleSelectionAdd() {
        loadContent("/view/low_stock_add.fxml");
    }

    @FXML
    private void handleSelectionView() {
        loadContent("/view/low_stock_view.fxml");
    }

    private void loadContent(String fxmlPath) {
        try {
            contentPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();
            
            // allow child controllers to access this controller
            // Set userData on the content container so child views can lookup the parent controller
            contentPane.setUserData(this);

            pane.setPrefWidth(contentPane.getWidth());
            pane.setPrefHeight(contentPane.getHeight());
            
            // Bind dimensions to expand with window
            pane.prefWidthProperty().bind(contentPane.widthProperty());
            pane.prefHeightProperty().bind(contentPane.heightProperty());

            contentPane.getChildren().add(pane);
            
            selectionPane.setVisible(false);
            contentPane.setVisible(true);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Called by child controllers to return to main selection
    public void showSelection() {
        contentPane.getChildren().clear();
        contentPane.setVisible(false);
        selectionPane.setVisible(true);
    }

    // Kept for compatibility if called externally, but redirects to proper flow
    @FXML
    private void handleAdd() {
        handleSelectionAdd();
    }

    @FXML
    public void handleView() {
        handleSelectionView();
    }

    public void openAdd(String itemName, Integer limit) {
        try {
            contentPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/low_stock_add.fxml"));
            AnchorPane pane = loader.load();
            pane.setUserData(this);
            
            Object controller = loader.getController();
            if (controller instanceof LowStockAddController) {
                ((LowStockAddController) controller).setInitialData(itemName, limit);
            }
            
            pane.setPrefWidth(contentPane.getWidth());
            pane.setPrefHeight(contentPane.getHeight());
             // Bind dimensions
            pane.prefWidthProperty().bind(contentPane.widthProperty());
            pane.prefHeightProperty().bind(contentPane.heightProperty());
            
            contentPane.getChildren().add(pane);
            
            selectionPane.setVisible(false);
            contentPane.setVisible(true);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not open Add Low Stock page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
