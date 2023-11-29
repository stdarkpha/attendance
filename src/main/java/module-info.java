module project.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens project.app to javafx.fxml;
    exports project.app;
}