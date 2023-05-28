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

    public Repository(int userId, String remoteLink, String localPath) {
        this.userId = userId;
        this.remoteLink = remoteLink;
        this.localPath = localPath;
    }

    public Repository(int userId, String localPath) {
        this.userId = userId;
        this.remoteLink = "";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repository that = (Repository) o;

        if (!localPath.equals(that.getLocalPath()) || !remoteLink.equals(that.getRemoteLink()) || userId != that.getUserId())
            return false;

        return true;
    }
}