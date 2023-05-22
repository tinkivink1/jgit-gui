package com.example.fxjgit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.diff.Edit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    @FXML
    private BorderPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private ListView<String> changesListView;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Открываем репозиторий
            repository = openRepository("C:\\Users\\Valodya\\Desktop\\jgit-gui");
            git = new Git(repository);

            // Загружаем список изменений
            loadChangesList();

            // Загружаем историю коммитов
            loadCommitHistory();

            // Загружаем список файлов
//            loadFileList();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        changesListView.setOnMouseClicked(event -> {
            String selectedFile = changesListView.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                try {
                    loadChangesToListView(selectedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        changesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // При выборе элемента changesListView
            if (newValue != null) {
                commitChangesListView.setVisible(false);
                contentSplitPane.setDividerPositions(0);
            }
        });

        historyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // При выборе элемента historyListView
            if (newValue != null) {
                commitChangesListView.setVisible(true);
                contentSplitPane.setDividerPositions(0.3);
            }
        });

        historyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Получение выбранного элемента (название коммита)
            String selectedCommit = historyListView.getSelectionModel().getSelectedItem();
            selectedCommit = selectedCommit.replace("\n", "");
            // Получение списка измененных файлов для выбранного коммита
            List<String> commitChanges = null;
            try {
                RevCommit revCommit = findCommitByMessage(selectedCommit);
                commitChanges = getAffectedFiles(git.getRepository(), revCommit.toObjectId());
                fillCommitChangesList(commitChanges);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Отображение списка измененных файлов в commitChangesListView
            ObservableList<String> changes = FXCollections.observableArrayList(commitChanges);
            commitChangesListView.setItems(changes);
        });


    }
    public static List<String> getAffectedFiles(Repository repository, ObjectId commitId) throws IOException {
        List<String> affectedFiles = new ArrayList<>();

        try (RevWalk revWalk = new RevWalk(repository)) {
            ObjectId commitObjectId = commitId;
            RevCommit commit = revWalk.parseCommit(commitObjectId);

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    affectedFiles.add(treeWalk.getPathString());
                }
            }
        }
        return affectedFiles;
    }






public RevCommit findCommitByMessage(String commitMessage) throws IOException {
        Repository repository = git.getRepository();

        try (RevWalk revWalk = new RevWalk(repository)) {
            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit commit : commits) {
                if (commit.getShortMessage().equals(commitMessage)) {
                    return commit;
                }
            }
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void loadChangesToListView(String fileName) throws IOException {
        // Load the Git repository
        try (Repository repository = git.getRepository()) {
            // Get the latest commit
            RevCommit commit = getLatestCommit(repository);

            // Get the file content from the latest commit
            String lastCommitContent = getFileContentFromCommit(repository, commit, fileName);

            // Get the current file content
            Path currentPath = Paths.get(git.getRepository().getDirectory() + "\\..\\", fileName);
            String currentContent = new String(Files.readAllBytes(currentPath));

            // Compare the contents and generate the diff
            List<String> diffList = generateDiff(lastCommitContent, currentContent);

            // Create an ObservableList to store the changes
            ObservableList<String> changes = FXCollections.observableArrayList(diffList);

            // Set the changes list to the ListView
            fileListView.setItems(changes);

            // Set cell factory to apply styles to list cells
            fileListView.setCellFactory(listView -> new ListCell<String>() {
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
    }


    private RevCommit getLatestCommit(Repository repository) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve("HEAD"));
            return commit;
        }
    }

    private String getFileContentFromCommit(Repository repository, RevCommit commit, String fileName)
            throws IOException {
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(fileName));

            if (treeWalk.next()) {
                ObjectId objectId = treeWalk.getObjectId(0);
                try (org.eclipse.jgit.lib.ObjectReader reader = repository.newObjectReader()) {
                    return new String(reader.open(objectId).getBytes());
                }
            }
        }

        return "";
    }


    private List<String> generateDiff(String oldContent, String newContent) {
        // Implement your own diff generation logic here
        // This can be done using libraries like diffutils or by implementing your own diff algorithm
        // Here's a simple example using string comparison
        List<String> diffList = new ArrayList<>();

        String[] oldLines = oldContent.split("\\r?\\n");
        String[] newLines = newContent.split("\\r?\\n");

        for (int i = 0; i < oldLines.length; i++) {
            if (i >= newLines.length || !oldLines[i].equals(newLines[i])) {
                diffList.add("- \t" + (i + 1) + ": " + oldLines[i]);
                if (i < newLines.length) {
                    diffList.add("+ \t" + (i + 1) + ": " + newLines[i]);
                }
            } else {
                diffList.add("  \t" + (i + 1) + ": " + oldLines[i]);
            }
        }

        if (oldLines.length < newLines.length) {
            for (int i = oldLines.length; i < newLines.length; i++) {
                diffList.add("+ \t" + (i + 1) + ": " + newLines[i]);
            }
        }

        return diffList;
    }

    private Repository openRepository(String repositoryPath) throws IOException {
        Path path = Paths.get(repositoryPath);
        return JgitApi.openRepository(path.toString()).getRepository();
    }

    private void loadChangesList() throws IOException, GitAPIException {
        changesListView.getItems().clear();

        // Выполняем команду git status
        StatusCommand statusCommand = git.status();
        Status status = statusCommand.call();

        // Добавляем измененные файлы в список изменений
        for (String changedFile : status.getModified()) {
            changesListView.getItems().add(changedFile);
        }
    }

    private void fillCommitChangesList(List<String> changes)  {
        commitChangesListView.getItems().clear();

        // Добавляем измененные файлы в список изменений
        for (String changedFile : changes) {
            commitChangesListView.getItems().add(changedFile);
        }
    }

    private void loadCommitHistory() throws GitAPIException {
        historyListView.getItems().clear();

        // Получаем список коммитов текущей ветки
        Iterable<RevCommit> commits = git.log().call();

        // Добавляем каждый коммит в историю
        for (RevCommit commit : commits) {
            historyListView.getItems().add(commit.getFullMessage());
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
        } catch (GitAPIException | IOException e) {
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



}



