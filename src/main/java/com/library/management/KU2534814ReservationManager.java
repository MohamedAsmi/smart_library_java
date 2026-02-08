package com.library.management;

import com.library.database.KU2534814DatabaseConnection;
import com.library.models.KU2534814Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class KU2534814ReservationManager {
    private Connection connection;
    private Map<Integer, KU2534814Reservation> inMemoryReservations;
    private int reservationIdCounter = 1;
    private boolean useDatabase = false;

    public KU2534814ReservationManager() {
        this.inMemoryReservations = new HashMap<>();
        try {
            this.connection = KU2534814DatabaseConnection.getConnection();
            if (this.connection != null && !this.connection.isClosed()) {
                this.useDatabase = true;
            }
        } catch (Exception e) {
            this.useDatabase = false;
        }
    }

    // Create a reservation
    public KU2534814Reservation createReservation(int bookId, int userId) {
        try {
            // Check if book is already reserved by this user
            String checkSql = "SELECT * FROM reservations WHERE book_id = ? AND user_id = ? AND status = 'Active'";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            checkStmt.setInt(2, userId);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                System.out.println("User already has an active reservation for this book.");
                return null;
            }

            // Create reservation
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(48);
            String sql = "INSERT INTO reservations (book_id, user_id, status, expiry_date) VALUES (?, ?, 'Active', ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            pstmt.setTimestamp(3, Timestamp.valueOf(expiryDate));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int reservationId = rs.getInt(1);
                
                // Update book status
                String updateSql = "UPDATE books SET availability_status = 'Reserved' WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                // Log activity
                logActivity(userId, "RESERVE_BOOK", "Book", bookId, "Reserved book ID: " + bookId);

                KU2534814Reservation reservation = new KU2534814Reservation(reservationId, bookId, userId);
                reservation.setExpiryDate(expiryDate);
                
                System.out.println("Reservation created successfully. Expires in 48 hours.");
                return reservation;
            }
        } catch (SQLException e) {
            System.out.println("Error creating reservation: " + e.getMessage());
        }
        return null;
    }

    // Cancel a reservation
    public boolean cancelReservation(int reservationId) {
        try {
            // Get reservation details
            String getSql = "SELECT * FROM reservations WHERE id = ?";
            PreparedStatement getStmt = connection.prepareStatement(getSql);
            getStmt.setInt(1, reservationId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");
                int userId = rs.getInt("user_id");

                // Update reservation status
                String sql = "UPDATE reservations SET status = 'Cancelled' WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, reservationId);
                pstmt.executeUpdate();

                // Check if there are other active reservations for this book
                String checkSql = "SELECT COUNT(*) as count FROM reservations WHERE book_id = ? AND status = 'Active' AND id != ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                checkStmt.setInt(1, bookId);
                checkStmt.setInt(2, reservationId);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next() && checkRs.getInt("count") == 0) {
                    // No other reservations, set book to available
                    String updateSql = "UPDATE books SET availability_status = 'Available' WHERE id = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();
                }

                // Log activity
                logActivity(userId, "CANCEL_RESERVATION", "Book", bookId, "Cancelled reservation for book ID: " + bookId);

                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error cancelling reservation: " + e.getMessage());
        }
        return false;
    }

    // Fulfill a reservation (when user borrows the reserved book)
    public boolean fulfillReservation(int reservationId) {
        try {
            String sql = "UPDATE reservations SET status = 'Fulfilled' WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error fulfilling reservation: " + e.getMessage());
        }
        return false;
    }

    // Get user's active reservations
    public List<KU2534814Reservation> getUserReservations(int userId) {
        List<KU2534814Reservation> reservations = new ArrayList<>();
        try {
            String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY reservation_date DESC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814Reservation reservation = new KU2534814Reservation(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id")
                );
                reservation.setReservationDate(rs.getTimestamp("reservation_date").toLocalDateTime());
                reservation.setStatus(rs.getString("status"));
                reservation.setNotificationSent(rs.getBoolean("notification_sent"));
                if (rs.getTimestamp("expiry_date") != null) {
                    reservation.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                }
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user reservations: " + e.getMessage());
        }
        return reservations;
    }

    // Get reservation for a specific book
    public KU2534814Reservation getBookReservation(int bookId) {
        try {
            String sql = "SELECT * FROM reservations WHERE book_id = ? AND status = 'Active' ORDER BY reservation_date ASC LIMIT 1";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                KU2534814Reservation reservation = new KU2534814Reservation(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id")
                );
                reservation.setReservationDate(rs.getTimestamp("reservation_date").toLocalDateTime());
                reservation.setStatus(rs.getString("status"));
                reservation.setNotificationSent(rs.getBoolean("notification_sent"));
                if (rs.getTimestamp("expiry_date") != null) {
                    reservation.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                }
                return reservation;
            }
        } catch (SQLException e) {
            System.out.println("Error getting book reservation: " + e.getMessage());
        }
        return null;
    }

    // Check and expire old reservations
    public void checkExpiredReservations() {
        try {
            String sql = "UPDATE reservations SET status = 'Expired' WHERE status = 'Active' AND expiry_date < NOW()";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            int expiredCount = pstmt.executeUpdate();

            if (expiredCount > 0) {
                System.out.println(expiredCount + " reservations have been expired.");
                
                // Update book statuses for expired reservations
                String updateSql = "UPDATE books b " +
                                 "SET b.availability_status = 'Available' " +
                                 "WHERE b.id IN (" +
                                 "  SELECT r.book_id FROM reservations r " +
                                 "  WHERE r.status = 'Expired' " +
                                 "  AND NOT EXISTS (" +
                                 "    SELECT 1 FROM reservations r2 " +
                                 "    WHERE r2.book_id = r.book_id " +
                                 "    AND r2.status = 'Active'" +
                                 "  )" +
                                 ")";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error checking expired reservations: " + e.getMessage());
        }
    }

    // Get all active reservations
    public List<KU2534814Reservation> getAllActiveReservations() {
        List<KU2534814Reservation> reservations = new ArrayList<>();
        try {
            String sql = "SELECT * FROM reservations WHERE status = 'Active' ORDER BY reservation_date ASC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814Reservation reservation = new KU2534814Reservation(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id")
                );
                reservation.setReservationDate(rs.getTimestamp("reservation_date").toLocalDateTime());
                reservation.setStatus(rs.getString("status"));
                reservation.setNotificationSent(rs.getBoolean("notification_sent"));
                if (rs.getTimestamp("expiry_date") != null) {
                    reservation.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                }
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all active reservations: " + e.getMessage());
        }
        return reservations;
    }

    private void logActivity(int userId, String action, String entityType, int entityId, String details) {
        try {
            String sql = "INSERT INTO activity_log (user_id, action, entity_type, entity_id, details) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, entityType);
            pstmt.setInt(4, entityId);
            pstmt.setString(5, details);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error logging activity: " + e.getMessage());
        }
    }
}
