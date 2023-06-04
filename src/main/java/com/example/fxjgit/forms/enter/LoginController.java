package com.example.fxjgit.forms.enter;

import com.example.fxjgit.db.DAOFactory;
import com.example.fxjgit.forms.StartWindowController;
import com.example.fxjgit.db.UserDAO;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.forms.ToolsMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public Button loginButton;
    public BorderPane rootNode;
    Stage parentStage;

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

    public void setParentStage(Stage stage){
        parentStage=stage;
    }

    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public void loginButtonClicked(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        UserDAO userDAO = DAOFactory.getUserDAO();
        User user = userDAO.getByUsername(username);
        if(user != null){
            if(user.getPassword().equals(password));
                login(user);
        }
    }

    private void login(User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxjgit/forms/start-window.fxml"));
        Parent root = loader.load();

        // Create a new stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));

        if(loader.getController().getClass().equals(StartWindowController.class)){
            ((StartWindowController)loader.getController()).setParentStage((Stage)loginButton.getScene().getWindow());
            ((StartWindowController)loader.getController()).setUser(user);
        }
        // Show the new stage
        stage.show();

        // Close the current stage (optional)
        loginButton.getScene().getWindow().hide();
    }
}