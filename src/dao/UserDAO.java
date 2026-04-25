package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.User;

public class UserDAO {

    public User authenticate(String username, String password) {
        String query = "SELECT id, username, role, license_number FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        password,
                        rs.getString("role"),
                        rs.getString("license_number")
                );
            }
        } catch (Exception e) {
            System.err.println("Database Error during authentication:");
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String password, String licenseNumber) {
        String checkQuery = "SELECT id FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password, role, license_number) VALUES (?, ?, 'user', ?)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, licenseNumber);
                insertStmt.executeUpdate();
                return true;
            }

        } catch (Exception e) {
            System.err.println("Database Error during registration:");
            e.printStackTrace();
            return false;
        }
    }
}