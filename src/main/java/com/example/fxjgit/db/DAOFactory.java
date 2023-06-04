package com.example.fxjgit.db;



import java.sql.Connection;

public class DAOFactory {
//    public static final String DB_URL = "jdbc:postgresql://localhost:5432/gituserdb";
//    public static final String DB_USER = "root";
//    public static final String DB_PASS = "root";

    DAOFactory(){}

    private static Connection connection = null;
    // Список типов DAO, поддерживаемых генератором
    // Здесь будет метод для каждого DAO, который может быть
    // создан. Реализовывать эти методы
    // должны конкретные генераторы.

    // метод для создания соединений к Cloudscape
    public static Connection createConnection(String DB_URL, String DB_USER, String DB_PASS) {
        connection = ConnectionDB.connect(DB_URL, DB_USER, DB_PASS);
        return connection;
    }
    // метод для создания соединений к Cloudscape
    public static Connection createConnection(String DB_URL) {
        connection = ConnectionDB.connect(DB_URL);
        return connection;
    }


    public static UserDAO getUserDAO(){
        if(connection != null){
            UserDAO userDAO = new UserDAO(connection);
            return userDAO;
        }
        return null;
    }
    public static RepositoryDAO getRepositoryDAO(){
        if(connection != null)
            return new RepositoryDAO(connection);
        return null;
    }
}
