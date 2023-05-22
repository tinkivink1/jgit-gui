package com.example.fxjgit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class StartWindowController {
    @FXML
    public Button createButton;
    @FXML
    public Button cloneButton;
    @FXML
    public Button addExistingButton;

    private IPopup popupEndsListener;

    public void onCreateButtonClicked(MouseEvent event) {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(CreateRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-create-repository.fxml",
                "#createButton",
                git ->  resultGit.set(git) );
        System.out.println(resultGit.get());
    }

    public void onCloneButtonClicked(MouseEvent event) {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(CloneRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-clone-repository.fxml",
                "#cloneButton",
                git -> resultGit.set(git));
        System.out.println(resultGit.get());
    }

    public void onExistingButtonClicked(MouseEvent event) {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(ExistingRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-existing-repository.fxml",
                "#openButton",
                git -> resultGit.set(git));
        System.out.println(resultGit.get());
    }

    private <T> void showPopupScene(Class<T> controllerType, String pathToScene, String actionButtonName, Consumer<Git> resultHandler){
        // Загружаем разметку из FXML-файла
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathToScene));
            root = fxmlLoader.load();
            T controller = fxmlLoader.getController();

            // Создаем новую сцену
            Scene scene = new Scene(root);

            // Создаем новое окно
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Блокирует взаимодействие с основным окном
            popupStage.setScene(scene);

            Button initializeButton = (Button) root.lookup(actionButtonName); // Замените "initializeButton" на соответствующий ID кнопки в FXML
            initializeButton.addEventHandler(ActionEvent.ACTION, e -> {
                Git result = null;
                if (controllerType.isInstance(controller)) {
                    T typedController = controllerType.cast(controller);
                    if (IPopup.class.isAssignableFrom(IPopup.class)) {
                        IPopup repositoryController = (IPopup) typedController;
                        result = repositoryController.finalAction();
                    }
                }
                resultHandler.accept(result); // Передаем результат обратно в вызывающий код
                popupStage.close();
            });

            popupStage.setOnCloseRequest (e -> {
                if (IPopup.class.isAssignableFrom(IPopup.class)) {
                    IPopup repositoryController = (IPopup) controller;
                    Git result = repositoryController.finalAction();
                    resultHandler.accept(result); // Передаем результат обратно в вызывающий код
                    System.out.println(result);
                }
            });

            // Отображаем окно по центру
            popupStage.centerOnScreen();

            // Показываем окно и ждем, пока оно не будет закрыто
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
