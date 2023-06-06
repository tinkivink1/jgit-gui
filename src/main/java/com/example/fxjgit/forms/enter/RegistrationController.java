package com.example.fxjgit.forms.enter;

import com.example.fxjgit.db.DAOFactory;
import com.example.fxjgit.db.UserDAO;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.forms.ToolsMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RegistrationController {
    public Button registerButton;
    public BorderPane rootNode;
    public PasswordField secretToken;
    Stage parentStage;
    public void setParentStage(Stage stage){
        parentStage=stage;
    }

    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    public HBox menuHbox;
    private ToolsMenuController toolsMenuController;

    public void initialize(){
        try {
            rootNode.getStylesheets().add(getClass().getResource("/com/example/fxjgit/styles/github-like-style.css").toString());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxjgit/forms/tools-menu.fxml"));
            Node toolsMenu = loader.load();
            menuHbox.getChildren().add(toolsMenu);
            toolsMenuController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void registerButtonClicked(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String secret = secretToken.getText();
        UserDAO userDAO = DAOFactory.getUserDAO();
        // Создание нового пользователя
        if(!username.isEmpty() && !password.isEmpty()){
            if(userDAO.add(new User(username, password, secret)))
                System.out.println("Пользователь зарегистрирован");
            showEnterButton();
        }
    }

    private void showEnterButton(){
        parentStage.show();
        registerButton.getScene().getWindow().hide();
    }

}