package com.example.studentmgmt.controller;

import com.example.studentmgmt.MainApp;
import com.example.studentmgmt.model.Student;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> groupColumn;
    @FXML private TableColumn<Student, Number> gpaColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> groupFilterComboBox;
    @FXML private Label statusLabel;

    private final ObservableList<Student> masterData = FXCollections.observableArrayList();
    private FilteredList<Student> filteredData;

    @FXML
    public void initialize() {
        // 10 Демонстрационных записей (Пункт 3 требований)
        masterData.add(new Student("101", "Алиев Арман", "ИС-24-1", 3.8));
        masterData.add(new Student("102", "Иванова Мария", "ИС-24-2", 3.5));
        masterData.add(new Student("103", "Смагулов Берик", "ИС-24-1", 3.9));
        masterData.add(new Student("104", "Петров Александр", "ВТ-23-4", 2.9));
        masterData.add(new Student("105", "Каримова Диана", "ИС-24-2", 3.2));
        masterData.add(new Student("106", "Серик Мурат", "ВТ-23-4", 3.6));
        masterData.add(new Student("107", "Болатова Анель", "ИС-24-1", 4.0));
        masterData.add(new Student("108", "Ким Алексей", "ВТ-23-4", 3.1));
        masterData.add(new Student("109", "Ахметов Данияр", "ИС-24-2", 2.7));
        masterData.add(new Student("110", "Оспанова Мадина", "ИС-24-1", 3.4));

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        groupColumn.setCellValueFactory(cellData -> cellData.getValue().groupProperty());
        gpaColumn.setCellValueFactory(cellData -> cellData.getValue().gpaProperty());

        filteredData = new FilteredList<>(masterData, p -> true);
        studentTable.setItems(filteredData);

        groupFilterComboBox.setItems(FXCollections.observableArrayList("Все группы", "ИС-24-1", "ИС-24-2", "ВТ-23-4"));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        groupFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        statusLabel.setText("Загружено записей: " + masterData.size());
    }

    private void updateFilter() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String selectedGroup = groupFilterComboBox.getValue();

        filteredData.setPredicate(student -> {
            boolean matchesSearch = student.getFullName().toLowerCase().contains(searchText);
            boolean matchesGroup = (selectedGroup == null || selectedGroup.equals("Все группы") || student.getGroup().equals(selectedGroup));
            return matchesSearch && matchesGroup;
        });
        statusLabel.setText("Найдено записей: " + filteredData.size());
    }

    @FXML
    private void handleAddStudent() {
        Student tempStudent = new Student("", "", "", 0.0);
        boolean okClicked = showStudentFormDialog(tempStudent, "Добавление студента");
        if (okClicked) {
            masterData.add(tempStudent);
            updateFilter();
        }
    }

    @FXML
    private void handleEditStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            boolean okClicked = showStudentFormDialog(selectedStudent, "Редактирование студента");
            if (okClicked) {
                studentTable.refresh();
            }
        } else {
            showAlert("Никто не выбран", "Выберите студента для редактирования.");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        int selectedIndex = studentTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Student removed = studentTable.getItems().remove(selectedIndex);
            masterData.remove(removed);
        } else {
            showAlert("Никто не выбран", "Выберите студента для удаления.");
        }
    }

    @FXML
    private void handleClearFilter() {
        searchField.clear();
        groupFilterComboBox.getSelectionModel().select("Все группы");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private boolean showStudentFormDialog(Student student, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/main-view.fxml"));
            // Меняем путь дочернего ресурса
            FXMLLoader formLoader = new FXMLLoader(MainApp.class.getResource("view/student-form.fxml"));
            DialogPane page = formLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(studentTable.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            StudentFormController controller = formLoader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStudent(student);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}