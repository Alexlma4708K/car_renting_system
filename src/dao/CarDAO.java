package dao;

import model.Car;
import java.sql.*;
import java.util.*;

public class CarDAO {

    public List<Car> getFilteredCars(String search, boolean availableOnly) {
        List<Car> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM cars WHERE (brand LIKE ? OR model LIKE ?)";
            if (availableOnly) sql += " AND available > 0";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Car c = new Car();
                c.setId(rs.getInt("id"));
                c.setBrand(rs.getString("brand"));
                c.setModel(rs.getString("model"));
                c.setPrice(rs.getDouble("price"));
                c.setAvailable(rs.getInt("available"));
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Car> getAllCars() {
        return getFilteredCars("", false);
    }

    public void addCar(String brand, String model, double price, int available) {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO cars(brand, model, price, available) VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, brand);
            ps.setString(2, model);
            ps.setDouble(3, price);
            ps.setInt(4, available);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCar(int id, String brand, String model, double price, int available) {
        try {
            Connection con = DBConnection.getConnection();

            String sql = "UPDATE cars SET brand=?, model=?, price=?, available=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, brand);
            ps.setString(2, model);
            ps.setDouble(3, price);
            ps.setInt(4, available);
            ps.setInt(5, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCar(int id) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("DELETE FROM cars WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}