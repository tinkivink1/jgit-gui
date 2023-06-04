package com.example.fxjgit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    /**
     Метод connect() устанавливает соединение с базой данных по указанному URL, используя указанное имя пользователя и пароль.
     @param dbUrl URL базы данных
     @param dbUser имя пользователя для аутентификации
     @param dbPassword пароль для аутентификации
     @return объект Connection, представляющий установленное соединение с базой данных
     @throws SQLException если происходит ошибка SQL при установлении соединения
     */
    public static Connection connect(String dbUrl, String dbUser, String dbPassword) {
        Connection connection = null;
        try {
            // Загружаем драйвер JDBC
            Class.forName("org.postgresql.Driver");
            // Устанавливаем соединение
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Подключение успешно
            System.out.println("Соединение с базой данных установлено.");
        } catch (ClassNotFoundException e) {
            System.out.println("Не удалось найти драйвер JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к базе данных.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     Метод connect() устанавливает соединение с базой данных по указанному URL.
     @param dbUrl URL базы данных
     @return объект Connection, представляющий установленное соединение с базой данных
     @throws SQLException если происходит ошибка SQL при установлении соединения
     */
    public static Connection connect(String dbUrl) {
        Connection connection = null;
        try {
            // Загружаем драйвер JDBC
            Class.forName("org.postgresql.Driver");

            // Устанавливаем соединение
            connection = DriverManager.getConnection(dbUrl);

            // Подключение успешно
            System.out.println("Соединение с базой данных установлено.");
        } catch (ClassNotFoundException e) {
            System.out.println("Не удалось найти драйвер JDBC.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к базе данных.");
            e.printStackTrace();
        }
        return connection;
    }
}
