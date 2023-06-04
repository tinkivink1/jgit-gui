package com.example.fxjgit.forms.popups;

import com.example.fxjgit.JgitApi;
import com.example.fxjgit.db.entities.User;
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

public class ExistingRepositoryController implements Initializable, IPopup{
    @FXML
    private TextField pathTextField;

    @FXML
    public Button openButton;

    private String title = "Open existing repository";

    Git git;
    User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void initialize() {
        openButton.setDisable(true);
        // ставим заголовок окна через костыль
        Stage currentStage = (Stage)openButton.getScene().getWindow();
        currentStage.setTitle(title);


        // Установка слушателя событий изменения текста
        pathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Действие, которое нужно выполнить при изменении текста
            validateInput();
        });
    }

    public Git openRepository() {
        String repositoryPath = pathTextField.getText();

        this.git = JgitApi.openRepository(repositoryPath);
        // Здесь можно выполнить необходимые операции с открытым репозиторием
        return this.git;
    }

    @FXML
    private void validateInput() {
        if(!pathTextField.getText().isEmpty()){
            openButton.setDisable(false);
        }
        else {
            openButton.setDisable(true);
        }
    }



    @Override
    public Git finalAction() {
        return openRepository();
    }

    @Override
    public void setUser(User user) {
        this.user = user;
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
            pathTextField.setText(selectedPath);
        }
    }
}