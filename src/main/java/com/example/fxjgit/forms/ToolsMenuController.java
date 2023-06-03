package com.example.fxjgit.forms;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import org.eclipse.jgit.api.Git;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolsMenuController implements Initializable {
    public AnchorPane Menu;
    public MenuItem newRepoButton;
    public MenuItem cloneRepoButton;
    public MenuItem openRepoButton;
    public MenuItem pushButton;
    public MenuItem pullButton;
    public MenuItem fetchButton;
    public MenuItem removeButton;
    public MenuItem newBranchButton;
    public MenuItem renameButton;
    public MenuItem deleteButton;
    public MenuItem discardButton;
    public MenuItem stashButton;

    Git git;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Menu.getStylesheets().add(getClass().getResource("/com/example/fxjgit/styles/menu-style.css").toString());
    }


    public void setGit(Git git) {
        this.git = git;
    }

    public void newRepoClicked(ActionEvent actionEvent) {
    }

    public void cloneClicked(ActionEvent actionEvent) {
    }

    public void openClicked(ActionEvent actionEvent) {
    }

    public void pushClicked(ActionEvent actionEvent) {
    }

    public void pullClicked(ActionEvent actionEvent) {
    }

    public void fetchClicked(ActionEvent actionEvent) {
    }

    public void removeClicked(ActionEvent actionEvent) {
    }

    public void newBranchClicked(ActionEvent actionEvent) {
    }

    public void branchClicked(ActionEvent actionEvent) {
    }

    public void deleteClicked(ActionEvent actionEvent) {
    }

    public void discardClicked(ActionEvent actionEvent) {
    }

    public void stashClicked(ActionEvent actionEvent) {
    }
}
