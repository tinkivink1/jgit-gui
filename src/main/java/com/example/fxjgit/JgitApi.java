package com.example.fxjgit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class JgitApi {

    public static Git initializeRepository(String path) {
        Git git = null;
        try {
            git = Git.init().setDirectory(new File(path)).call();
            System.out.println(git.toString());
        }
        catch (GitAPIException e){
            System.out.println(e.getStackTrace());
        }

        return git;
    }

    public static Git openRepository(String path){
        Git git = null;
        try {
            git = Git.open(new File(path));
            System.out.println(git.toString());
        }
        catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        return git;
    }

    public static Git cloneRepository(String url, String path) {
        Git git = null;
        try {
            git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(new File(path))
                    .call();
            System.out.println("Repository cloned successfully!");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return git;
    }
    public static void addFile(Git git, String filePattern){
        AddCommand addCommand  = git.add();
        addCommand.addFilepattern("filePattern");
        try {
            addCommand.call();
        } catch (GitAPIException e) {
            System.out.println(e.getStackTrace());
        }

    }

    public static Status checkStatus(Git git) {
        StatusCommand statusCommand = git.status();
        Status status = null;
        try {
            status = statusCommand.call();
        } catch (GitAPIException e) {
            System.out.println(e.getStackTrace());
        }

        return status;
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

    public static String getStatusString(Status status){
        StringBuilder sb = new StringBuilder();


        for(String i: status.getAdded()) {
            System.out.println("Added: " + i);
            sb.append("Added: " + i + "\n");
        }

        for(String i: status.getRemoved()) {
            System.out.println("Removed: " + i);
            sb.append("Removed: " + i + "\n");
        }

        for(String i: status.getModified()) {
            System.out.println("Modified: " + i);
            sb.append("Modified: " + i + "\n");
        }

        for(String i: status.getChanged()){
            System.out.println("Changed: " + i);
            sb.append("Changed: " + i + "\n");
        }

        for(String i: status.getUntracked()) {
            System.out.println("Untracked: " + i);
            sb.append("Untracked: " + i + "\n");
        }

        for(String i: status.getConflicting()){
            System.out.println("Conflicting: " + i);
            sb.append("Conflicting: " + i + "\n");
        }

        for(String i: status.getMissing()) {
            System.out.println("Missing: " + i);
            sb.append("Missing: " + i + "\n");
        }

        return sb.toString();
    }
}