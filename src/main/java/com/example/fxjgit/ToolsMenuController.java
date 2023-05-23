package com.example.fxjgit;

import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.eclipse.jgit.api.Git;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolsMenuController implements Initializable {
    public AnchorPane Menu;
    Git git;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Menu.getStylesheets().add(getClass().getResource("/com/example/fxjgit/styles/menu-style.css").toString());
    }


    public void setGit(Git git) {
        this.git = git;
    }
}
