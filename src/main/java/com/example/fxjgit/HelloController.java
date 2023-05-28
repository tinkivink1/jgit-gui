package com.example.fxjgit;

import com.example.fxjgit.db.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    public VBox linesContainer;
    @FXML
    public SplitPane contentSplitPane;
    public ListView commitChangesListView;
    public HBox menuHbox;


    @FXML
    private BorderPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<CheckBox> changesListView;

    @FXML
    private ListView<String> historyListView;

    @FXML
    private ListView<String> fileListView;

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

    User user;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateScreen();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        changesListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onChangesListviewClicked(newValue));

        historyListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onHistoryListviewClicked(newValue));

        commitChangesListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onCommitChangesClicked((String) newValue));
    }

    private ObservableList<String> loadChangesToListView(Git git, ObjectId oldCommit, ObjectId newCommit, String fileName) throws IOException, GitAPIException {
        CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
        try (var reader = git.getRepository().newObjectReader()) {
            var head = newCommit;
            newTreeParser.reset(reader, head);
        }

        // Get the previous commit
        Iterable<RevCommit> commits = git.log().setMaxCount(1).call();
        RevCommit commit = commits.iterator().next();

        // Get the previous file tree
        CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
        try (var reader = git.getRepository().newObjectReader()) {
            var oldHead = oldCommit;
            oldTreeParser.reset(reader, oldHead);
        }

        // Perform diff between the two file trees
        List<DiffEntry> diffs = git.diff()
                .setNewTree(newTreeParser)
                .setOldTree(oldTreeParser)
                .call();

        // Create an ObservableList to store the changes
        ObservableList<String> changes = FXCollections.observableArrayList();

        // Generate diff for the specified file
        for (DiffEntry diff : diffs) {
            if (diff.getNewPath().equals(fileName)) {
                // Create a formatter for generating the diff text
                try (var reader = git.getRepository().newObjectReader()) {
                    var oldObjectId = diff.getOldId().toObjectId();
                    var newObjectId = diff.getNewId().toObjectId();
                    var oldText = new RawText(reader.open(oldObjectId).getBytes());
                    var newText = new RawText(reader.open(newObjectId).getBytes());
                    var diffFormatter = new DiffFormatter(System.out);
                    var edits = diffFormatter.toFileHeader(diff).toEditList();

                    // Generate the diff text for the specified file
                    StringBuilder diffTextBuilder = new StringBuilder();
                    for (Edit edit : edits) {
                        for (int i = edit.getBeginA(); i < edit.getEndA(); i++) {
                            diffTextBuilder.append(oldText.getString(i)).append("\n");
                        }
                        for (int i = edit.getBeginB(); i < edit.getEndB(); i++) {
                            diffTextBuilder.append(newText.getString(i)).append("\n");
                        }
                    }

                    // Add the diff text to the changes list
                    changes.add(diffTextBuilder.toString());
                }
            }
        }

        // Set the changes list to the ListView
        return changes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateScreen() throws GitAPIException, IOException {
        if(git != null){
            loadChangesList();
            loadCommitHistory();
        }

    }

    public void setGit(Git git) throws IOException {
        this.git = git;
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

    private void loadChangesList() {
        changesListView.getItems().clear();

        StatusCommand statusCommand = git.status();
        Status status = null;
        try {
            status = statusCommand.call();
            for (String changedFile : status.getModified()) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(true);
                checkBox.setText(changedFile);
                changesListView.getItems().add(checkBox);
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

    private void onChangesListviewClicked(CheckBox newCheckboxValue){
        String newValue = newCheckboxValue.getText();
        // При выборе элемента changesListView
        if (newValue != null) {
            commitChangesListView.setVisible(false);
            contentSplitPane.setDividerPositions(0);
            try {
                JgitApi.getCurrentDiffs(git, newValue);
            } catch (IOException e) {
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
        }

        // Отображение списка измененных файлов в commitChangesListView
        ObservableList<String> changes = FXCollections.observableArrayList(commitChanges);
        commitChangesListView.setItems(changes);
    }

    private void onCommitChangesClicked(String newValue){
        String selectedCommit = historyListView
                                    .getSelectionModel()
                                    .getSelectedItem();
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
    private void onCommitButtonClicked(ActionEvent event) {
        try {
            // Выполняем коммит
            git.commit().setMessage("Commit message").call();

            // Обновляем списки изменений и истории коммитов
            loadChangesList();
            loadCommitHistory();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPullButtonClicked(ActionEvent event) {
        try {
            // Выполняем pull
            git.pull().call();

            // Обновляем списки изменений и истории коммитов
            loadChangesList();
            loadCommitHistory();
        } catch (GitAPIException e) {
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

}