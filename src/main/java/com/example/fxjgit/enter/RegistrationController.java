package com.example.fxjgit.enter;

import com.example.fxjgit.db.DBUser;
import com.example.fxjgit.db.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RegistrationController {
    public Button registerButton;
    Stage parentStage;
    public void setParentStage(Stage stage){
        parentStage=stage;
    }

    @FXML
    public TextField usernameField;

    @FXML
    public TextField passwordField;

    @FXML
    public void registerButtonClicked(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        DBUser dbUser = DBUser.getInstance();
        // Создание нового пользователя
        if(!username.isEmpty() && !password.isEmpty()){
            if(dbUser.add(new User(username, password)))
                System.out.println("Пользователь зарегистрирован");
            showEnterButton();
        }
    }

    private void showEnterButton(){
        parentStage.show();
        registerButton.getScene().getWindow().hide();
    }

}