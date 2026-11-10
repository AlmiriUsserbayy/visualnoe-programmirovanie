package kz.atu.lab11.service;

import kz.atu.lab11.model.Transaction;
import kz.atu.lab11.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static kz.atu.lab11.model.TransactionType.EXPENSE;
import static kz.atu.lab11.model.TransactionType.INCOME;
import static org.junit.jupiter.api.Assertions.*;

class FinanceServiceTest {

    private static final LocalDate D = LocalDate.of(2025, 1, 15);
    private FinanceService s;

    @BeforeEach
    void setUp() {
        s = new FinanceService();
    }

    @Test
    void shouldAddTransaction() {
        s.addTransaction("Зарплата", "Работа", INCOME, 1000, D);
        assertEquals(1, s.getTransactions().size());
        assertEquals("Зарплата", s.getTransactions().get(0).getDescription());
    }

    @Test
    void shouldTrimTextFields() {
        s.addTransaction("  Кофе  ", "  Питание ", EXPENSE, 5, D);
        assertEquals("Кофе", s.getTransactions().get(0).getDescription());
        assertEquals("Питание", s.getTransactions().get(0).getCategory());
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThrows(IllegalArgumentException.class,
                () -> s.addTransaction("  ", "Работа", INCOME, 100, D));
    }

    @Test
    void shouldRejectBlankCategory() {
        assertThrows(IllegalArgumentException.class,
                () -> s.addTransaction("Кофе", "", EXPENSE, 100, D));
    }

    @Test
    void shouldRejectInvalidAmount() {
        for (double bad : new double[]{0, -5, Double.NaN, Double.POSITIVE_INFINITY, 2_000_000_000}) {
            assertThrows(IllegalArgumentException.class,
                    () -> s.addTransaction("Кофе", "Питание", EXPENSE, bad, D), "amount=" + bad);
        }
    }

    @Test
    void shouldRejectNullTypeAndDate() {
        assertThrows(IllegalArgumentException.class,
                () -> s.addTransaction("Кофе", "Питание", null, 100, D));
        assertThrows(IllegalArgumentException.class,
                () -> s.addTransaction("Кофе", "Питание", EXPENSE, 100, null));
    }

    @Test
    void shouldRejectFutureDate() {
        assertThrows(IllegalArgumentException.class,
                () -> s.addTransaction("Кофе", "Питание", EXPENSE, 100, LocalDate.now().plusDays(1)));
    }

    @Test
    void shouldRejectDuplicate() {
        s.addTransaction("Кофе", "Питание", EXPENSE, 100, D);
        assertThrows(IllegalArgumentException.class,
                () -> s.addTransaction("кофе", "ПИТАНИЕ", EXPENSE, 200, D));
        // другая дата — не дубликат
        assertDoesNotThrow(() -> s.addTransaction("Кофе", "Питание", EXPENSE, 100, D.minusDays(1)));
    }

    @Test
    void shouldUpdateTransaction() {
        s.addTransaction("Кофе", "Питание", EXPENSE, 100, D);
        Transaction t = s.getTransactions().get(0);
        s.updateTransaction(t, "Чай", "Напитки", INCOME, 250, D);
        assertEquals("Чай", t.getDescription());
        assertEquals("Напитки", t.getCategory());
        assertEquals(INCOME, t.getType());
        assertEquals(250, t.getAmount(), 1e-9);
    }

    @Test
    void updateShouldAllowSavingSameRecordWithoutDuplicateError() {
        s.addTransaction("Кофе", "Питание", EXPENSE, 100, D);
        Transaction t = s.getTransactions().get(0);
        assertDoesNotThrow(() -> s.updateTransaction(t, "Кофе", "Питание", EXPENSE, 120, D));
    }

    @Test
    void shouldRejectUpdateOrDeleteWithoutSelection() {
        assertThrows(IllegalArgumentException.class,
                () -> s.updateTransaction(null, "Кофе", "Питание", EXPENSE, 100, D));
        assertThrows(IllegalArgumentException.class, () -> s.deleteTransaction(null));
    }

    @Test
    void shouldDeleteTransaction() {
        s.addTransaction("Кофе", "Питание", EXPENSE, 100, D);
        s.deleteTransaction(s.getTransactions().get(0));
        assertTrue(s.getTransactions().isEmpty());
    }

    @Test
    void shouldCalculateBalanceAndAverage() {
        s.addTransaction("Зарплата", "Работа", INCOME, 100, D);
        s.addTransaction("Продукты", "Питание", EXPENSE, 40, D);
        assertEquals(100, s.getTotalIncome(s.getTransactions()), 1e-9);
        assertEquals(40, s.getTotalExpense(s.getTransactions()), 1e-9);
        assertEquals(60, s.getBalance(), 1e-9);
        assertEquals(70, s.getAverageAmount(), 1e-9);
    }

    @Test
    void averageOfEmptyListIsZero() {
        assertEquals(0.0, s.getAverageAmount(), 1e-9);
        assertEquals(0.0, s.getBalance(), 1e-9);
    }

    @Test
    void shouldBuildDynamicDistinctSortedCategories() {
        s.addTransaction("А", "Работа", INCOME, 1, D);
        s.addTransaction("Б", "Питание", EXPENSE, 1, D);
        s.addTransaction("В", "работа", INCOME, 1, D.minusDays(1));
        assertEquals(List.of("Питание", "Работа"), s.getCategories());
    }

    @Test
    void predicateShouldSearchByDescriptionAndFilterByCategory() {
        s.addTransaction("Продукты", "Питание", EXPENSE, 10, D);
        s.addTransaction("Проезд", "Транспорт", EXPENSE, 5, D);
        s.addTransaction("Премия", "Работа", INCOME, 50, D);

        long byName = s.getTransactions().stream()
                .filter(s.createPredicate("про", FinanceService.ALL_CATEGORIES)).count();
        assertEquals(2, byName);

        long byCategory = s.getTransactions().stream()
                .filter(s.createPredicate("", "Транспорт")).count();
        assertEquals(1, byCategory);

        long both = s.getTransactions().stream()
                .filter(s.createPredicate("про", "Питание")).count();
        assertEquals(1, both);
    }

    @Test
    void signedAmountDependsOnType() {
        Transaction in = new Transaction("a", "b", TransactionType.INCOME, 10, D);
        Transaction out = new Transaction("a", "b", TransactionType.EXPENSE, 10, D);
        assertEquals(10, in.getSignedAmount(), 1e-9);
        assertEquals(-10, out.getSignedAmount(), 1e-9);
    }
}
