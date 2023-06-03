package com.example.fxjgit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

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
