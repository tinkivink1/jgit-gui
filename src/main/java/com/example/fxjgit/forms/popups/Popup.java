package com.example.fxjgit.forms.popups;

import com.example.fxjgit.db.DAOFactory;
import com.example.fxjgit.db.UserDAO;
import com.example.fxjgit.db.entities.Repository;
import com.example.fxjgit.db.entities.User;
import com.example.fxjgit.forms.ProjectWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Popup {

    public static void showCreatePopup(User user) throws IOException {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(CreateRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-create-repository.fxml",
                "#createButton",
                        user,
                        git ->  resultGit.set(git) );

        System.out.println(resultGit.get());
        if (resultGit.get() != null){
            nextScene(resultGit.get(), user);
            UserDAO userDAO = DAOFactory.getUserDAO();
            user.getRepositories().add(new Repository(user.getUserId(), resultGit
                    .get()
                    .getRepository()
                    .getDirectory()
                    .getAbsolutePath()));
            userDAO.update(user);
        }
    }

    public static void showClonePopup(User user) throws IOException, GitAPIException {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(CloneRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-clone-repository.fxml",
                "#cloneButton",
                user,
                git -> resultGit.set(git));
        System.out.println(resultGit.get());
        if (resultGit.get() != null){
            nextScene(resultGit.get(), user);

            UserDAO userDAO = DAOFactory.getUserDAO();
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
            userDAO.update(user);
        }
    }

    public static void showExistingPopup(User user) throws IOException, GitAPIException {
        AtomicReference<Git> resultGit = new AtomicReference<>(null);
        showPopupScene(ExistingRepositoryController.class,
                "/com/example/fxjgit/forms/popups/popup-existing-repository.fxml",
                "#openButton",
                user,
                git -> resultGit.set(git));
        System.out.println(resultGit.get());
        if (resultGit.get() != null){
            nextScene(resultGit.get(), user);

            UserDAO userDAO = DAOFactory.getUserDAO();
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
            userDAO.update(user);
        }
    }
    private static  <T> void showPopupScene(Class<T> controllerType, String pathToScene, String actionButtonName, User user, Consumer<Git> resultHandler){
        // Загружаем разметку из FXML-файла
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Popup.class.getResource(pathToScene));
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

    public static void nextScene(Git git, User user) throws IOException {
        if(git==null)
            return;
        FXMLLoader fxmlLoader = new FXMLLoader(ProjectWindowController.class.getResource("/com/example/fxjgit/forms/project-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        ProjectWindowController helloController = fxmlLoader.getController();
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
//        Stage stage = (Stage) createButton.getScene().getWindow();
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

//        ((Stage)createButton.getScene().getWindow()).close();
    }

}
