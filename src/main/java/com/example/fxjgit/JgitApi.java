package com.example.fxjgit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static Git cloneRepository(CredentialsProvider credentialsProvider, String url, String path) {
        Git git = null;
        try {
            TransportConfigCallback transportConfigCallback = transport -> {
                if (transport instanceof TransportHttp) {
                    ((TransportHttp) transport).setCredentialsProvider(credentialsProvider);
                }
            };
            git = Git.cloneRepository()
                    .setURI(url)
                    .setTransportConfigCallback(transportConfigCallback)
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
        addCommand.addFilepattern(filePattern);
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

    public static List<String> getCurrentDiffs(Git git, String filename) throws IOException {
        Repository repository = git.getRepository();
        // Get the latest commit
        RevCommit commit = getLatestCommit(repository);

        // Get the file content from the latest commit
        String lastCommitContent = getFileContentFromCommit(repository, commit, filename);

        // Get the current file content
        Path currentPath = Paths.get(git.getRepository().getDirectory() + "\\..\\", filename);
        String currentContent = new String(Files.readAllBytes(currentPath));

        // Compare the contents and generate the diff
        List<String> diffList = generateDiff(lastCommitContent, currentContent);

        return diffList;
    }

    public static String getFileContentFromCommit(Repository repository, RevCommit commit, String fileName) throws IOException {
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

    public static RevCommit getLatestCommit(Repository repository) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve("HEAD"));
            return commit;
        }
    }

    public static RevCommit findCommitByMessage(Git git, String commitMessage) throws IOException {
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

    public static List<String> generateDiff(String oldContent, String newContent) {
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

    public static void commit(Git git, String commitMessage) throws GitAPIException {
        CommitCommand commitCommand = git.commit();
        commitCommand.setMessage(commitMessage);
        commitCommand.call();
    }
}