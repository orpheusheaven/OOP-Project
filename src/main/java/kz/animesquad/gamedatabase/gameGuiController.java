package kz.animesquad.gamedatabase;

import javafx.application.Platform;
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

public class gameGuiController {

    @FXML
    private Button addGameButton;

    @FXML
    private Button allGamesButton;

    @FXML
    private Button deleteGameButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button filterPanelButton;

    @FXML
    private TableColumn<Game, String> tableDeveloper;

    @FXML
    private TableColumn<Game, String> tableGenre;

    @FXML
    private TableColumn<Game, Integer> tableID;

    @FXML
    private TableColumn<Game, String> tablePublisher;

    @FXML
    private TableColumn<Game, String> tableTitle;

    @FXML
    private TableColumn<Game, Integer> tableYear;

    @FXML
    private TableView<GameFunctions> tableDatabase;

    @FXML
    private AnchorPane allGamesPanel;
    private Stage stage;

    @FXML
    private Button closeFiltersButton;

    @FXML
    private VBox filterPanel;
    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void showAddGamePanel(ActionEvent event) {
        allGamesPanel.setVisible(false);
    }

    @FXML
    void showAllGamesPanel(ActionEvent event) {
        allGamesPanel.setVisible(true);
    }

    @FXML
    void showDeleteGamePanel(ActionEvent event) {
        allGamesPanel.setVisible(false);
    }

    @FXML
    void showFilterPanel(ActionEvent event) {
        filterPanel.setVisible(true);
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void closeFilterPanel(ActionEvent event) {
        filterPanel.setVisible(false);

    }
    private ObservableList<GameFunctions> allGames = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        ObservableList<GameFunctions> gameList = FXCollections.observableArrayList();

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

                games.add(game);
                gameList.add(game);

            }

            allGames.addAll(gameList);

            tableTitle.setCellValueFactory(new PropertyValueFactory<Game, String>("title"));
            tableID.setCellValueFactory(new PropertyValueFactory<Game, Integer>("id"));
            tableDeveloper.setCellValueFactory(new PropertyValueFactory<Game, String>("developer"));
            tableGenre.setCellValueFactory(new PropertyValueFactory<Game, String>("genre"));
            tablePublisher.setCellValueFactory(new PropertyValueFactory<Game, String>("publisher"));
            tableYear.setCellValueFactory(new PropertyValueFactory<Game, Integer>("releaseYear"));

            tableDatabase.setItems(gameList);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private TextField filterDev;

    @FXML
    private TextField filterGameName;

    @FXML
    private TextField filterGenre;

    @FXML
    private TextField filterPublisher;

    @FXML
    private TextField filterYear;
    @FXML
    public void filterList() {
        ObservableList<GameFunctions> gameList = FXCollections.observableArrayList();

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
                    tableDatabase.setItems(gameList);

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                tableDatabase.setItems(allGames);
            }

        } catch (NumberFormatException e) {
            System.out.println("Неверный формат года");
        }
    }
}
