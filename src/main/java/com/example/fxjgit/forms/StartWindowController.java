package com.example.fxjgit.forms;

import com.example.fxjgit.JgitApi;
import com.example.fxjgit.db.DAOFactory;
import com.example.fxjgit.db.RepositoryDAO;
import com.example.fxjgit.db.UserDAO;
import com.example.fxjgit.db.entities.Repository;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.forms.popups.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class StartWindowController {
    @FXML
    public Button createButton;
    @FXML
    public Button cloneButton;
    @FXML
    public Button addExistingButton;
    public ListView repositoriesList;
    public Button reloadButton;
    public HBox menuHbox;

    Stage parentStage;

    User user;

    private ToolsMenuController toolsMenuController = null;
    public StartWindowController(){
    }

    public void initialize(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tools-menu.fxml"));
            Node toolsMenu = loader.load();
            menuHbox.getChildren().add(toolsMenu);
            toolsMenuController = loader.getController();
//            toolsMenuController.setGit();
        } catch (IOException e) {
            e.printStackTrace();
        }


        repositoriesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                Object selectedItem = repositoriesList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    try {
                        Popup.nextScene(JgitApi.openRepository((String) selectedItem), user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setParentStage(Stage stage){
        parentStage=stage;
//        parentStage= (Stage) createButton.getScene().getWindow();
    }

    public void setUser(User user){
        this.user=user;
        setRepositoriesInInterface();
    }

    private void setRepositoriesInInterface() {
        repositoriesList.getItems().clear();

        RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
        List<Repository> userRepositories = repositoryDAO.getRepositoriesByUserId(user.getUserId());
        for (Repository repository : userRepositories) {
            repositoriesList.getItems().add(repository.getLocalPath());
        }
    }

    public void onCreateButtonClicked(MouseEvent event) throws IOException {
        Popup.showCreatePopup(user);
    }

    public void onCloneButtonClicked(MouseEvent event) throws IOException, GitAPIException {
        Popup.showClonePopup(user);
    }

    public void onExistingButtonClicked(MouseEvent event) throws IOException, GitAPIException {
        Popup.showExistingPopup(user);
    }



    public void reloadRepositories(MouseEvent mouseEvent) {
        setRepositoriesInInterface();
    }

    public void onReturnClicked(MouseEvent mouseEvent) {
        parentStage.show();
        Stage stage = (Stage) cloneButton.getScene().getWindow();
        stage.close();
    }
}
