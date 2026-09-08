package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML private TextField txtFullName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAge;
    @FXML private ComboBox<String> cmbCity;
    @FXML private Label lblResult;

    @FXML
    public void initialize() {
        // Заполняем города (минимум 5 по заданию)
        cmbCity.getItems().addAll("Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань");
        cmbCity.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCreateClick() {
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String ageStr = txtAge.getText().trim();
        String city = cmbCity.getValue();

        // Проверка пустых полей
        if (fullName.isBlank() || phone.isBlank() || email.isBlank() || ageStr.isBlank()) {
            showError("Заполните все поля формы.");
            return;
        }

        // Проверка возраста на число
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showError("Возраст должен быть числом.");
            return;
        }

        if (age < 18 || age > 120) {
            showError("Возраст клиента должен быть от 18 до 120 лет.");
            return;
        }

        // Вывод результата в рамку
        lblResult.setText(
                "=== КАРТОЧКА КЛИЕНТА ===\n" +
                        "ФИО: " + fullName + "\n" +
                        "Телефон: " + phone + "\n" +
                        "Email: " + email + "\n" +
                        "Возраст: " + age + " лет\n" +
                        "Город: " + city
        );
    }

    @FXML
    private void onClearClick() {
        txtFullName.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtAge.clear();
        cmbCity.getSelectionModel().selectFirst();
        lblResult.setText("");
        txtFullName.requestFocus();
    }

    @FXML
    private void onExitClick() {
        Platform.exit();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
