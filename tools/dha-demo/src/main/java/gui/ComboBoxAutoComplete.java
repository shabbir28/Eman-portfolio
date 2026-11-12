package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * Utility class to make a ComboBox searchable/filterable.
 */
public class ComboBoxAutoComplete<T> {

    private final ComboBox<T> comboBox;
    private final ObservableList<T> originalItems;
    private StringConverter<T> converter;

    public ComboBoxAutoComplete(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        this.originalItems = FXCollections.observableArrayList(comboBox.getItems());
        this.converter = comboBox.getConverter(); // Use existing converter or default logic

        this.comboBox.setEditable(true);

        // Handle Key Events on Editor
        this.comboBox.getEditor().setOnKeyReleased(this::handleKeyReleased);

        // Reset items when hiding? optional.
        // this.comboBox.setOnHiding(e -> resetItems());
    }

    private void handleKeyReleased(KeyEvent event) {
        // Navigation keys - let standard behavior happen (up/down/enter)
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
                event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END ||
                event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
            return;
        }

        String filter = comboBox.getEditor().getText();

        if (filter == null || filter.isEmpty()) {
            comboBox.setItems(originalItems);
            comboBox.hide();
        } else {
            ObservableList<T> filteredItems = FXCollections.observableArrayList();
            for (T item : originalItems) {
                if (matches(item, filter)) {
                    filteredItems.add(item);
                }
            }
            comboBox.setItems(filteredItems);

            if (!filteredItems.isEmpty()) {
                comboBox.show();
                // If the text matches exactly one item, maybe select it?
                // But typically we let user keep typing or arrow down.
            } else {
                comboBox.hide();
            }
        }
    }

    private boolean matches(T item, String filter) {
        String itemString = (converter != null) ? converter.toString(item) : item.toString();
        return itemString != null && itemString.toLowerCase().contains(filter.toLowerCase());
    }

    /**
     * Static helper to apply to a ComboBox
     */
    public static <T> void install(ComboBox<T> comboBox) {
        new ComboBoxAutoComplete<>(comboBox);
    }
}
