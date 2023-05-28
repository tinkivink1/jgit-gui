package com.example.fxjgit;

import com.example.fxjgit.db.DBRepository;
import com.example.fxjgit.db.DBUser;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.enter.EnterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String DB_URL = "jdbc:postgresql://localhost:5432/gituserdb";
        String DB_USER = "root";
        String DB_PASS = "root";
        DBUser.getInstance(DB_URL, DB_USER, DB_PASS);
        DBRepository.getInstance(DB_URL, DB_USER, DB_PASS);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/fxjgit/forms/enter/enter.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Jgit FX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}