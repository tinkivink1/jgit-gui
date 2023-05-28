package com.example.fxjgit;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class CheckboxListCell<T> extends ListCell<T> {
    private final CheckBox checkBox;

    public CheckboxListCell() {
        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (getItem() != null) {
                // Обработка изменения состояния чекбокса
                // Можете добавить свою логику здесь
                System.out.println("Выбран элемент: " + getItem() + ", состояние: " + newValue);
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            checkBox.setText(item.toString());
            setGraphic(checkBox);
        }
    }

    public static <T> Callback<ListView<T>, ListCell<T>> forListView() {
        return param -> new CheckboxListCell<>();
    }
}