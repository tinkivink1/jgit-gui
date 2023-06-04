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

    /**
     The start() method is called when the JavaFX application is started.
     It sets up the database connection, loads the FXML file, creates a scene,
     and displays the stage.
     @param stage the primary stage for the application
     @throws IOException if an I/O exception occurs during the loading of the FXML file
     */
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

    /**
     The main() method is the entry point for the Java application.
     It launches the JavaFX application by calling the launch() method.
     @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}