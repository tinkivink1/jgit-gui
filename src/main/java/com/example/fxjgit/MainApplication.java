package com.example.fxjgit;

import com.example.fxjgit.db.DAOFactory;
import com.example.fxjgit.db.RepositoryDAO;
import com.example.fxjgit.db.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String DB_URL = "jdbc:postgresql://localhost:5432/gituserdb";
        String DB_USER = "root";
        String DB_PASS = "root";
        DAOFactory.createConnection(DB_URL, DB_USER, DB_PASS);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/example/fxjgit/forms/enter/enter.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/com/example/fxjgit/forms/forms/ProjectWindowController.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Jgit FX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}