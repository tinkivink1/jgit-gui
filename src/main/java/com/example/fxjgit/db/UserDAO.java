package com.example.fxjgit.db;

import com.example.fxjgit.db.entities.Repository;
import com.example.fxjgit.db.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements ModelDAO<User> {
    private Connection connection;

    private UserDAO(String dbUrl, String dbUser, String dbPassword) {
        connection = ConnectionDB.connect(dbUrl, dbUser, dbPassword);
    }

    private UserDAO(String dbUrl) {
        connection = ConnectionDB.connect(dbUrl);
    }

    public UserDAO(Connection connection){
        this.connection = connection;
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
                RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
                User user = new User(userId, username, password, repositoryDAO.getRepositoriesByUserId(userId));
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
                // Заполняем репозитории пользователя
                RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
                List<Repository> repositories = repositoryDAO.getRepositoriesByUserId(id);
                user.setRepositories(repositories);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getByUsername(String usernameToFind) {
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE Username = ?");
            statement.setString(1, usernameToFind);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("userid");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                user = new User(id, username, password, null);
                // Заполняем репозитории пользователя
                RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
                List<Repository> repositories = repositoryDAO.getRepositoriesByUserId(id);
                user.setRepositories(repositories);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean add(User user) {
        if (getByUsername(user.getUsername()) == null) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (Username, Password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    user.setUserId(userId);

                    // Записываем репозитории пользователя в базу данных
                    List<Repository> repositories = user.getRepositories();
                    if (repositories != null) {
                        RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
                        for (Repository repository : repositories) {
                            repository.setUserId(userId);
                            repositoryDAO.add(repository);
                        }
                    }

                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean update(User user) {
        User existingUser = getByUsername(user.getUsername());
        if (existingUser != null) {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE Users SET Username = ?, Password = ? WHERE UserId = ?");
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setInt(3, user.getUserId());
                statement.executeUpdate();

                // Получаем список репозиториев пользователя из базы данных
                RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
                List<Repository> existingRepositories = repositoryDAO.getRepositoriesByUserId(user.getUserId());

                // Получаем новый список репозиториев пользователя
                List<Repository> updatedRepositories = user.getRepositories();

                // Удаляем репозитории, которые были удалены из списка
                for (Repository existingRepository : existingRepositories) {
                    if (!updatedRepositories.contains(existingRepository)) {
                        repositoryDAO.delete(existingRepository.getRepositoryId());
                    }
                }

                // Обновляем информацию о существующих репозиториях
                for (Repository updatedRepository : updatedRepositories) {
                    if (existingRepositories.contains(updatedRepository)) {
                        repositoryDAO.update(updatedRepository);
                    }
                }

                // Добавляем новые репозитории
                for (Repository updatedRepository : updatedRepositories) {
                    if (!existingRepositories.contains(updatedRepository)) {
                        updatedRepository.setUserId(user.getUserId());
                        repositoryDAO.add(updatedRepository);
                    }
                }

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        User user = getById(id);
        if (user != null) {
            try {
                // Получаем список репозиториев пользователя из базы данных
                RepositoryDAO repositoryDAO = DAOFactory.getRepositoryDAO();
                List<Repository> repositories = repositoryDAO.getRepositoriesByUserId(id);

                // Удаляем все репозитории пользователя из базы данных
                for (Repository repository : repositories) {
                    repositoryDAO.delete(repository.getRepositoryId());
                }

                // Удаляем информацию о пользователе из таблицы Users
                PreparedStatement statement = connection.prepareStatement("DELETE FROM Users WHERE UserId = ?");
                statement.setInt(1, id);
                statement.executeUpdate();

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}