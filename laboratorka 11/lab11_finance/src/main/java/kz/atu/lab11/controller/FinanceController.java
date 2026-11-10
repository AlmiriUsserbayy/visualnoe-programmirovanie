package kz.atu.lab11.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import kz.atu.lab11.model.Transaction;
import kz.atu.lab11.model.TransactionType;
import kz.atu.lab11.service.FinanceService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Контроллер: только реакция на действия пользователя и отображение. */
public class FinanceController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML private TableView<Transaction> tableTransactions;
    @FXML private TableColumn<Transaction, String> colDescription, colCategory;
    @FXML private TableColumn<Transaction, TransactionType> colType;
    @FXML private TableColumn<Transaction, Number> colAmount;
    @FXML private TableColumn<Transaction, LocalDate> colDate;
    @FXML private TextField txtDescription, txtCategory, txtAmount, txtSearch;
    @FXML private ComboBox<TransactionType> cmbType;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<String> cmbCategoryFilter;
    @FXML private Label lblCount, lblBalance, lblAverage;

    private final FinanceService service = new FinanceService();
    private FilteredList<Transaction> filtered;

    @FXML
    private void initialize() {
        // --- столбцы ---
        colDescription.setCellValueFactory(c -> c.getValue().descriptionProperty());
        colCategory.setCellValueFactory(c -> c.getValue().categoryProperty());
        colType.setCellValueFactory(c -> c.getValue().typeProperty());
        colAmount.setCellValueFactory(c -> c.getValue().amountProperty());
        colDate.setCellValueFactory(c -> c.getValue().dateProperty());

        colAmount.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : String.format(Locale.US, "%,.2f", item.doubleValue()));
            }
        });
        colDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : DATE_FMT.format(item));
            }
        });

        // --- демонстрационные данные ---
        LocalDate today = LocalDate.now();
        service.addTransaction("Зарплата", "Работа", TransactionType.INCOME, 450000, today.minusDays(10));
        service.addTransaction("Продукты", "Питание", TransactionType.EXPENSE, 35000, today.minusDays(5));
        service.addTransaction("Проезд", "Транспорт", TransactionType.EXPENSE, 8000, today.minusDays(3));
        service.addTransaction("Фриланс-проект", "Работа", TransactionType.INCOME, 120000, today.minusDays(2));

        // --- ObservableList -> FilteredList -> SortedList -> TableView ---
        filtered = new FilteredList<>(service.getTransactions(), t -> true);
        SortedList<Transaction> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tableTransactions.comparatorProperty());
        tableTransactions.setItems(sorted);

        // счётчик и итоги обновляются при любом изменении видимого списка (CRUD, поиск, фильтр)
        filtered.addListener((ListChangeListener<Transaction>) c -> updateSummary());
        // список категорий обновляется при любом изменении данных
        service.getTransactions().addListener((ListChangeListener<Transaction>) c -> refreshCategories());

        // --- форма ---
        cmbType.getItems().addAll(TransactionType.values());
        cmbType.setValue(TransactionType.EXPENSE);
        dpDate.setValue(today);

        refreshCategories();
        txtSearch.textProperty().addListener((o, a, b) -> applyFilter());
        cmbCategoryFilter.valueProperty().addListener((o, a, b) -> applyFilter());

        // выбор строки заполняет поля формы
        tableTransactions.getSelectionModel().selectedItemProperty().addListener((o, old, t) -> {
            if (t != null) {
                txtDescription.setText(t.getDescription());
                txtCategory.setText(t.getCategory());
                cmbType.setValue(t.getType());
                txtAmount.setText(String.format(Locale.US, "%.2f", t.getAmount()));
                dpDate.setValue(t.getDate());
            }
        });

        updateSummary();
    }

    // ---------- Поиск и фильтрация ----------

    private void applyFilter() {
        filtered.setPredicate(service.createPredicate(txtSearch.getText(), cmbCategoryFilter.getValue()));
        updateSummary();
    }

    private void refreshCategories() {
        String selected = cmbCategoryFilter.getValue();
        List<String> items = new ArrayList<>();
        items.add(FinanceService.ALL_CATEGORIES);
        items.addAll(service.getCategories());
        cmbCategoryFilter.getItems().setAll(items);
        cmbCategoryFilter.setValue(items.contains(selected) ? selected : FinanceService.ALL_CATEGORIES);
    }

    private void updateSummary() {
        lblCount.setText("Найдено операций: " + filtered.size());
        lblBalance.setText(String.format(Locale.US, "Доходы: %,.2f | Расходы: %,.2f | Баланс: %,.2f",
                service.getTotalIncome(filtered),
                service.getTotalExpense(filtered),
                service.getBalance(filtered)));
        lblAverage.setText(String.format(Locale.US, "Средняя сумма операции: %,.2f",
                service.getAverageAmount(filtered)));
    }

    // ---------- CRUD ----------

    private double readAmount() {
        try {
            return Double.parseDouble(txtAmount.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введите числовую сумму");
        }
    }

    @FXML
    private void onAdd() {
        try {
            service.addTransaction(txtDescription.getText(), txtCategory.getText(),
                    cmbType.getValue(), readAmount(), dpDate.getValue());
            clearFields();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        try {
            Transaction t = tableTransactions.getSelectionModel().getSelectedItem();
            service.updateTransaction(t, txtDescription.getText(), txtCategory.getText(),
                    cmbType.getValue(), readAmount(), dpDate.getValue());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        try {
            Transaction t = tableTransactions.getSelectionModel().getSelectedItem();
            if (t != null && !confirmDelete(t)) {
                return;
            }
            service.deleteTransaction(t); // при t == null сервис выбросит исключение
            clearFields();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void clearFields() {
        txtDescription.clear();
        txtCategory.clear();
        txtAmount.clear();
        cmbType.setValue(TransactionType.EXPENSE);
        dpDate.setValue(LocalDate.now());
        tableTransactions.getSelectionModel().clearSelection();
    }

    // ---------- Диалоги ----------

    private boolean confirmDelete(Transaction t) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Подтверждение");
        a.setHeaderText(null);
        a.setContentText("Удалить операцию «" + t.getDescription() + "»?");
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    private void showError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
