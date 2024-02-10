package kz.animesquad.gamedatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class GameController {
    @FXML
    private TextField titleField;
    @FXML
    private TextField developerField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField releaseYearField;
    @FXML
    private TextField genreField;
    @FXML
    private Button addButton;
    @FXML
    private TextArea outputArea;

    @FXML
    private TableView<GameFunctions> tGameList;
    @FXML
    private TableColumn<Game, String> tGameDeveloper;

    @FXML
    private TableColumn<Game, String> tGameGenre;

    @FXML
    private TableColumn<Game, Integer> tGameID;

    @FXML
    private TableColumn<Game, String> tGameName;

    @FXML
    private TableColumn<Game, String> tGamePublisher;

    @FXML
    private TextField filterGenre;

    @FXML
    private TableColumn<Game, Integer> tGameYear;

    @FXML
    private TextField filterGameName;

    @FXML
    private TextField filterDev;

    @FXML
    private TextField filterPublisher;

    @FXML
    private TextField filterYear;

    @FXML
    private Button btnFilter;

    // Переменная для хранения всех игр
    private ObservableList<GameFunctions> allGames = FXCollections.observableArrayList();

    @FXML
    private void addGame() {
        try {
            // Загрузка FXML для второго окна
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/kz/animesquad/gamedatabase/game-form.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 300, 200); // Здесь указывайте предпочтительные размеры
            Stage stage = new Stage();
            stage.setTitle("Второе окно");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String title = titleField.getText();
        String developer = developerField.getText();
        // и так далее для остальных полей
        outputArea.setText("Игра добавлена: " + title); // Простой пример вывода
    }

    @FXML
    public void checkConnection() {
        ObservableList<GameFunctions> gameList = FXCollections.observableArrayList();
        outputArea.setText("СПИСОК ИГР: ");
        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db("postgres", "god1sdead");
        List<Game> games = new ArrayList<>();

        String sql = "SELECT * FROM games";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Game game = new Game(
                        rs.getString("title"),
                        rs.getString("developer"),
                        rs.getString("publisher"),
                        rs.getInt("release_date"),
                        rs.getString("genre"));
                game.setId(rs.getInt("id"));

                tGameName.setCellValueFactory(new PropertyValueFactory<Game, String>("title"));
                tGameID.setCellValueFactory(new PropertyValueFactory<Game, Integer>("id"));
                tGameDeveloper.setCellValueFactory(new PropertyValueFactory<Game, String>("developer"));
                tGameGenre.setCellValueFactory(new PropertyValueFactory<Game, String>("genre"));
                tGamePublisher.setCellValueFactory(new PropertyValueFactory<Game, String>("publisher"));
                tGameYear.setCellValueFactory(new PropertyValueFactory<Game, Integer>("releaseYear"));

                outputArea.setText(outputArea.getText() + "\n " + game.getId() + ". " + game.getTitle());
                games.add(game);
                gameList.add(game);
            }

            allGames.addAll(gameList);

            tGameList.setItems(gameList);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void initialize() {

    }


    @FXML
    public void filterList() {
        ObservableList<GameFunctions> gameList = FXCollections.observableArrayList();
        outputArea.setText("СПИСОК ИГР: ");
        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db("postgres", "god1sdead");
        List<Game> games = new ArrayList<>();

        try {
            String searchGenre = filterGenre.getText();
            String searchGameName = filterGameName.getText();
            String searchYear = filterYear.getText();
            String searchDev = filterDev.getText();
            String searchPublisher = filterPublisher.getText();

            String sql = "SELECT * FROM games WHERE 1 = 1";

            if (!searchGenre.isEmpty()) {
                sql += " AND genre = ?";
            }
            if (!searchGameName.isEmpty()) {
                sql += " AND title LIKE ?";
                searchGameName = "%" + searchGameName + "%";
            }
            if (!searchYear.isEmpty()) {
                sql += " AND release_date = ?";
            }
            if (!searchDev.isEmpty()) {
                sql += " AND developer LIKE ?";
                searchDev = "%" + searchDev + "%";
            }
            if (!searchPublisher.isEmpty()) {
                sql += " AND publisher LIKE ?";
                searchPublisher = "%" + searchPublisher + "%";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int parameterIndex = 1;
                if (!searchGenre.isEmpty()) {
                    pstmt.setString(parameterIndex++, searchGenre);
                }
                if (!searchGameName.isEmpty()) {
                    pstmt.setString(parameterIndex++, searchGameName);
                }
                if (!searchYear.isEmpty()) {
                    pstmt.setInt(parameterIndex++, Integer.parseInt(searchYear));
                }
                if (!searchDev.isEmpty()) {
                    pstmt.setString(parameterIndex++, searchDev);
                }
                if (!searchPublisher.isEmpty()) {
                    pstmt.setString(parameterIndex++, searchPublisher);
                }

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Game game = new Game(
                            rs.getString("title"),
                            rs.getString("developer"),
                            rs.getString("publisher"),
                            rs.getInt("release_date"),
                            rs.getString("genre"));
                    game.setId(rs.getInt("id"));

                    tGameName.setCellValueFactory(new PropertyValueFactory<Game, String>("title"));
                    tGameID.setCellValueFactory(new PropertyValueFactory<Game, Integer>("id"));
                    tGameDeveloper.setCellValueFactory(new PropertyValueFactory<Game, String>("developer"));
                    tGameGenre.setCellValueFactory(new PropertyValueFactory<Game, String>("genre"));
                    tGamePublisher.setCellValueFactory(new PropertyValueFactory<Game, String>("publisher"));
                    tGameYear.setCellValueFactory(new PropertyValueFactory<Game, Integer>("releaseYear"));

                    outputArea.setText(outputArea.getText() + "\n " + game.getId() + ". " + game.getTitle());
                    games.add(game);
                    gameList.add(game);
                }
                tGameList.setItems(gameList);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }


            if (gameList.isEmpty()) {
                tGameList.setItems(allGames);
            }

        } catch (NumberFormatException e) {
            System.out.println("Неверный формат года");
        }
    }
}