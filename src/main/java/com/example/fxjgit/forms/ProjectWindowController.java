package com.example.fxjgit.forms;

import com.example.fxjgit.JgitApi;
import com.example.fxjgit.db.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectWindowController implements Initializable {
    @FXML
    public VBox linesContainer;
    @FXML
    public SplitPane contentSplitPane;


    public HBox menuHbox;
    public TextField commitMessageTextField;


    @FXML
    private BorderPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<CheckBox> diffListView;

    @FXML
    private ListView<String> historyListView;

    @FXML
    private ListView<String> fileListView;

    @FXML
    public ListView<String> commitChangesListView;

    @FXML
    private TextField filterTextField;

    @FXML
    private Button commitButton;

    @FXML
    private Button pullButton;

    @FXML
    private Button pushButton;

    @FXML
    private Button settingsButton;

    private Repository repository;
    private Git git;

    private ToolsMenuController toolsMenuController = null;
    User user;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateScreen();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tools-menu.fxml"));
            Node toolsMenu = loader.load();
            menuHbox.getChildren().add(toolsMenu);
            toolsMenuController = loader.getController();

        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }


        diffListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onDiffListviewClicked(newValue.getText()));

        historyListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onHistoryListviewClicked(newValue));

        commitChangesListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onCommitChangesClicked((String) newValue));



    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        toolsMenuController.setUser(user);
    }

    public void updateScreen() throws GitAPIException, IOException {
        if(git != null){
            loadDiffList();
            loadCommitHistory();
        }

    }

    public void setGit(Git git) throws IOException {
        this.git = git;
        toolsMenuController.setGit(git);
    }

    private void loadChangesToListView(List<String> diffList, ListView dstListView) throws IOException {
        // Load the Git repository


        // Create an ObservableList to store the changes
        ObservableList<String> changes = FXCollections.observableArrayList(diffList);

        // Set the changes list to the ListView
        dstListView.setItems(changes);

        // Set cell factory to apply styles to list cells
        dstListView.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");  // Clear any existing styles
                } else {
                    setText(item);

                    // Apply styles based on the content of the item
                    getStyleClass().removeAll("deleted", "added");
                    if (item.startsWith("-")) {
                        getStyleClass().add("deleted");
                    } else if (item.startsWith("+")) {
                        getStyleClass().add("added");
                    }
                }
            }
        });
    }

    private void loadDiffList(){
        diffListView.getItems().clear();

        try {
            List<String> status = JgitApi.status(git);
            for (String changedFile : status) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(true);
                checkBox.setText(changedFile);
                diffListView.getItems().add(new CheckBox(changedFile));
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }


    }

    private void fillCommitChangesList(List<String> changes)  {
        commitChangesListView.getItems().clear();
        // Добавляем измененные файлы в список изменений
        for (String changedFile : changes) {
            commitChangesListView.getItems().add(changedFile);
        }
    }

    private void loadCommitHistory() {
        historyListView.getItems().clear();

        try {
            // Получаем список коммитов текущей ветки
            Iterable<RevCommit> commits = git.log().call();

            // Добавляем каждый коммит в историю
            for (RevCommit commit : commits) {
                historyListView.getItems().add(commit.getFullMessage());
            }
        }
        catch (NoHeadException e) {
            e.printStackTrace();
        }
        catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void onDiffListviewClicked(String newValue){
        // При выборе элемента changesListView
        if (newValue != null) {
            commitChangesListView.setVisible(false);
            contentSplitPane.setDividerPositions(0);
            try {
                 loadChangesToListView(JgitApi.getCurrentDiffs(git, newValue), fileListView);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void onHistoryListviewClicked(String newValue){
        // При выборе элемента historyListView
        if (newValue != null) {
            commitChangesListView.setVisible(true);
            contentSplitPane.setDividerPositions(0.3);
        }
        // Получение выбранного элемента (название коммита)
        String selectedCommit = newValue;
        selectedCommit = selectedCommit.replace("\n", "");
        // Получение списка измененных файлов для выбранного коммита
        List<String> commitChanges = null;
        try {
            RevCommit revCommit = JgitApi.findCommitByMessage(git, selectedCommit);
            commitChanges = JgitApi.getAffectedFiles(git.getRepository(), revCommit.toObjectId());
            fillCommitChangesList(commitChanges);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        // Отображение списка измененных файлов в commitChangesListView
        ObservableList<String> changes = FXCollections.observableArrayList(commitChanges);
        commitChangesListView.setItems(changes);
    }

    private void onCommitChangesClicked(String newValue){
        String selectedCommit = historyListView
                                    .getSelectionModel()
                                    .getSelectedItem()
                                    .replace("\n", "");
        String selectedFile = ((String) commitChangesListView
                                            .getSelectionModel()
                                            .getSelectedItem())
                                            .replace("\n", "");

        try {
            RevCommit selectedCommitData = JgitApi.findCommitByMessage(git, selectedCommit);
            RevCommit parentCommitData = selectedCommitData.getParent(0);

            String oldData = JgitApi.getFileContentFromCommit(git.getRepository(), selectedCommitData, selectedFile);
            String newData = JgitApi.getFileContentFromCommit(git.getRepository(), parentCommitData, selectedFile);

            loadChangesToListView(JgitApi.generateDiff(oldData, newData), fileListView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onPullButtonClicked(ActionEvent event) {
        try {
            // Выполняем pull
            git.pull().call();

            // Обновляем списки изменений и истории коммитов
            updateScreen();

        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPushButtonClicked(ActionEvent event) {
        try {
            // Выполняем push
            git.push().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSettingsButtonClicked(ActionEvent event) {
        // Обработка события нажатия на кнопку Settings
    }

    public void onCommitButtonClicked(ActionEvent actionEvent) throws Exception {
        if(!commitMessageTextField.getText().isEmpty()
                && diffListView
                            .getItems()
                            .stream()
                            .anyMatch(x -> x.isSelected())){
            addSelectedFilesToCommit();
            JgitApi.commit(git, commitMessageTextField.getText());
        }

        updateScreen();
    }

    public void addSelectedFilesToCommit() throws Exception {
        for (CheckBox checkBox : diffListView.getItems()) {
            if (checkBox.isSelected()) {
                String filePath = checkBox.getText(); // Предполагается, что текст CheckBox содержит путь к файлу
                JgitApi.addFile(git, filePath);
            }
        }
    }


    boolean oddClick = true;
    public void allClicked(ActionEvent actionEvent) {
        for (CheckBox checkBox:
             diffListView.getItems()) {
            checkBox.setSelected(oddClick);
        }
        oddClick=!oddClick;
    }

    public void onResetButtonClicked(ActionEvent actionEvent) {
        JgitApi.reset(git);
    }
}