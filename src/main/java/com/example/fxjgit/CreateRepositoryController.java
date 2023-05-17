package com.example.fxjgit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateRepositoryController implements Initializable {
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField descriptionTextField;
    @FXML
    public TextField selectedLocationTextField;

    @FXML
    public Button createButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButton.setDisable(true);

        // Установка слушателя событий изменения текста
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
            System.out.println("Текст изменился: " + newValue);
            validateInput();
        });

        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
            System.out.println("Текст изменился: " + newValue);
            validateInput();
        });

        selectedLocationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
            System.out.println("Текст изменился: " + newValue);
            validateInput();
        });
    }
    @FXML
    private void onSelectLocationButtonClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Repository Location");

        // Отображение диалогового окна выбора пути
        File selectedDirectory = directoryChooser.showDialog(((Button) event.getSource()).getScene().getWindow());

        if (selectedDirectory != null) {
            // Получение пути к выбранному расположению
            String selectedPath = selectedDirectory.getAbsolutePath();
            // Отображение пути в TextField
            selectedLocationTextField.setText(selectedPath);
        }
    }


    public void createNewRepository(ActionEvent actionEvent) {
        JgitApi.initializeRepository(selectedLocationTextField.getText());
    }

    @FXML
    private void validateInput() {
        if(!nameTextField.getText().isEmpty()
        && !descriptionTextField.getText().isEmpty()
        && !selectedLocationTextField.getText().isEmpty()){
            createButton.setDisable(false);
            System.out.println("not valid");
        }
        else{
            createButton.setDisable(true);
            System.out.println("valid");

        }
    }
}
