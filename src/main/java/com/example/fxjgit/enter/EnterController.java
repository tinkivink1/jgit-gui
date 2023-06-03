package com.example.fxjgit.enter;

import com.example.fxjgit.forms.StartWindowController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class EnterController {

    public Button loginButton;
    public Button registerButton;
    public Button enterButton;

    @FXML
    public void loginButtonClicked(ActionEvent event) throws IOException {
        // Обработчик нажатия кнопки "Залогиниться"
        nextScene(LoginController.class, "/com/example/fxjgit/forms/enter/login.fxml");
    }

    @FXML
    public void registerButtonClicked(ActionEvent event) throws IOException {
        // Обработчик нажатия кнопки "Зарегистрироваться"
        nextScene(RegistrationController.class, "/com/example/fxjgit/forms/enter/registration.fxml");
    }

    @FXML
    public void enterButtonClicked(ActionEvent event) throws IOException {
        // Обработчик нажатия кнопки "Войти без учетной записи"
        nextScene(StartWindowController.class, "/com/example/fxjgit/forms/start-window.fxml");
    }

    public void nextScene(Class<?> controllerClass, String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        Parent root = loader.load();

        // Get the controller instance
        Object controller = loader.getController();

        // Create a new stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        // Set the controller for the new scene
        if (controller != null) {
            loader.setControllerFactory(clazz -> {
                if (clazz == controllerClass) {
                    return controller;
                } else {
                    try {
                        return clazz;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        if(loader.getController().getClass().equals(RegistrationController.class)){
            ((RegistrationController)loader.getController()).setParentStage((Stage)loginButton.getScene().getWindow());

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    ((Stage) loginButton.getScene().getWindow()).show();
                }
            });
        }
        if(loader.getController().getClass().equals(LoginController.class)){
            ((LoginController)loader.getController()).setParentStage((Stage)loginButton.getScene().getWindow());

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    ((Stage) loginButton.getScene().getWindow()).show();
                }
            });
        }
        Stage a = (Stage)loginButton.getScene().getWindow();
        if(loader.getController().getClass().equals(StartWindowController.class)){
            ((StartWindowController)loader.getController()).setParentStage(a);
        }
        // Show the new stage
        stage.show();

        // Close the current stage (optional)
        loginButton.getScene().getWindow().hide();
    }
}