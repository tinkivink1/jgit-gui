package com.example.fxjgit.db.entities;

public class Repository {
    private int repositoryId;
    private int userId;
    private String remoteLink;
    private String localPath;

    public Repository(int repositoryId, int userId, String remoteLink, String localPath) {
        this.repositoryId = repositoryId;
        this.userId = userId;
        this.remoteLink = remoteLink;
        this.localPath = localPath;
    }

    // Геттеры и сеттеры
    public int getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRemoteLink() {
        return remoteLink;
    }

    public void setRemoteLink(String remoteLink) {
        this.remoteLink = remoteLink;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}