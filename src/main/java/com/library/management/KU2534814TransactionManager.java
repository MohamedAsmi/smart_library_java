package com.library.management;

import com.library.database.KU2534814DatabaseConnection;
import com.library.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class KU2534814TransactionManager {
    private Connection connection;
    private Map<Integer, KU2534814BorrowTransaction> inMemoryTransactions;
    private int transactionIdCounter = 1;
    private boolean useDatabase = false;

    public KU2534814TransactionManager() {
        this.inMemoryTransactions = new HashMap<>();
        try {
            this.connection = KU2534814DatabaseConnection.getConnection();
            if (this.connection != null && !this.connection.isClosed()) {
                this.useDatabase = true;
            }
        } catch (Exception e) {
            this.useDatabase = false;
            System.out.println("Database not available. Using in-memory storage.");
        }
    }

    // Create a borrow transaction
    public KU2534814BorrowTransaction createBorrowTransaction(int bookId, int userId, int borrowDays) {
        if (!useDatabase) {
            // In-memory mode
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(borrowDays);
            KU2534814BorrowTransaction transaction = new KU2534814BorrowTransaction(
                transactionIdCounter++, bookId, userId, borrowDate, dueDate
            );
            transaction.setStatus("Active");
            inMemoryTransactions.put(transaction.getTransactionId(), transaction);
            return transaction;
        }
        
        try {
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(borrowDays);

            String sql = "INSERT INTO borrow_transactions (book_id, user_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, 'Active')";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            pstmt.setDate(3, Date.valueOf(borrowDate));
            pstmt.setDate(4, Date.valueOf(dueDate));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int transactionId = rs.getInt(1);
                KU2534814BorrowTransaction transaction = new KU2534814BorrowTransaction(
                    transactionId, bookId, userId, borrowDate, dueDate
                );
                
                // Update book status
                updateBookStatus(bookId, "Borrowed");
                
                // Log activity
                logActivity(userId, "BORROW_BOOK", "Book", bookId, "Borrowed book ID: " + bookId);
                
                return transaction;
            }
        } catch (SQLException e) {
            System.out.println("Error creating borrow transaction: " + e.getMessage());
        }
        return null;
    }

    // Return a book
    public boolean returnBook(int bookId, int userId) {
        try {
            // Get active transaction
            String sql = "SELECT * FROM borrow_transactions WHERE book_id = ? AND user_id = ? AND status = 'Active' ORDER BY borrow_date DESC LIMIT 1";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int transactionId = rs.getInt("id");
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                LocalDate returnDate = LocalDate.now();

                // Calculate fine if overdue
                double fine = 0.0;
                if (returnDate.isAfter(dueDate)) {
                    int daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
                    // Get user's fine rate
                    fine = calculateFineForUser(userId, daysOverdue);
                    
                    if (fine > 0) {
                        // Create fine record
                        createFine(userId, transactionId, fine, daysOverdue);
                    }
                }

                // Update transaction
                String updateSql = "UPDATE borrow_transactions SET return_date = ?, status = 'Returned', fine_amount = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setDate(1, Date.valueOf(returnDate));
                updateStmt.setDouble(2, fine);
                updateStmt.setInt(3, transactionId);
                updateStmt.executeUpdate();

                // Check for reservations
                checkAndNotifyReservations(bookId);

                // Update book status
                updateBookStatus(bookId, "Available");
                
                // Log activity
                logActivity(userId, "RETURN_BOOK", "Book", bookId, "Returned book ID: " + bookId + ", Fine: " + fine);

                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
        return false;
    }

    // Get all transactions for a user
    public List<KU2534814BorrowTransaction> getUserTransactions(int userId) {
        List<KU2534814BorrowTransaction> transactions = new ArrayList<>();
        try {
            String sql = "SELECT * FROM borrow_transactions WHERE user_id = ? ORDER BY borrow_date DESC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814BorrowTransaction transaction = new KU2534814BorrowTransaction(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id"),
                    rs.getDate("borrow_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate()
                );
                if (rs.getDate("return_date") != null) {
                    transaction.setReturnDate(rs.getDate("return_date").toLocalDate());
                }
                transaction.setStatus(rs.getString("status"));
                transaction.setFineAmount(rs.getDouble("fine_amount"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get all active (borrowed) transactions
    public List<KU2534814BorrowTransaction> getActiveTransactions() {
        List<KU2534814BorrowTransaction> transactions = new ArrayList<>();
        
        if (!useDatabase) {
            // In-memory mode
            for (KU2534814BorrowTransaction transaction : inMemoryTransactions.values()) {
                if ("Active".equals(transaction.getStatus())) {
                    transactions.add(transaction);
                }
            }
            return transactions;
        }
        
        try {
            String sql = "SELECT * FROM borrow_transactions WHERE status = 'Active' ORDER BY due_date ASC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814BorrowTransaction transaction = new KU2534814BorrowTransaction(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id"),
                    rs.getDate("borrow_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate()
                );
                transaction.setStatus(rs.getString("status"));
                transaction.setFineAmount(rs.getDouble("fine_amount"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error getting active transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get overdue transactions
    public List<KU2534814BorrowTransaction> getOverdueTransactions() {
        List<KU2534814BorrowTransaction> transactions = new ArrayList<>();
        
        if (!useDatabase) {
            // In-memory mode
            LocalDate today = LocalDate.now();
            for (KU2534814BorrowTransaction transaction : inMemoryTransactions.values()) {
                if ("Active".equals(transaction.getStatus()) && transaction.getDueDate().isBefore(today)) {
                    transactions.add(transaction);
                }
            }
            return transactions;
        }
        
        try {
            String sql = "SELECT * FROM borrow_transactions WHERE status = 'Active' AND due_date < CURDATE() ORDER BY due_date ASC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814BorrowTransaction transaction = new KU2534814BorrowTransaction(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id"),
                    rs.getDate("borrow_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate()
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error getting overdue transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Helper methods
    private void updateBookStatus(int bookId, String status) {
        if (!useDatabase) {
            return; // In-memory mode doesn't need to update database
        }
        
        try {
            String sql = "UPDATE books SET status = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating book status: " + e.getMessage());
        }
    }

    private double calculateFineForUser(int userId, int daysOverdue) {
        try {
            String sql = "SELECT user_type FROM users WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String userType = rs.getString("user_type");
                double ratePerDay;
                switch (userType) {
                    case "Student":
                        ratePerDay = 1.0;
                        break;
                    case "Faculty":
                        ratePerDay = 0.5;
                        break;
                    case "Guest":
                        ratePerDay = 2.0;
                        break;
                    default:
                        ratePerDay = 1.0;
                        break;
                }
                return daysOverdue * ratePerDay;
            }
        } catch (SQLException e) {
            System.out.println("Error calculating fine: " + e.getMessage());
        }
        return 0.0;
    }

    private void createFine(int userId, int transactionId, double amount, int daysOverdue) {
        try {
            // Get fine rate
            String sql = "SELECT user_type FROM users WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            double rate = 1.0;
            if (rs.next()) {
                String userType = rs.getString("user_type");
                switch (userType) {
                    case "Student":
                        rate = 1.0;
                        break;
                    case "Faculty":
                        rate = 0.5;
                        break;
                    case "Guest":
                        rate = 2.0;
                        break;
                    default:
                        rate = 1.0;
                        break;
                }
            }

            String insertSql = "INSERT INTO fines (user_id, borrow_transaction_id, fine_amount, days_overdue, fine_rate, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, transactionId);
            insertStmt.setDouble(3, amount);
            insertStmt.setInt(4, daysOverdue);
            insertStmt.setDouble(5, rate);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating fine: " + e.getMessage());
        }
    }

    private void checkAndNotifyReservations(int bookId) {
        try {
            String sql = "SELECT * FROM reservations WHERE book_id = ? AND status = 'Active' ORDER BY reservation_date ASC LIMIT 1";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                int reservationId = rs.getInt("id");
                
                // Update book status to reserved
                updateBookStatus(bookId, "Reserved");
                
                // Send notification
                String notifSql = "INSERT INTO notifications (user_id, notification_type, message) VALUES (?, 'Reservation Available', ?)";
                PreparedStatement notifStmt = connection.prepareStatement(notifSql);
                notifStmt.setInt(1, userId);
                notifStmt.setString(2, "Your reserved book (ID: " + bookId + ") is now available for pickup!");
                notifStmt.executeUpdate();
                
                // Update reservation
                String updateSql = "UPDATE reservations SET notification_sent = TRUE WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, reservationId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error checking reservations: " + e.getMessage());
        }
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

    // Get all transactions (for reporting)
    public List<KU2534814BorrowTransaction> getAllTransactions() {
        List<KU2534814BorrowTransaction> transactions = new ArrayList<>();
        
        if (!useDatabase) {
            // Return all in-memory transactions
            transactions.addAll(inMemoryTransactions.values());
            return transactions;
        }
        
        try {
            String sql = "SELECT * FROM borrow_transactions ORDER BY borrow_date DESC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814BorrowTransaction transaction = new KU2534814BorrowTransaction(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("user_id"),
                    rs.getDate("borrow_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate()
                );
                
                Date returnDate = rs.getDate("return_date");
                if (returnDate != null) {
                    transaction.setReturnDate(returnDate.toLocalDate());
                }
                transaction.setStatus(rs.getString("status"));
                
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all transactions: " + e.getMessage());
        }
        
        return transactions;
    }
}
