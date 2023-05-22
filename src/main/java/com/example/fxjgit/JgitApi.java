package com.example.fxjgit;

import javafx.event.ActionEvent;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

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