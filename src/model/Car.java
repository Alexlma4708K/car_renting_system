package model;

public class Car {
    private int id;
    private String brand;
    private String model;
    private double price;
    private int available;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
}