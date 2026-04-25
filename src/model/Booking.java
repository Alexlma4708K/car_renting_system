package model;

public class Booking {
    private int id;
    private int userId;
    private int carId;
    private int days;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
}