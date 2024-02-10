package kz.animesquad.gamedatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbFunctions {
    private String connectionUrl = "jdbc:postgresql://localhost:5432/Books";
    private static final String DRIVER = "org.postgresql.Driver";

    public Connection connect_to_db(String username, String password) {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(connectionUrl, username, password);
            System.out.println("Соединение с базой данных успешно установлено.");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер базы данных не найден: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Ошибка соединения с базой данных: " + e.getMessage());
        }
        return conn;
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Соединение с базой данных успешно закрыто.");
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии соединения с базой данных: " + e.getMessage());
            }
        }
    }
}
