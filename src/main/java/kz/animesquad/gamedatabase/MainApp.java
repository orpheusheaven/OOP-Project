package kz.animesquad.gamedatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class MainApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/kz/animesquad/gamedatabase/gamedatabase.fxml"));
        Parent root = (Parent)loader.load();
        gameGuiController controller = (gameGuiController)loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Игровая База Данных");
        primaryStage.setScene(new Scene(root, 1000, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
