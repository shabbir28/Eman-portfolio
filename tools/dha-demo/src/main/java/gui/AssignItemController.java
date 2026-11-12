package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AssignItemController {

    @FXML
    private Button auctionButton;
    @FXML
    private Button userButton;
    @FXML
    private Button storeButton;

    public void initialize() {
        // Initialization logic if needed
    }

    @FXML
    private void handleAuction() {
        try {
            // Load the assign_auction_item.fxml form
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/assign_auction_item.fxml"));
            javafx.scene.layout.AnchorPane auctionItemPane = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            javafx.scene.Scene scene = auctionButton.getScene();
            if (scene != null) {
                javafx.scene.layout.AnchorPane centerPane = (javafx.scene.layout.AnchorPane) scene
                        .lookup("#centerPane");
                if (centerPane != null) {
                    // Replace content in centerPane
                    centerPane.getChildren().clear();
                    auctionItemPane.setPrefHeight(centerPane.getHeight());
                    auctionItemPane.setPrefWidth(centerPane.getWidth());
                    centerPane.getChildren().add(auctionItemPane);
                } else {
                    DisplayAlert.showError("Error", "Could not find centerPane in the scene");
                }
            }
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load Auction Item form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStore() {
        try {
            // Load the assign_store_item.fxml form
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/assign_store_item.fxml"));
            javafx.scene.layout.AnchorPane storeItemPane = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            javafx.scene.Scene scene = storeButton.getScene();
            if (scene != null) {
                javafx.scene.layout.AnchorPane centerPane = (javafx.scene.layout.AnchorPane) scene
                        .lookup("#centerPane");
                if (centerPane != null) {
                    // Replace content in centerPane
                    centerPane.getChildren().clear();
                    storeItemPane.setPrefHeight(centerPane.getHeight());
                    storeItemPane.setPrefWidth(centerPane.getWidth());
                    centerPane.getChildren().add(storeItemPane);
                } else {
                    DisplayAlert.showError("Error", "Could not find centerPane in the scene");
                }
            }
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load Store Item form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUser() {
        try {
            // Load the assign_user_item.fxml form
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/view/assign_user_item.fxml"));
            javafx.scene.layout.AnchorPane userItemPane = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            javafx.scene.Scene scene = userButton.getScene();
            if (scene != null) {
                javafx.scene.layout.AnchorPane centerPane = (javafx.scene.layout.AnchorPane) scene
                        .lookup("#centerPane");
                if (centerPane != null) {
                    // Replace content in centerPane
                    centerPane.getChildren().clear();
                    userItemPane.setPrefHeight(centerPane.getHeight());
                    userItemPane.setPrefWidth(centerPane.getWidth());
                    centerPane.getChildren().add(userItemPane);
                } else {
                    DisplayAlert.showError("Error", "Could not find centerPane in the scene");
                }
            }
        } catch (Exception e) {
            DisplayAlert.showError("Error", "Could not load User Item form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
