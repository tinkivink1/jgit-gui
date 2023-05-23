package com.example.fxjgit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/fxjgit/forms/StartWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        StartWindowController swc = fxmlLoader.getController();
        swc.setParentStage(stage);
        stage.setTitle("Jgit FX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}