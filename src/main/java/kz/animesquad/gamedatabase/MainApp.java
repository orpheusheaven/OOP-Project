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
        Parent root = FXMLLoader.load(getClass().getResource("/kz/animesquad/gamedatabase/main.fxml"));
        primaryStage.setTitle("Игровая База Данных");
        primaryStage.setScene(new Scene(root, 812, 621));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
