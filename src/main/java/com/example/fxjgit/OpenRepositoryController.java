package com.example.fxjgit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenRepositoryController implements Initializable, IPopup{
    @FXML
    private TextField pathTextField;

    @FXML
    private Button openButton;

    private String title = "Open existing repository";

    Git git;
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

        try {
            FileRepository fileRepository = new FileRepository(repositoryPath);
            git = new Git(fileRepository);
            // Здесь можно выполнить необходимые операции с открытым репозиторием
            return git;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    private void validateInput() {
        if(!pathTextField.getText().isEmpty()){
            openButton.setDisable(false);
        }
        else{
            openButton.setDisable(true);
        }
    }



    @Override
    public Git returnValueOnClose() {
        return git;
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