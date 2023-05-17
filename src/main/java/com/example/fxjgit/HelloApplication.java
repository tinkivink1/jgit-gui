package com.example.fxjgit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/fxjgit/forms/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
//        scene.getStylesheets().add(getClass().getResource("github-like-style.css").toString());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}