package gui;

import handler.LoginHandler;
import handler.LoginResult;
import handler.UserExistsHandler;
import handler.ValidPasswordHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import repository.UserRepository;
import service.UserService;
import util.ImageLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ImageView bgImageView;

    private final UserService userService;

    public LoginController() {
        userService = new UserService(new UserRepository());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load background image using the ImageLoader utility
        ImageLoader.setImageToView(bgImageView, "/bg.png");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        LoginHandler handler = new UserExistsHandler(userService);
        handler.setNext(new ValidPasswordHandler());

        try {
            LoginResult result = handler.handle(username, password);
            if (result.success()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/base.fxml"));
                Parent root = loader.load();



                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.setMaximized(true);
                stage.show();
            } else {
                errorLabel.setText(result.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error: " + e.getCause());
        }
    }
}