package com.example.fxjgit.db;

import com.example.fxjgit.db.entities.Repository;
import com.example.fxjgit.db.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBRepository implements ModelDAO<Repository> {
    private static DBRepository instance;
    private Connection connection;

    private DBRepository(String dbUrl, String dbUser, String dbPassword) {
        connection = ConnectionDB.connect(dbUrl, dbUser, dbPassword);
    }

    private DBRepository(String dbUrl) {
        connection = ConnectionDB.connect(dbUrl);
    }

    public static synchronized DBRepository getInstance(String dbUrl, String dbUser, String dbPassword) {
        if (instance == null) {
            instance = new DBRepository(dbUrl, dbUser, dbPassword);
        }
        return instance;
    }

    public static synchronized DBRepository getInstance(String dbUrl) {
        if (instance == null) {
            instance = new DBRepository(dbUrl);
        }
        return instance;
    }

    public static synchronized DBRepository getInstance() {
        if (instance == null) {
            throw new NullPointerException("DB connection is not established");
        }
        return instance;
    }
    @Override
    public List<Repository> getAll() {
        List<Repository> repositories = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Repositories");
            while (resultSet.next()) {
                int repositoryId = resultSet.getInt("RepositoryId");
                int userId = resultSet.getInt("UserId");
                String remoteLink = resultSet.getString("RemoteLink");
                String localPath = resultSet.getString("LocalPath");
                Repository repository = new Repository(repositoryId, userId, remoteLink, localPath);
                repositories.add(repository);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repositories;
    }

    public List<Repository> getRepositoriesByUserId(int userId) {
        List<Repository> repositories = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Repositories WHERE UserId = ?");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int repositoryId = resultSet.getInt("RepositoryId");
                String remoteLink = resultSet.getString("RemoteLink");
                String localPath = resultSet.getString("LocalPath");
                Repository repository = new Repository(repositoryId, userId, remoteLink, localPath);
                repositories.add(repository);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repositories;
    }

    @Override
    public Repository getById(int repositoryId) {
        Repository repository = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Repositories WHERE RepositoryId = ?");
            statement.setInt(1, repositoryId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("UserId");
                String remoteLink = resultSet.getString("RemoteLink");
                String localPath = resultSet.getString("LocalPath");
                repository = new Repository(repositoryId, userId, remoteLink, localPath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repository;
    }

    public Repository getByPath(String localPath) {
        Repository repository = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Repositories WHERE LocalPath = ?");
            statement.setString(1, localPath);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int repositoryId = resultSet.getInt("RepositoryId");
                int userId = resultSet.getInt("UserId");
                String remoteLink = resultSet.getString("RemoteLink");
                repository = new Repository(repositoryId, userId, remoteLink, localPath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repository;
    }

    @Override
    public boolean add(Repository repository) {
        if(!getRepositoriesByUserId(repository.getUserId()).stream().anyMatch(el -> el.getLocalPath() == repository.getLocalPath()))
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Repositories (UserId, RemoteLink, LocalPath) VALUES (?, ?, ?)");
                statement.setInt(1, repository.getUserId());
                statement.setString(2, repository.getRemoteLink());
                statement.setString(3, repository.getLocalPath());
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return false;
    }

    @Override
    public boolean update(Repository repository) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Repositories SET UserId = ?, RemoteLink = ?, LocalPath = ? WHERE RepositoryId = ?");
            statement.setInt(1, repository.getUserId());
            statement.setString(2, repository.getRemoteLink());
            statement.setString(3, repository.getLocalPath());
            statement.setInt(4, repository.getRepositoryId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int repositoryId) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Repositories WHERE RepositoryId = ?");
            statement.setInt(1, repositoryId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}