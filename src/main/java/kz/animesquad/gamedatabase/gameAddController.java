package kz.animesquad.gamedatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javafx.stage.Stage;


public class gameAddController {

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
            stage.close();
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
    public void initialize() {

    }
}
