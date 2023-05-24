package com.example.fxjgit.popups;

import com.example.fxjgit.JgitApi;
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

    public class CloneRepositoryController implements Initializable, IPopup {

    @FXML
    private TextField urlTextField;

    @FXML
    private TextField selectedLocationTextField;

    @FXML
    public Button cloneButton;

    @FXML
    private String title = "Clone repository";

    Git git;
    @FXML
    private void initialize() {
        // ставим заголовок окна через костыль
        cloneButton.setDisable(true);
        Stage currentStage = (Stage)cloneButton.getScene().getWindow();
        currentStage.setTitle(title);

        // Установка слушателя событий изменения текста
        urlTextField.textProperty().addListener((observable, oldValue, newValue) -> {
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

    @FXML
    private Git cloneRepository() {
        // Обработчик нажатия кнопки "Clone"
        String repositoryURL = urlTextField.getText();
        String localPath = selectedLocationTextField.getText();

       this.git = JgitApi.cloneRepository(repositoryURL, localPath);

       return this.git;
    }

    @FXML
    private void validateInput() {
        if(!urlTextField.getText().isEmpty()
                && !selectedLocationTextField.getText().isEmpty()){
            cloneButton.setDisable(false);
        }
        else{
            cloneButton.setDisable(true);
        }
    }

    @Override
    public Git finalAction() {
        return cloneRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}