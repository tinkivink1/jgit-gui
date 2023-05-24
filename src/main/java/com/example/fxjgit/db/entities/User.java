package com.example.fxjgit.db.entities;

import java.util.List;

public class User {
    private int userId;
    private String username;
    private String password;
    private List<Repository> repositories;

    public User(int userId, String username, String password, List<Repository> repositories) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.repositories = repositories;
    }

    // Геттеры и сеттеры
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
}