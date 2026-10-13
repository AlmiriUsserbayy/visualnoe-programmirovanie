module com.example.studentmgmt {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.studentmgmt to javafx.fxml;
    opens com.example.studentmgmt.model to javafx.base;
    opens com.example.studentmgmt.controller to javafx.fxml;

    exports com.example.studentmgmt;
}