package kz.animesquad.gamedatabase;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class gameDeleteController {

    @FXML
    private TextField gameIDToDelete;

    @FXML
    private Button btnDeleteGame;

    @FXML
    private Button btnCancelDelete;

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
            stage.close();
        }
    }

    @FXML
    void cancelDelete(ActionEvent event) {
        Stage stage = (Stage) btnCancelDelete.getScene().getWindow();
        stage.close();
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

    @FXML
    public void initialize() {

    }
}