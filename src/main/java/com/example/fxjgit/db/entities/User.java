package com.example.fxjgit.db.entities;

import java.util.List;

public class User {
    private int userId;
    private String username;
    private String password;
    private List<Repository> repositories;
    private String secret;

    public User(int userId, String username, String password, List<Repository> repositories, String secret) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.repositories = repositories;
        this.secret = secret;
    }

    public User(String username, String password, List<Repository> repositories, String secret) {
        this.username = username;
        this.password = password;
        this.repositories = repositories;
        this.secret = secret;
    }

    public User(String username, String password, String secret) {
        this.username = username;
        this.password = password;
        this.secret = secret;
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;

        if (!username.equals(that.username)) return false;
        return password.equals(that.password);
    }
}