package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class AddItemController {

    @FXML
    private Button auctionButton;

    @FXML
    private Button userButton;

    @FXML
    private Button storeButton;

    public void initialize() {
        // Initialization logic if needed
    }

    // --- New handlers for Auction / Store / User dialogs ---

    @FXML
    private void handleAuction() {
        try {
            // Load the add_auction_item.fxml form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_auction_item.fxml"));
            AnchorPane auctionItemPane = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            Scene scene = auctionButton.getScene();
            if (scene != null) {
                AnchorPane centerPane = (AnchorPane) scene.lookup("#centerPane");
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
            // Load the add_store_item.fxml form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_store_item.fxml"));
            AnchorPane storeItemPane = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            Scene scene = storeButton.getScene();
            if (scene != null) {
                AnchorPane centerPane = (AnchorPane) scene.lookup("#centerPane");
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
            // Load the add_user_item.fxml form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_user_item.fxml"));
            AnchorPane userItemPane = loader.load();

            // Get the current scene and find the centerPane (from base.fxml)
            Scene scene = userButton.getScene();
            if (scene != null) {
                AnchorPane centerPane = (AnchorPane) scene.lookup("#centerPane");
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
