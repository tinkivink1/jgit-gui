package com.example.fxjgit.db;



import java.sql.Connection;

public class DAOFactory {


    DAOFactory(){}

    private static Connection connection = null;


    /**

     Метод createConnection() создает соединение с базой данных, используя указанный URL, имя пользователя и пароль.
     @param DB_URL URL базы данных
     @param DB_USER имя пользователя для аутентификации
     @param DB_PASS пароль для аутентификации
     @return объект Connection, представляющий установленное соединение с базой данных
     */
    public static Connection createConnection(String DB_URL, String DB_USER, String DB_PASS) {
        connection = ConnectionDB.connect(DB_URL, DB_USER, DB_PASS);
        return connection;
    }

    /**

     Метод createConnection() создает соединение с базой данных, используя указанный URL, имя пользователя и пароль.
     @param DB_URL URL базы данных
     @return объект Connection, представляющий установленное соединение с базой данных
     */
    public static Connection createConnection(String DB_URL) {
        connection = ConnectionDB.connect(DB_URL);
        return connection;
    }

    /**

     Метод getUserDAO() возвращает объект UserDAO, предоставляющий доступ к операциям сущности "пользователь" в базе данных.
     @return объект UserDAO для выполнения операций сущности "пользователь"
     */
    public static UserDAO getUserDAO(){
        if(connection != null){
            UserDAO userDAO = new UserDAO(connection);
            return userDAO;
        }
        return null;
    }

    /**

     Метод getRepositoryDAO() возвращает объект RepositoryDAO, предоставляющий доступ к операциям сущности "репозиторий" в базе данных.
     @return объект RepositoryDAO для выполнения операций сущности "репозиторий"
     */
    public static RepositoryDAO getRepositoryDAO(){
        if(connection != null)
            return new RepositoryDAO(connection);
        return null;
    }
}
