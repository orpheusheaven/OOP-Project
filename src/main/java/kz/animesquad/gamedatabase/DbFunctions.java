package kz.animesquad.gamedatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbFunctions implements DatabaseOperations {
    private String connectionUrl = "jdbc:postgresql://localhost:5432/Games";
    private static final String DRIVER = "org.postgresql.Driver";
    private String username = "postgres";
    private String password = "god1sdead";

    @Override
    public void addGame(Game game) {
        // Реализация добавления игры в базу данных
        String sql = "INSERT INTO games (title, developer, publisher, release_year, genre) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect_to_db();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, game.getTitle());
            pstmt.setString(2, game.getDeveloper());
            pstmt.setString(3, game.getPublisher());
            pstmt.setInt(4, game.getReleaseYear());
            pstmt.setString(5, game.getGenre());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteGame(int gameId) {
        String sql = "DELETE FROM games WHERE id = ?";

        try (Connection conn = connect_to_db();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public Connection connect_to_db() {
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
