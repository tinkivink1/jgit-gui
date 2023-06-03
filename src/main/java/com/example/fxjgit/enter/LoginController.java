package com.example.fxjgit.enter;

import com.example.fxjgit.forms.StartWindowController;
import com.example.fxjgit.db.DBUser;
import com.example.fxjgit.db.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public Button loginButton;
    Stage parentStage;
    public void setParentStage(Stage stage){
        parentStage=stage;
    }
    @FXML
    public TextField usernameField;

    @FXML
    public TextField passwordField;

    @FXML
    public void loginButtonClicked(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        DBUser dbUser = DBUser.getInstance();
        User user = dbUser.getByUsername(username);
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