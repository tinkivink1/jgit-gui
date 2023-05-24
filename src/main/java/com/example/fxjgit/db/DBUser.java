package com.example.fxjgit.db;

import com.example.fxjgit.db.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUser implements ModelDAO<User> {
    Connection connection;

    public DBUser(String dbUrl, String dbUser, String dbPassword) {
        connection = ConnectionDB.connect(dbUrl, dbUser, dbPassword);
    }
    public DBUser(String dbUrl) {
        connection = ConnectionDB.connect(dbUrl);
    }
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Users");
            while (resultSet.next()) {
                int userId = resultSet.getInt("UserId");
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                User user = new User(userId, username, password, null);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User getById(int id) {
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE UserId = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                user = new User(id, username, password, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    @Override
    public void add(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (UserId, Username, Password) VALUES (?, ?, ?)");
            statement.setInt(1, user.getUserId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE Users SET Username = ?, Password = ? WHERE UserId = ?");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Users WHERE UserId = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}