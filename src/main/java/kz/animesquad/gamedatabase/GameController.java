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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private TableView<Game> tGameList;
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

    @FXML
    private AnchorPane filterAnchorPane;

    @FXML
    private VBox filterPanel;
    @FXML
    private void toggleFilters() {
        if (stage != null) {
            filterAnchorPane.setVisible(!filterAnchorPane.isVisible());

            if (filterAnchorPane.isVisible()) {
                stage.setWidth(860);
                stage.setHeight(621);
            } else {
                stage.setWidth(mainVBox.getPrefWidth());
                stage.setHeight(mainVBox.getPrefHeight());
            }
        }

    }

    @FXML
    private VBox mainVBox;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private ObservableList<Game> allGames = FXCollections.observableArrayList();

    @FXML
    public void checkConnection() { //Обновление таблицы
        ObservableList<Game> gameList = FXCollections.observableArrayList();

        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db();
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

                games.add(game);
                gameList.add(game);

            }

            allGames.addAll(gameList);

            tGameName.setCellValueFactory(new PropertyValueFactory<Game, String>("title"));
            tGameID.setCellValueFactory(new PropertyValueFactory<Game, Integer>("id"));
            tGameDeveloper.setCellValueFactory(new PropertyValueFactory<Game, String>("developer"));
            tGameGenre.setCellValueFactory(new PropertyValueFactory<Game, String>("genre"));
            tGamePublisher.setCellValueFactory(new PropertyValueFactory<Game, String>("publisher"));
            tGameYear.setCellValueFactory(new PropertyValueFactory<Game, Integer>("releaseYear"));

            tGameList.setItems(gameList);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void initialize() {

    }

    public void removeGameById(int gameId) {
        for (Game game : allGames) {
            if (game.getId() == gameId) {
                allGames.remove(game);
                break;
            }
        }
    }


    @FXML
    public void filterList() {
        ObservableList<Game> gameList = FXCollections.observableArrayList();

        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db();
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

            if (!searchGenre.isEmpty() || !searchGameName.isEmpty() || !searchYear.isEmpty() || !searchDev.isEmpty() || !searchPublisher.isEmpty()) {
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

                        games.add(game);
                        gameList.add(game);
                    }
                    tGameList.setItems(gameList);

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                tGameList.setItems(allGames);
            }

        } catch (NumberFormatException e) {
            System.out.println("Неверный формат года");
        }
    }

    public void addAllGame(Game game) {
    }
}