module kz.atu.lab04 {
    requires javafx.controls;
    requires javafx.fxml;


    opens kz.atu.lab04 to javafx.fxml;
    exports kz.atu.lab04;
}