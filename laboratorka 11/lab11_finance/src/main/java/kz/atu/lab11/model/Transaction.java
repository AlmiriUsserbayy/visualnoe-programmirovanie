package kz.atu.lab11.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Модель «Финансовая операция».
 * Поля представлены JavaFX Properties, поэтому TableView
 * автоматически отслеживает их изменения.
 */
public class Transaction {
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final ObjectProperty<TransactionType> type = new SimpleObjectProperty<>();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    public Transaction(String description, String category,
                       TransactionType type, double amount, LocalDate date) {
        setDescription(description);
        setCategory(category);
        setType(type);
        setAmount(amount);
        setDate(date);
    }

    public String getDescription() { return description.get(); }
    public void setDescription(String v) { description.set(v); }
    public StringProperty descriptionProperty() { return description; }

    public String getCategory() { return category.get(); }
    public void setCategory(String v) { category.set(v); }
    public StringProperty categoryProperty() { return category; }

    public TransactionType getType() { return type.get(); }
    public void setType(TransactionType v) { type.set(v); }
    public ObjectProperty<TransactionType> typeProperty() { return type; }

    public double getAmount() { return amount.get(); }
    public void setAmount(double v) { amount.set(v); }
    public DoubleProperty amountProperty() { return amount; }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate v) { date.set(v); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    /** Сумма со знаком: доход — плюс, расход — минус. */
    public double getSignedAmount() {
        return getType() == TransactionType.EXPENSE ? -getAmount() : getAmount();
    }
}
