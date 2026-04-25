package dao;

import java.sql.*;

public class BookingDAO {

    public int saveBooking(int userId, int carId, int days, double totalAmount) {
        String insertBookingSql = "INSERT INTO bookings (user_id, car_id, days, status, start_date, total_price) VALUES (?, ?, ?, 'BOOKED', NOW(), ?)";
        String updateCarSql = "UPDATE cars SET available = 0 WHERE id = ?";

        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement pst1 = conn.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement pst2 = conn.prepareStatement(updateCarSql)) {

                pst1.setInt(1, userId);
                pst1.setInt(2, carId);
                pst1.setInt(3, days);
                pst1.setDouble(4, totalAmount);
                pst1.executeUpdate();

                try (ResultSet rs = pst1.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }

                pst2.setInt(1, carId);
                pst2.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public void finalizeReturn(int bookingId, double finalPrice) {
        String updateBookingSql = "UPDATE bookings SET status = 'RETURNED', end_date = NOW(), total_price = ? WHERE id = ?";
        String updateCarSql = "UPDATE cars SET available = 1 WHERE id = (SELECT car_id FROM bookings WHERE id = ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(updateBookingSql);
                 PreparedStatement pst2 = conn.prepareStatement(updateCarSql)) {

                pst1.setDouble(1, finalPrice);
                pst1.setInt(2, bookingId);
                pst1.executeUpdate();

                pst2.setInt(1, bookingId);
                pst2.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}