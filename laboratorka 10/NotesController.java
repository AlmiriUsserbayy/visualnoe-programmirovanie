package kz.atu.lab10;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

public class NotesController {
    @FXML private ListView<Note> listNotes;
    @FXML private TextField txtTitle;
    @FXML private TextArea txtBody;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private Label lblStatus;

    private final ObservableList<Note> allNotes = FXCollections.observableArrayList();
    private FilteredList<Note> filteredNotes;
    private final NoteStorage storage = new NoteStorage();

    @FXML
    public void initialize() {
        ObservableList<String> categories = FXCollections.observableArrayList("Билеты", "Отели", "Экскурсии", "Рестораны");
        cmbCategory.setItems(categories);

        ObservableList<String> filterOptions = FXCollections.observableArrayList("Все");
        filterOptions.addAll(categories);
        cmbFilter.setItems(filterOptions);
        cmbFilter.setValue("Все");

        allNotes.addAll(
                new Note("Рейс Алматы - Милан", "Вылет в 06:40, Астана Эйр. Багаж 23кг.", "Билеты"),
                new Note("Отель Duomo", "Заселение в 14:00. Бронь на Booking.", "Отели"),
                new Note("Миланский собор", "Билеты куплены онлайн на 11:00.", "Экскурсии"),
                new Note("Пиццерия Luini", "Попробовать фирменные панцеротти.", "Рестораны"),
                new Note("Поезд в Рим", "Отправление в 09:15 с центрального вокзала.", "Билеты"),
                new Note("Хостел Sunshine Rome", "Адрес: Via Veneto 12. Оплата наличными.", "Отели"),
                new Note("Колизей и Форум", "Гид Александр, встреча у метро в 15:00.", "Экскурсии"),
                new Note("Траттория Da Enzo", "Забронировать столик заранее на вечер.", "Рестораны"),
                new Note("Рейс Рим - Алматы", "Вылет в 22:10. Не забыть сувениры.", "Билеты"),
                new Note("Шоппинг в галерее", "Купить подарки коллегам и семье.", "Экскурсии")
        );

        filteredNotes = new FilteredList<>(allNotes, p -> true);
        listNotes.setItems(filteredNotes);

        txtSearch.textProperty().addListener((obs, old, newVal) -> updateFilter());
        cmbFilter.valueProperty().addListener((obs, old, newVal) -> updateFilter());

        listNotes.getSelectionModel().selectedItemProperty().addListener((obs, old, note) -> {
            if (note != null) {
                txtTitle.setText(note.getTitle());
                txtBody.setText(note.getBody());
                cmbCategory.setValue(note.getCategory());
            }
        });
        lblStatus.setText("Готово. Демо-записей: " + allNotes.size());
    }

    private void updateFilter() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String filterCat = cmbFilter.getValue();

        filteredNotes.setPredicate(note -> {
            boolean matchesSearch = note.getTitle().toLowerCase().contains(searchText) ||
                    note.getBody().toLowerCase().contains(searchText);
            boolean matchesCategory = filterCat == null || filterCat.equals("Все") ||
                    note.getCategory().equals(filterCat);
            return matchesSearch && matchesCategory;
        });
    }

    @FXML
    private void onAdd() {
        String title = txtTitle.getText().trim();
        String body = txtBody.getText().trim();
        String cat = cmbCategory.getValue();

        if (title.isBlank() || body.isBlank() || cat == null) {
            showError("Заполните все поля!");
            return;
        }
        allNotes.add(new Note(title, body, cat));
        clearFields();
    }

    @FXML
    private void onEdit() {
        Note selected = listNotes.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите запись!"); return; }

        String title = txtTitle.getText().trim();
        String body = txtBody.getText().trim();
        String cat = cmbCategory.getValue();

        if (title.isBlank() || body.isBlank() || cat == null) { showError("Поля пустые!"); return; }

        selected.setTitle(title);
        selected.setBody(body);
        selected.setCategory(cat);

        int index = allNotes.indexOf(selected);
        allNotes.set(index, selected);
    }

    @FXML
    private void onDelete() {
        Note selected = listNotes.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите запись!"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить план?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            allNotes.remove(selected);
            clearFields();
        }
    }

    @FXML
    private void onSave() {
        FileChooser chooser = createChooser();
        File file = chooser.showSaveDialog(listNotes.getScene().getWindow());
        if (file == null) return;
        try {
            storage.save(file.toPath(), allNotes);
            lblStatus.setText("Сохранено: " + file.getName());
        } catch (IOException ex) { showError("Ошибка сохранения!"); }
    }

    @FXML
    private void onOpen() {
        FileChooser chooser = createChooser();
        File file = chooser.showOpenDialog(listNotes.getScene().getWindow());
        if (file == null) return;
        try {
            var loaded = storage.load(file.toPath());
            allNotes.setAll(loaded);
            lblStatus.setText("Загружено: " + allNotes.size());
        } catch (Exception ex) { showError("Файл JSON поврежден!"); }
    }

    private FileChooser createChooser() {
        FileChooser c = new FileChooser();
        c.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        return c;
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    private void clearFields() {
        txtTitle.clear();
        txtBody.clear();
        cmbCategory.setValue(null);
    }
}
