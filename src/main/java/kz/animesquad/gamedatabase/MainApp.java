package kz.animesquad.gamedatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/kz/animesquad/gamedatabase/main.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Игровая База Данных");
        primaryStage.setScene(new Scene(root, 840, 360));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
