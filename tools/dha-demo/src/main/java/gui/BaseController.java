package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;
import util.ImageLoader;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BaseController implements Initializable {
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane centerPane;
    @FXML
    private ImageView logoImageView;


    private User currentUser;
    private final Map<String, String> urls;

    public BaseController() {
        urls = new HashMap<>();
        urls.put("Dashboard", "/view/dashboard.fxml");
        urls.put("Add Item", "/view/add_item.fxml");
        urls.put("Assign Item", "/view/assign_item.fxml");
        urls.put("Return Item", "/view/return_item.fxml");
        urls.put("Store Inventory", "/view/store_inventory.fxml");
        urls.put("User Inventory", "/view/user_inventory.fxml");
        urls.put("Auction Inventory", "/view/auction_inventory.fxml");
        urls.put("Report", "/view/report.fxml");
        urls.put("Low Stock", "/view/low_stock.fxml");
        urls.put("DHA Employee", "/view/dha_employee.fxml");
    }

    @FXML
    private void btnNavigators(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String btnText = btn.getText();
        String url = urls.get(btnText);

        if (url == null) {
            DisplayAlert.showError("Error", "No page found for: " + btnText);
            return;
        }

        try {
            ctrlRightPane(url);
        } catch (IOException e) {
            DisplayAlert.showError("Error", "Error loading FXML: " + url);
            e.printStackTrace();
        }
    }

    @FXML
    private void ctrlRightPane(String url) throws IOException {
        try {
            centerPane.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            AnchorPane newCenterPane = loader.load();
            newCenterPane.setPrefHeight(centerPane.getHeight());
            newCenterPane.setPrefWidth(centerPane.getWidth());
            AnchorPane.setTopAnchor(newCenterPane, 0.0); AnchorPane.setBottomAnchor(newCenterPane, 0.0); AnchorPane.setLeftAnchor(newCenterPane, 0.0); AnchorPane.setRightAnchor(newCenterPane, 0.0);
            centerPane.getChildren().add(newCenterPane);
        } catch (IOException e) {
            // show user friendly alert and print stacktrace for debugging
            DisplayAlert.showError("Error", "Could not load FXML: " + url);
            e.printStackTrace();
            throw e; // rethrow so callers can also handle if needed
        }
    }

    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            Stage newStage = new Stage();
            newStage.setScene(scene);
            currentStage.close();
            newStage.show();
        } catch (IOException e) {
            DisplayAlert.showError("Error", "Could not load login view");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load logo image using the ImageLoader utility
        ImageLoader.setImageToView(logoImageView, "/logo.png");
        try { ctrlRightPane("/view/dashboard.fxml"); } catch (IOException e) { throw new IllegalStateException(e); }
    }
}