module kz.animesquad.gamedatabase {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens kz.animesquad.gamedatabase to javafx.fxml;
    exports kz.animesquad.gamedatabase;
}