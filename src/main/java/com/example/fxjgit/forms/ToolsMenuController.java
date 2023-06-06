package com.example.fxjgit.forms;

import com.example.fxjgit.JgitApi;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.forms.popups.Popup;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    public MenuBar menuBar;
    public javafx.scene.control.Menu branchMenu;
    public javafx.scene.control.Menu branchName;

    Git git;
    User user;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Menu.getStylesheets().add(getClass().getResource("/com/example/fxjgit/styles/menu-style.css").toString());
    }



    public void setGit(Git git) {
        this.git = git;
        updateBranchListMenu();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void newRepoClicked(ActionEvent actionEvent) throws IOException {
        Popup.showCreatePopup(user);
    }

    public void cloneClicked(ActionEvent actionEvent) throws GitAPIException, IOException {
        Popup.showClonePopup(user);

    }

    public void openClicked(ActionEvent actionEvent) throws GitAPIException, IOException {
        Popup.showExistingPopup(user);
    }

    public void newBranchClicked(ActionEvent actionEvent) {
        // Показываем диалоговое окно для ввода строки
        JOptionPane jOptionPane = new JOptionPane();
        String input = jOptionPane.showInputDialog(null, "Название ветки:");

        // Проверка, была ли введена строка или нажата кнопка "Отмена"
        if (input != null) {
            JgitApi.createBranch(git, input);
            updateBranchListMenu();
        } else {
            System.out.println("Операция отменена");
        }
        updateBranchListMenu();

    }

    public void renameBranchClicked(ActionEvent actionEvent) throws IOException {
        JOptionPane jOptionPane = new JOptionPane();
        String input = jOptionPane.showInputDialog(null, "Название ветки:");

        // Проверка, была ли введена строка или нажата кнопка "Отмена"
        if (input != null) {
            JgitApi.renameBranch(git, git.getRepository().getBranch(), input);
            updateBranchListMenu();
        } else {
            System.out.println("Операция отменена");
        }

        updateBranchListMenu();
    }

    public void deleteClicked(ActionEvent actionEvent) throws IOException {
        JgitApi.deleteBranch(git, git.getRepository().getBranch());
        updateBranchListMenu();

    }

    public void discardClicked(ActionEvent actionEvent) {

    }

    public void stashClicked(ActionEvent actionEvent) {

    }

    public void onPush() {
        try {
            JgitApi.push(git, JgitApi.getRemoteRepositoryUrl(git, "origin"), user.getUsername(), user.getPassword());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void onPull() {
        try {
            JgitApi.pull(git, JgitApi.getRemoteRepositoryUrl(git, "origin"), user.getUsername(), user.getPassword());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void onFetch(ActionEvent actionEvent) {
        try {
            JgitApi.fetch(git, JgitApi.getRemoteRepositoryUrl(git, "origin"), user.getUsername(), user.getPassword());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void updateBranchListMenu(){
        branchMenu.getItems().clear();

        List<String> branchList = JgitApi.getBranchList(git);

        for (String branch : branchList) {
            MenuItem menuItem = new MenuItem(branch);
            menuItem.setOnAction(event -> {
                JgitApi.switchToBranch(git, branch);
                updateBranchListMenu();
            });
            branchMenu.getItems().add(menuItem);
        }

        try {
            branchName.setText("Current branch: " + git.getRepository().getBranch());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void newRemoteClicked(ActionEvent actionEvent) {
        JOptionPane jOptionPane = new JOptionPane();
        String remote = jOptionPane.showInputDialog(null, "Название удаленного репозитория:");

        // Проверка, была ли введена строка или нажата кнопка "Отмена"
        if (remote == null) {
            return;
        }
        String url = jOptionPane.showInputDialog(null, "Ссылка на удаленный репозиторий:");
        if (remote == null) {
            return;
        }

        JgitApi.addRemote(git, remote, url);
        updateBranchListMenu();
    }
}
