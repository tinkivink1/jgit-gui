package com.example.fxjgit;

import com.example.fxjgit.db.DBRepository;
import com.example.fxjgit.db.DBUser;
import com.example.fxjgit.db.entities.Repository;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.popups.CloneRepositoryController;
import com.example.fxjgit.popups.CreateRepositoryController;
import com.example.fxjgit.popups.ExistingRepositoryController;
import com.example.fxjgit.popups.IPopup;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
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

    Stage parentStage;

    User user;

    public StartWindowController(){


    }

    public void initialize(){
        repositoriesList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                Object selectedItem = repositoriesList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    try {
                        nextScene(JgitApi.openRepository((String) selectedItem));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setParentStage(Stage stage){
        parentStage=stage;
    }

    public void setUser(User user){
        this.user=user;
        setRepositoriesInInterface();
    }

    private void setRepositoriesInInterface() {
        repositoriesList.getItems().clear();

        DBRepository dbRepository = DBRepository.getInstance();
        List<Repository> userRepositories = dbRepository.getRepositoriesByUserId(user.getUserId());
        for (Repository repository : userRepositories) {
            repositoriesList.getItems().add(repository.getLocalPath());
        }
    }

    public void onCreateButtonClicked(MouseEvent event) throws IOException {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(CreateRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-create-repository.fxml",
                "#createButton",
                git ->  resultGit.set(git) );
        System.out.println(resultGit.get());
        if (resultGit.get() != null){
            nextScene(resultGit.get());
        }

        DBUser dbUser = DBUser.getInstance();
        user.getRepositories().add(new Repository(user.getUserId(), resultGit
                                                                        .get()
                                                                        .getRepository()
                                                                        .getDirectory()
                                                                        .getAbsolutePath()));
        dbUser.update(user);
    }

    public void onCloneButtonClicked(MouseEvent event) throws IOException, GitAPIException {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(CloneRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-clone-repository.fxml",
                "#cloneButton",
                git -> resultGit.set(git));
        System.out.println(resultGit.get());
        if (resultGit.get() != null){
            nextScene(resultGit.get());

            DBUser dbUser = DBUser.getInstance();
            Iterable<RemoteConfig> remoteConfigs = resultGit.get().remoteList().call();
            // Перебрать все удаленные репозитории и получить ссылку на нужный
            String remoteName = "origin";
            String remoteUrl = "";
            for (RemoteConfig remoteConfig : remoteConfigs)
                if (remoteConfig.getName().equals(remoteName)) {
                    URIish remoteUri = remoteConfig.getURIs().get(0);
                    remoteUrl = remoteUri.toString();
                    break;
                }

            user.getRepositories().add(new Repository(user.getUserId(), remoteUrl,resultGit
                                                                                    .get()
                                                                                    .getRepository()
                                                                                    .getDirectory()
                                                                                    .getAbsolutePath()));
            dbUser.update(user);
        }
    }

    public void onExistingButtonClicked(MouseEvent event) throws IOException, GitAPIException {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(ExistingRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-existing-repository.fxml",
                "#openButton",
                git -> resultGit.set(git));
        System.out.println(resultGit.get());
        if (resultGit.get() != null){
            nextScene(resultGit.get());

            DBUser dbUser = DBUser.getInstance();
            Iterable<RemoteConfig> remoteConfigs = resultGit.get().remoteList().call();
            // Перебрать все удаленные репозитории и получить ссылку на нужный
            String remoteName = "origin";
            String remoteUrl = "";
            for (RemoteConfig remoteConfig : remoteConfigs)
                if (remoteConfig.getName().equals(remoteName)) {
                    URIish remoteUri = remoteConfig.getURIs().get(0);
                    remoteUrl = remoteUri.toString();
                    break;
                }

            user.getRepositories().add(new Repository(user.getUserId(), remoteUrl,resultGit
                    .get()
                    .getRepository()
                    .getDirectory()
                    .getAbsolutePath()));
            dbUser.update(user);
        }
    }

    private  void nextScene(Git git) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/fxjgit/forms/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        HelloController helloController = fxmlLoader.getController();
        helloController.setGit(git);
        helloController.setUser(user);
        try {
            helloController.updateScreen();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
//        parentStage.setTitle("Jgit FX");
//        parentStage.setScene(scene);
//        parentStage.show();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

//        ((Stage)createButton.getScene().getWindow()).close();
    }

    private <T> void showPopupScene(Class<T> controllerType, String pathToScene, String actionButtonName, Consumer<Git> resultHandler){
        // Загружаем разметку из FXML-файла
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathToScene));
            root = fxmlLoader.load();
            T controller = fxmlLoader.getController();

            // Создаем новую сцену
            Scene scene = new Scene(root);

            // Создаем новое окно
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Блокирует взаимодействие с основным окном
            popupStage.setScene(scene);

            Button initializeButton = (Button) root.lookup(actionButtonName); // Замените "initializeButton" на соответствующий ID кнопки в FXML
            initializeButton.addEventHandler(ActionEvent.ACTION, e -> {
                Git result = null;
                if (controllerType.isInstance(controller)) {
                    T typedController = controllerType.cast(controller);
                    if (IPopup.class.isAssignableFrom(IPopup.class)) {
                        IPopup repositoryController = (IPopup) typedController;
                        repositoryController.setUser(user);
                        result = repositoryController.finalAction();
                    }
                }
                resultHandler.accept(result); // Передаем результат обратно в вызывающий код
                popupStage.close();
//                parentStage.hide();
            });

            popupStage.setOnCloseRequest (e -> {
                if (IPopup.class.isAssignableFrom(IPopup.class)) {
                    IPopup repositoryController = (IPopup) controller;
                    Git result = repositoryController.finalAction();
                    resultHandler.accept(result); // Передаем результат обратно в вызывающий код
                    System.out.println(result);
                }
            });

            // Отображаем окно по центру
            popupStage.centerOnScreen();

            // Показываем окно и ждем, пока оно не будет закрыто
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadRepositories(MouseEvent mouseEvent) {
        setRepositoriesInInterface();
    }
}
