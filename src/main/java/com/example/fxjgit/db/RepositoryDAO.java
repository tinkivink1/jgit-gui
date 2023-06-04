package com.example.fxjgit.db;

import com.example.fxjgit.db.entities.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**

 Класс RepositoryDAO предоставляет доступ к операциям сущности "репозиторий" в базе данных.
 */
public class RepositoryDAO implements ModelDAO<Repository> {
    private Connection connection;

    /**

     Конструктор класса RepositoryDAO.
     @param connection объект Connection, представляющий установленное соединение с базой данных
     */
    public RepositoryDAO(Connection connection) {
        this.connection = connection;
    }
    /**

     Метод getAll() возвращает список всех репозиториев из базы данных.
     @return список объектов Repository, содержащий все репозитории из базы данных
     */
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
    /**

     Метод getRepositoriesByUserId() возвращает список репозиториев, принадлежащих указанному пользователю.
     @param userId идентификатор пользователя
     @return список объектов Repository, содержащий репозитории, принадлежащие указанному пользователю
     */
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
    /**

     Метод getById() возвращает репозиторий с указанным идентификатором.
     @param repositoryId идентификатор репозитория
     @return объект Repository, представляющий репозиторий с указанным идентификатором
     */
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
    /**

     Метод getByPath() возвращает репозиторий с указанным путем.
     @param localPath локальный путь репозитория
     @return объект Repository, представляющий репозиторий с указанным путем
     */
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
    /**

     Метод add() добавляет новый репозиторий в базу данных.
     @param repository объект Repository, представляющий репозиторий для добавления
     @return true, если репозиторий успешно добавлен, false в противном случае
     */
    @Override
    public boolean add(Repository repository) {
        if (!getRepositoriesByUserId(repository.getUserId()).stream().anyMatch(el -> el.getLocalPath().equals(repository.getLocalPath()))) {
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
        }
        return false;
    }
    /**

     Метод update() обновляет информацию о репозитории в базе данных.
     @param repository объект Repository, представляющий репозиторий для обновления
     @return true, если информация о репозитории успешно обновлена, false в противном случае
     */
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
    /**

     Метод delete() удаляет репозиторий с указанным идентификатором из базы данных.
     @param repositoryId идентификатор репозитория для удаления
     @return true, если репозиторий успешно удален, false в противном случае
     */
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