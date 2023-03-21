module com.company.checkers {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.company.checkers to javafx.fxml;
    exports com.company.checkers;
}