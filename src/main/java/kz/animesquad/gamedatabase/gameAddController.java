package kz.animesquad.gamedatabase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.cell.PropertyValueFactory;

public class gameAddController extends GameController {


    @FXML
    private Button btnYes;

    @FXML
    private Label titleYes;

    @FXML
    private TextField fNumber;


    @FXML
    void Yes(ActionEvent event) {
        int x, y;
        x = 2;
        y = Integer.parseInt(fNumber.getText());
        titleYes.setText(String.valueOf(y*y));


    }
    @FXML
    public void initialize() {

    }
}
