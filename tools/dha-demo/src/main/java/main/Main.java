package main;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Run Database Migration (safe to run multiple times)
            database.ConnectionFactory.getInstance();
            
            // 2. Start Sync Service
            service.SyncService syncService = new service.SyncService();
            syncService.start();

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login.fxml")));
            Scene scene = new Scene(root);

            // Apply robust DatePicker converters across the loaded scene to avoid parsing exceptions
            applyDatePickerConverters(root);

            primaryStage.setTitle("DHA Inventory — Offline Sample Demo");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void applyDatePickerConverters(Parent root) {
        java.time.format.DateTimeFormatter isoFormatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
        javafx.util.StringConverter<java.time.LocalDate> robustConverter = new javafx.util.StringConverter<>() {
            private final java.time.format.DateTimeFormatter[] formats = new java.time.format.DateTimeFormatter[] {
                    java.time.format.DateTimeFormatter.ISO_LOCAL_DATE,
                    java.time.format.DateTimeFormatter.ISO_DATE,
                    java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd")
            };

            @Override
            public String toString(java.time.LocalDate date) {
                return date == null ? "" : date.format(isoFormatter);
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                String s = string.trim();
                for (java.time.format.DateTimeFormatter f : formats) {
                    try {
                        return java.time.LocalDate.parse(s, f);
                    } catch (java.time.format.DateTimeParseException ex) {
                        // try next
                    }
                }
                try {
                    return java.time.LocalDate.parse(s);
                } catch (java.time.format.DateTimeParseException ex) {
                    return null; // avoid throwing
                }
            }
        };

        traverseAndApply(root, node -> {
            if (node instanceof DatePicker) {
                ((DatePicker) node).setConverter(robustConverter);
            }
        });
    }

    private void traverseAndApply(Node node, java.util.function.Consumer<Node> action) {
        action.accept(node);
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                traverseAndApply(child, action);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
