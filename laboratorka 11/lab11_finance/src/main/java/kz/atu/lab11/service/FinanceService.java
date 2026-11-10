package kz.atu.lab11.service;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kz.atu.lab11.model.Transaction;
import kz.atu.lab11.model.TransactionType;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

/**
 * Сервисный слой: все бизнес-правила учёта финансов.
 * Не зависит от графического интерфейса и тестируется без запуска JavaFX.
 */
public class FinanceService {

    public static final String ALL_CATEGORIES = "Все";
    public static final double MAX_AMOUNT = 1_000_000_000.0;

    /** extractor: изменение любого поля операции порождает событие update у списка. */
    private final ObservableList<Transaction> transactions =
            FXCollections.observableArrayList(t -> new Observable[]{
                    t.descriptionProperty(), t.categoryProperty(), t.typeProperty(),
                    t.amountProperty(), t.dateProperty()
            });

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    // ---------- CRUD ----------

    public void addTransaction(String description, String category,
                               TransactionType type, double amount, LocalDate date) {
        validate(description, category, type, amount, date);
        checkDuplicate(null, description, category, date);
        transactions.add(new Transaction(description.trim(), category.trim(), type, amount, date));
    }

    public void updateTransaction(Transaction t, String description, String category,
                                  TransactionType type, double amount, LocalDate date) {
        if (t == null) throw new IllegalArgumentException("Операция не выбрана");
        validate(description, category, type, amount, date);
        checkDuplicate(t, description, category, date);
        t.setDescription(description.trim());
        t.setCategory(category.trim());
        t.setType(type);
        t.setAmount(amount);
        t.setDate(date);
    }

    public void deleteTransaction(Transaction t) {
        if (t == null) throw new IllegalArgumentException("Операция не выбрана");
        transactions.remove(t);
    }

    // ---------- Поиск и фильтрация ----------

    /** Предикат: поиск по описанию + фильтр по категории. */
    public Predicate<Transaction> createPredicate(String query, String category) {
        String q = query == null ? "" : query.trim().toLowerCase();
        boolean allCategories = category == null || ALL_CATEGORIES.equals(category);
        return t -> t.getDescription().toLowerCase().contains(q)
                && (allCategories || t.getCategory().equalsIgnoreCase(category));
    }

    /** Динамический список категорий, сформированный из данных. */
    public List<String> getCategories() {
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Transaction t : transactions) {
            set.add(t.getCategory());
        }
        return new ArrayList<>(set);
    }

    // ---------- Статистика ----------

    public double getTotalIncome(Collection<? extends Transaction> items) {
        return items.stream().filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getTotalExpense(Collection<? extends Transaction> items) {
        return items.stream().filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getBalance(Collection<? extends Transaction> items) {
        return getTotalIncome(items) - getTotalExpense(items);
    }

    /** Средняя сумма операции (0, если операций нет). */
    public double getAverageAmount(Collection<? extends Transaction> items) {
        return items.stream().mapToDouble(Transaction::getAmount).average().orElse(0.0);
    }

    public double getBalance() { return getBalance(transactions); }
    public double getAverageAmount() { return getAverageAmount(transactions); }

    // ---------- Валидация ----------

    private void validate(String description, String category,
                          TransactionType type, double amount, LocalDate date) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Введите описание операции");
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Введите категорию");
        if (type == null)
            throw new IllegalArgumentException("Выберите тип операции");
        if (!Double.isFinite(amount) || amount <= 0 || amount > MAX_AMOUNT)
            throw new IllegalArgumentException("Сумма должна быть больше 0 и не более 1 000 000 000");
        if (date == null)
            throw new IllegalArgumentException("Выберите дату");
        if (date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Дата не может быть в будущем");
    }

    /** Запрет дубликата: одинаковые описание, категория и дата. */
    private void checkDuplicate(Transaction self, String description, String category, LocalDate date) {
        for (Transaction t : transactions) {
            if (t != self
                    && t.getDescription().equalsIgnoreCase(description.trim())
                    && t.getCategory().equalsIgnoreCase(category.trim())
                    && t.getDate().equals(date)) {
                throw new IllegalArgumentException("Такая операция уже существует");
            }
        }
    }
}
