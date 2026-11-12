package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class DHAEmployeeController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private HBox selectionPane;

    @FXML
    private AnchorPane root;

    public void initialize() {
        // Start with selection view visible, content hidden
        selectionPane.setVisible(true);
        contentPane.setVisible(false);
    }

    @FXML
    private void handleSelectionAdd() {
        loadContent("/view/dha_employee_add.fxml");
    }

    @FXML
    private void handleSelectionView() {
        loadContent("/view/dha_employee_view.fxml");
    }

    private void loadContent(String fxmlPath) {
        try {
            contentPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();

            // Set userData on contentPane to this controller so children can call back
            contentPane.setUserData(this);

            pane.setPrefWidth(contentPane.getWidth());
            pane.setPrefHeight(contentPane.getHeight());

            // Bind dimensions
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

    // Method to open Add view with data (for Edit functionality)
    public void openAdd(repository.DHAEmployeeRepository.DHAEmployeeItem data) {
         try {
            contentPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dha_employee_add.fxml"));
            AnchorPane pane = loader.load();
            
            // Set userData so child can navigate back
            contentPane.setUserData(this);
            pane.setUserData(this); // Redundant but safe

            Object controller = loader.getController();
            if (controller instanceof DHAEmployeeAddController) {
                ((DHAEmployeeAddController) controller).setInitialData(data);
            }

            pane.setPrefWidth(contentPane.getWidth());
            pane.setPrefHeight(contentPane.getHeight());
            pane.prefWidthProperty().bind(contentPane.widthProperty());
            pane.prefHeightProperty().bind(contentPane.heightProperty());

            contentPane.getChildren().add(pane);

            selectionPane.setVisible(false);
            contentPane.setVisible(true);
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not open Add/Edit page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
