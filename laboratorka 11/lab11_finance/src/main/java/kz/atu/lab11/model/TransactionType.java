package kz.atu.lab11.model;

/** Тип финансовой операции. */
public enum TransactionType {
    INCOME("Доход"),
    EXPENSE("Расход");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
