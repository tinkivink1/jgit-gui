package com.example.fxjgit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateRepositoryController implements Initializable, IPopup {
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField descriptionTextField;
    @FXML
    public TextField selectedLocationTextField;

    @FXML
    public Button createButton;
    @FXML
    private String title = "Create a new repository";

    private Git git;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void initialize() {
        // ставим заголовок окна через костыль
        createButton.setDisable(true);
        Stage currentStage = (Stage)createButton.getScene().getWindow();
        currentStage.setTitle(title);


        // Установка слушателя событий изменения текста
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
            validateInput();
        });

        descriptionTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
            validateInput();
        });

        selectedLocationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
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


    public Git createNewRepository() {
        this.git = JgitApi.initializeRepository(selectedLocationTextField.getText());

        return this.git;
    }

    @FXML
    private void validateInput() {
        if(!nameTextField.getText().isEmpty()
        && !descriptionTextField.getText().isEmpty()
        && !selectedLocationTextField.getText().isEmpty()){
            createButton.setDisable(false);
        }
        else{
            createButton.setDisable(true);
        }
    }

    @Override
    public Git finalAction() {
        return createNewRepository();
    }
}
