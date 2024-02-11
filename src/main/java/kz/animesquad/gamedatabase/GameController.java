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

    // Переменная для хранения всех игр
    private ObservableList<Game> allGames = FXCollections.observableArrayList();

    @FXML
    private void WinAddGame() {
        try {
            // Загрузка FXML для второго окна
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/kz/animesquad/gamedatabase/game-form.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 649, 327); // Здесь указывайте предпочтительные размеры
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
        refreshGameList();
    }

    @FXML
    public void initialize() {
        tGameName.setCellValueFactory(new PropertyValueFactory<>("title"));
        tGameID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tGameDeveloper.setCellValueFactory(new PropertyValueFactory<>("developer"));
        tGameGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        tGamePublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        tGameYear.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

        refreshGameList();
    }

    @FXML
    public void filterList() {
        ObservableList<Game> filteredList = filterGames();
        tGameList.setItems(filteredList);
    }

    private ObservableList<Game> filterGames() {
        ObservableList<Game> filteredList = FXCollections.observableArrayList();
        String searchGenre = filterGenre.getText();
        String searchGameName = filterGameName.getText();
        String searchYear = filterYear.getText();
        String searchDev = filterDev.getText();
        String searchPublisher = filterPublisher.getText();

        for (Game game : allGames) {
            boolean addToFilteredList = true;

            if (!searchGenre.isEmpty() && !game.getGenre().equalsIgnoreCase(searchGenre)) {
                addToFilteredList = false;
            }

            if (!searchGameName.isEmpty() && !game.getTitle().toLowerCase().contains(searchGameName.toLowerCase())) {
                addToFilteredList = false;
            }



            if (addToFilteredList) {
                filteredList.add(game);
            }
        }

        return filteredList;
    }

    @FXML
    public void refreshGameList() {
        ObservableList<Game> gameList = FXCollections.observableArrayList();
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

                outputArea.setText(outputArea.getText() + "\n " + game.getId() + ". " + game.getTitle());
                games.add(game);
                gameList.add(game);
            }
            tGameList.setItems(gameList);
            allGames = gameList;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addAllGame(Game game) {
        allGames.add(game);
    }


}