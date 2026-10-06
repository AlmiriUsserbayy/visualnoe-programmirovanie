package kz.atu.lab06;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CatalogController {

    // Элементы интерфейса (fx:id)
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterCategory;
    @FXML private ListView<String> listCategories; // Из пункта 17
    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;

    // Поля ввода для нового товара
    @FXML private TextField txtName;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML private Label lblCount;

    // Списки данных
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts;

    @FXML
    public void initialize() {
        // 1. Начальные данные
        products.addAll(
                new Product("Ноутбук", "Электроника", 350000, 5),
                new Product("Смартфон", "Электроника", 180000, 10),
                new Product("Клавиатура", "Аксессуары", 15000, 15),
                new Product("Мышь", "Аксессуары", 9000, 20)
        );

        // 2. Настройка столбцов таблицы
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // 3. Инициализация фильтров (ComboBox)
        cmbCategory.getItems().addAll("Электроника", "Аксессуары", "Офисная техника");
        cmbFilterCategory.getItems().addAll("Все", "Электроника", "Аксессуары", "Офисная техника");
        cmbFilterCategory.setValue("Все");

        // 4. Инициализация бокового списка категорий (ListView - пункт 17)
        listCategories.getItems().addAll("Все товары", "Электроника", "Аксессуары", "Офисная техника");

        // 5. Обертка в FilteredList для динамического поиска
        filteredProducts = new FilteredList<>(products, product -> true);
        tableProducts.setItems(filteredProducts);

        // 6. Слушатели изменений для поиска и фильтрации
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        cmbFilterCategory.setOnAction(event -> applyFilter());

        // Слушатель для ListView
        listCategories.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cmbFilterCategory.setValue(newVal.equals("Все товары") ? "Все" : newVal);
                applyFilter();
            }
        });

        // Обновляем счетчик при старте
        updateCount();
    }

    // Метод фильтрации
    private void applyFilter() {
        String search = txtSearch.getText().trim().toLowerCase();
        String category = cmbFilterCategory.getValue();

        filteredProducts.setPredicate(product -> {
            boolean matchesSearch = product.getName().toLowerCase().contains(search);
            boolean matchesCategory = category == null || category.equals("Все") || product.getCategory().equals(category);
            return matchesSearch && matchesCategory;
        });

        updateCount();
    }

    // Добавление товара
    @FXML
    private void onAddClick() {
        String name = txtName.getText().trim();
        String category = cmbCategory.getValue();
        String priceText = txtPrice.getText().trim();
        String quantityText = txtQuantity.getText().trim();

        if (name.isBlank() || category == null || priceText.isBlank() || quantityText.isBlank()) {
            showError("Заполните все поля.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            if (price <= 0 || quantity < 0) {
                showError("Проверьте цену и количество.");
                return;
            }

            products.add(new Product(name, category, price, quantity));
            clearInput();
            applyFilter();
        } catch (NumberFormatException e) {
            showError("Цена и количество должны быть числами.");
        }
    }

    // Удаление товара
    @FXML
    private void onDeleteClick() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите товар для удаления.");
            return;
        }
        products.remove(selected);
        applyFilter();
    }

    // Очистить фильтры
    @FXML
    private void onClearFilterClick() {
        txtSearch.clear();
        cmbFilterCategory.setValue("Все");
        listCategories.getSelectionModel().clearSelection();
        applyFilter();
    }

    private void updateCount() {
        lblCount.setText("Найдено записей: " + tableProducts.getItems().size());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearInput() {
        txtName.clear();
        txtPrice.clear();
        txtQuantity.clear();
        cmbCategory.getSelectionModel().clearSelection();
        txtName.requestFocus();
    }
}
