module kz.atu.lab10 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind; // Разрешаем доступ к JSON-библиотеке

    opens kz.atu.lab10 to javafx.fxml, com.fasterxml.jackson.databind;
    exports kz.atu.lab10;
}
