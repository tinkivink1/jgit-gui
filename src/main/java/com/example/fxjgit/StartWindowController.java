package com.example.fxjgit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import com.example.fxjgit.JgitApi;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StartWindowController {
    @FXML
    public Button createButton;
    @FXML
    public Button cloneButton;
    @FXML
    public Button addExistingButton;


    public void onCreateButtonClicked(MouseEvent event) {
        // Загружаем разметку из FXML-файла
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/com/example/fxjgit/forms/popup-create-repository.fxml"));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        // Создаем новую сцену
        Scene scene = new Scene(root);

        // Создаем новое окно
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Блокирует взаимодействие с основным окном
//        popupStage.initOwner(primaryStage); // Устанавливает основное окно владельцем
        popupStage.setScene(scene);
        popupStage.setTitle("Create Repository");

        Button initializeButton = (Button) root.lookup("#createButton"); // Замените "initializeButton" на соответствующий ID кнопки в FXML
        initializeButton.setOnAction(e -> {
            handleRepositoryInitialization();
        });

        popupStage.setOnCloseRequest(e -> {
            handleRepositoryInitialization();
        });

        // Отображаем окно по центру
        popupStage.centerOnScreen();

        // Показываем окно
        popupStage.show();
    }


    private void handleRepositoryInitialization(){

    }
}
