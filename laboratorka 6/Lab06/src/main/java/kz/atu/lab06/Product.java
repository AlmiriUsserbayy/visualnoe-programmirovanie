package kz.atu.lab06;

public class Product {
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product(String name, String category, double price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
