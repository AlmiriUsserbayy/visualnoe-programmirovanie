package com.example.studentmgmt.controller;

import com.example.studentmgmt.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StudentFormController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField groupField;
    @FXML private TextField gpaField;
    @FXML private DialogPane dialogPane;

    private Stage dialogStage;
    private Student student;
    private boolean okClicked = false;

    @FXML
    public void initialize() {
        Button saveButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        if (saveButton == null && !dialogPane.getButtonTypes().isEmpty()) {
            saveButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(0));
        }

        if (saveButton != null) {
            saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                if (!isInputValid()) {
                    event.consume(); // Блокируем закрытие, если данные некорректны
                } else {
                    student.setId(idField.getText());
                    student.setFullName(nameField.getText());
                    student.setGroup(groupField.getText());
                    student.setGpa(Double.parseDouble(gpaField.getText()));
                    okClicked = true;
                    dialogStage.close();
                }
            });
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setStudent(Student student) {
        this.student = student;
        idField.setText(student.getId());
        nameField.setText(student.getFullName());
        groupField.setText(student.getGroup());
        gpaField.setText(student.getGpa() == 0.0 ? "" : String.valueOf(student.getGpa()));
    }

    public boolean isOkClicked() { return okClicked; }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (idField.getText() == null || idField.getText().trim().isEmpty()) errorMessage.append("Укажите ID!\n");
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) errorMessage.append("Укажите ФИО!\n");
        if (groupField.getText() == null || groupField.getText().trim().isEmpty()) errorMessage.append("Укажите группу!\n");

        try {
            double gpa = Double.parseDouble(gpaField.getText());
            if (gpa < 0.0 || gpa > 4.0) errorMessage.append("GPA должен быть от 0.0 до 4.0!\n");
        } catch (NumberFormatException e) {
            errorMessage.append("Неверный формат GPA (должно быть числом)!\n");
        }

        if (errorMessage.length() == 0) return true;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText("Исправьте поля:");
        alert.setContentText(errorMessage.toString());
        alert.showAndWait();
        return false;
    }
}