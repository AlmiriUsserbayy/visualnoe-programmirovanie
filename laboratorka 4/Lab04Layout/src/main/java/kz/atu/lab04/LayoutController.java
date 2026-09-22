package kz.atu.lab04;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class LayoutController {
    @FXML private TextField txtFullName;
    @FXML private TextField txtGroup;
    @FXML private ComboBox<String> cmbCourse;
    @FXML private TableView<Student> tblStudents;
    @FXML private TableColumn<Student, String> colFullName;
    @FXML private TableColumn<Student, String> colGroup;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private Label lblStatus;

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Заполняем ComboBox (Пункт 9)
        cmbCourse.getItems().addAll("1 курс", "2 курс", "3 курс", "4 курс");
        cmbCourse.getSelectionModel().selectFirst();

        // Настраиваем колонки таблицы для вывода данных
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGroup.setCellValueFactory(new PropertyValueFactory<>("group"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));

        // Связываем список данных с таблицей
        tblStudents.setItems(studentList);
    }

    @FXML
    private void onSaveClick() {
        String name = txtFullName.getText().trim();
        String group = txtGroup.getText().trim();
        String course = cmbCourse.getValue();

        if (name.isBlank() || group.isBlank()) {
            lblStatus.setText("Статус: заполните ФИО и группу");
            return;
        }

        // Добавляем студента в таблицу
        studentList.add(new Student(name, group, course));

        lblStatus.setText("Статус: данные сохранены - " + name + ", " + group + ", " + course);

        // Очищаем поля после добавления
        txtFullName.clear();
        txtGroup.clear();
    }

    @FXML
    private void onClearClick() {
        txtFullName.clear();
        txtGroup.clear();
        cmbCourse.getSelectionModel().selectFirst();
        lblStatus.setText("Статус: форма очищена");
        txtFullName.requestFocus();
    }
}
