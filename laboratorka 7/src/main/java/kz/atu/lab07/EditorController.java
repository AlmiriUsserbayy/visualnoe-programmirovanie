package kz.atu.lab07;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyCombination;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EditorController {

    @FXML private TextArea txtEditor;
    @FXML private Label lblStatus;
    @FXML private MenuItem miNew;
    @FXML private MenuItem miOpen;
    @FXML private MenuItem miSave;

    private Path currentFile;

    @FXML
    public void initialize() {
        miNew.setAccelerator(KeyCombination.keyCombination("Shortcut+N"));
        miOpen.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        miSave.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));

        txtEditor.textProperty().addListener((obs, oldText, newText) ->
                lblStatus.setText("Символов: " + newText.length()));
    }

    @FXML
    private void onNew() {
        txtEditor.clear();
        currentFile = null;
        lblStatus.setText("Новый документ");
    }

    @FXML
    private void onOpen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
        File file = chooser.showOpenDialog(txtEditor.getScene().getWindow());
        if (file == null) return;
        try {
            txtEditor.setText(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            currentFile = file.toPath();
            lblStatus.setText("Открыт: " + file.getName());
        } catch (IOException ex) {
            showError("Ошибка открытия: " + ex.getMessage());
        }
    }

    @FXML
    private void onSave() {
        if (currentFile == null) {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));
            File file = chooser.showSaveDialog(txtEditor.getScene().getWindow());
            if (file == null) return;
            currentFile = file.toPath();
        }
        try {
            Files.writeString(currentFile, txtEditor.getText(), StandardCharsets.UTF_8);
            lblStatus.setText("Сохранено: " + currentFile.getFileName());
        } catch (IOException ex) {
            showError("Ошибка сохранения: " + ex.getMessage());
        }
    }

    @FXML private void onCopy() { txtEditor.copy(); }
    @FXML private void onCut() { txtEditor.cut(); }
    @FXML private void onPaste() { txtEditor.paste(); }

    // Индивидуальный вариант №2: Выделить всё
    @FXML private void onSelectAll() { txtEditor.selectAll(); }

    @FXML private void onExit() { Platform.exit(); }

    @FXML
    private void onAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Редактор заметок. Лабораторная работа №7.", ButtonType.OK);
        a.setHeaderText("О программе");
        a.showAndWait();
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText("Ошибка");
        a.showAndWait();
    }
}