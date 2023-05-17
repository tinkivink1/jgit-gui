package com.example.fxjgit;

import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolsMenuContoller implements Initializable {
    public AnchorPane Menu;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        Menu.getStylesheets().add(getClass().getResource("/com/example/fxjgit/styles/menu-style.css").toString());
    }
}
