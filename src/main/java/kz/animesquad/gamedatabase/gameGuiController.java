package kz.animesquad.gamedatabase;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableView<Game> tableDatabase;

    @FXML
    private AnchorPane allGamesPanel;
    private Stage stage;

    @FXML
    private Button closeFiltersButton;

    @FXML
    private VBox filterPanel;

    @FXML
    private TextField AddGameDev;

    @FXML
    private TextField AddGameGenre;

    @FXML
    private TextField AddGameName;

    @FXML
    private TextField AddGamePublisher;

    @FXML
    private TextField AddGameYear;

    @FXML
    private Button btnAddGame;

    @FXML
    private TextField gameIDToDelete;

    @FXML
    private Button btnDeleteGame;


    @FXML
    private AnchorPane deleteGamePanel;

    @FXML
    private AnchorPane addGamePanel;

    private DbFunctions dbFunctions = new DbFunctions();

    @FXML
    void exit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void showAllGamesPanel(ActionEvent event) {
        allGamesPanel.setVisible(true);
        deleteGamePanel.setVisible(false);
        addGamePanel.setVisible(false);
        initialize();
    }

    @FXML
    void showAddGamePanel(ActionEvent event) {
        addGamePanel.setVisible(true);
        allGamesPanel.setVisible(false);
        deleteGamePanel.setVisible(false);
    }

    @FXML
    void showDeleteGamePanel(ActionEvent event) {
        deleteGamePanel.setVisible(true);
        allGamesPanel.setVisible(false);
        addGamePanel.setVisible(false);
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

    @FXML
    void addGame(ActionEvent event) {
        String title = AddGameName.getText();
        String developer = AddGameDev.getText();
        String publisher = AddGamePublisher.getText();
        int releaseYear = Integer.parseInt(AddGameYear.getText());
        String genre = AddGameGenre.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы уверены, что все данные введены правильно?");
        alert.setContentText("Игра: " + title + "\nРазработчик: " + developer + "\nИздатель: " + publisher +
                "\nГод выпуска: " + releaseYear + "\nЖанр: " + genre);

        ButtonType buttonTypeYes = new ButtonType("Да");
        ButtonType buttonTypeNo = new ButtonType("Нет");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            addToDatabaseAndList(title, developer, publisher, releaseYear, genre);
            Stage stage = (Stage) btnAddGame.getScene().getWindow();
        }
    }

    private ObservableList<Game> allGames = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ObservableList<Game> gameList = FXCollections.observableArrayList();
        allGamesPanel.setVisible(true);
        deleteGamePanel.setVisible(false);
        addGamePanel.setVisible(false);
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
                sql += " AND genre LIKE ?";
                searchGenre = "%" + searchGenre + "%";
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

    private void addToDatabaseAndList(String title, String developer, String publisher, int releaseYear, String genre) {
        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db();
        Alert bly = new Alert(Alert.AlertType.INFORMATION);
        bly.setTitle("Игра успешно добавлено");
        String sql = "INSERT INTO games (title, developer, publisher, release_date, genre) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, developer);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, releaseYear);
            pstmt.setString(5, genre);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                Alert inf = new Alert(Alert.AlertType.CONFIRMATION);
                inf.setTitle("Игра успешно добавлено");
                inf.setHeaderText("Ваша игра была успешно добавлено в базу данных");
                inf.setContentText("Игра: " + title + "\nРазработчик: " + developer + "\nИздатель: " + publisher +
                        "\nГод выпуска: " + releaseYear + "\nЖанр: " + genre);

                ButtonType buttonTypeOk = new ButtonType("OK");

                inf.getButtonTypes().setAll(buttonTypeOk);
                System.out.println("Игра успешно добавлена в базу данных.");

                GameController controller = new GameController();
                Game game = new Game(title, developer, publisher, releaseYear, genre);
                controller.addAllGame(game);
                Optional<ButtonType> infWindows = inf.showAndWait();
            } else {
                Alert fail = new Alert(Alert.AlertType.CONFIRMATION);
                fail.setTitle("Ошибка");
                fail.setHeaderText("Игра не была добавлено в база данных");

                ButtonType buttonTypeOk = new ButtonType("OK");

                fail.getButtonTypes().setAll(buttonTypeOk);

                System.out.println("Ошибка при добавлений игры в базу данных.");
                Optional<ButtonType> failWindows = fail.showAndWait();

            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }

    @FXML
    void clearDatabase(ActionEvent event) {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы уверены, что хотите очистить базу данных?");

        ButtonType buttonTypeYes = new ButtonType("Да");
        ButtonType buttonTypeNo = new ButtonType("Нет");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            DbFunctions db = new DbFunctions();
            String sql = "TRUNCATE TABLE games RESTART IDENTITY";
            try (Connection conn = db.connect_to_db();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    @FXML
    void deleteGame(ActionEvent event) {
        String gameIdStr = gameIDToDelete.getText();
        int gameId;
        try {
            gameId = Integer.parseInt(gameIdStr);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректный ID игры", "Пожалуйста, введите корректный ID игры (целое число).");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Вы уверены, что хотите удалить игру с ID: " + gameId + "?");

        ButtonType buttonTypeYes = new ButtonType("Да");
        ButtonType buttonTypeNo = new ButtonType("Нет");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            removeFromDatabase(gameId);
            Stage stage = (Stage) btnDeleteGame.getScene().getWindow();
            initialize();
        }
    }


    private void removeFromDatabase(int gameId) {
        DbFunctions db = new DbFunctions();
        Connection conn = db.connect_to_db();

        String sql = "DELETE FROM games WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Успешное удаление", "Игра успешно удалена", "Игра с ID " + gameId + " была успешно удалена из базы данных.");
                GameController controller = new GameController();
                controller.removeGameById(gameId);
            } else {
                showAlert("Ошибка удаления", "Игра не найдена", "Не удалось найти игру с ID " + gameId + ".");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}