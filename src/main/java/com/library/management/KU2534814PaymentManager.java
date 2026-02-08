package com.library.management;

import com.library.database.KU2534814DatabaseConnection;
import com.library.models.KU2534814Fine;
import com.library.models.KU2534814Payment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class KU2534814PaymentManager {
    private Connection connection;
    private Map<Integer, KU2534814Payment> inMemoryPayments;
    private Map<Integer, KU2534814Fine> inMemoryFines;
    private int paymentIdCounter = 1;
    private int fineIdCounter = 1;
    private boolean useDatabase = false;

    public KU2534814PaymentManager() {
        this.inMemoryPayments = new HashMap<>();
        this.inMemoryFines = new HashMap<>();
        try {
            this.connection = KU2534814DatabaseConnection.getConnection();
            if (this.connection != null && !this.connection.isClosed()) {
                this.useDatabase = true;
            }
        } catch (Exception e) {
            this.useDatabase = false;
        }
    }

    // Process payment for a fine
    public KU2534814Payment processPayment(int userId, int fineId, double amount, String paymentMethod) {
        if (!useDatabase) {
            // In-memory mode
            String txnRef = "TXN-" + System.currentTimeMillis() + "-" + userId;
            KU2534814Payment payment = new KU2534814Payment(userId, amount, paymentMethod);
            payment.setId(paymentIdCounter++);
            payment.setFineId(fineId);
            payment.setTransactionReference(txnRef);
            inMemoryPayments.put(payment.getId(), payment);
            
            // Update fine status if exists
            if (inMemoryFines.containsKey(fineId)) {
                KU2534814Fine fine = inMemoryFines.get(fineId);
                fine.setStatus("Paid");
                fine.setPaymentDate(LocalDateTime.now());
                fine.setPaymentMethod(paymentMethod);
            }
            
            return payment;
        }
        
        try {
            // First check if the fine exists
            String checkSql = "SELECT id FROM fines WHERE id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, fineId);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (!checkRs.next()) {
                System.out.println("Error: Fine ID " + fineId + " does not exist.");
                return null;
            }
            
            // Create payment record
            String sql = "INSERT INTO payments (user_id, fine_id, amount, payment_method, status, transaction_reference) VALUES (?, ?, ?, ?, 'Completed', ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            String txnRef = "TXN-" + System.currentTimeMillis() + "-" + userId;
            pstmt.setInt(1, userId);
            pstmt.setInt(2, fineId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, paymentMethod);
            pstmt.setString(5, txnRef);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int paymentId = rs.getInt(1);
                
                // Update fine status
                String updateSql = "UPDATE fines SET status = 'Paid', payment_date = NOW(), payment_method = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setString(1, paymentMethod);
                updateStmt.setInt(2, fineId);
                updateStmt.executeUpdate();

                // Create payment object
                KU2534814Payment payment = new KU2534814Payment(userId, amount, paymentMethod);
                payment.setId(paymentId);
                payment.setFineId(fineId);
                payment.setTransactionReference(txnRef);
                
                System.out.println("Payment processed successfully: " + txnRef);
                return payment;
            }
        } catch (SQLException e) {
            System.out.println("Error processing payment: " + e.getMessage());
        }
        return null;
    }

    // Get all fines for a user
    public List<KU2534814Fine> getUserFines(int userId) {
        List<KU2534814Fine> fines = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fines WHERE user_id = ? ORDER BY created_at DESC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814Fine fine = new KU2534814Fine(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("fine_amount"),
                    rs.getInt("days_overdue"),
                    rs.getDouble("fine_rate")
                );
                fine.setStatus(rs.getString("status"));
                if (rs.getObject("borrow_transaction_id") != null) {
                    fine.setBorrowTransactionId(rs.getInt("borrow_transaction_id"));
                }
                if (rs.getTimestamp("payment_date") != null) {
                    fine.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                }
                fine.setPaymentMethod(rs.getString("payment_method"));
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user fines: " + e.getMessage());
        }
        return fines;
    }

    // Get pending fines for a user
    public List<KU2534814Fine> getPendingFines(int userId) {
        List<KU2534814Fine> fines = new ArrayList<>();
        try {
            String sql = "SELECT * FROM fines WHERE user_id = ? AND status = 'Pending' ORDER BY created_at ASC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814Fine fine = new KU2534814Fine(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("fine_amount"),
                    rs.getInt("days_overdue"),
                    rs.getDouble("fine_rate")
                );
                fine.setStatus(rs.getString("status"));
                if (rs.getObject("borrow_transaction_id") != null) {
                    fine.setBorrowTransactionId(rs.getInt("borrow_transaction_id"));
                }
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.out.println("Error getting pending fines: " + e.getMessage());
        }
        return fines;
    }

    // Get total pending fine amount for a user
    public double getTotalPendingFines(int userId) {
        try {
            String sql = "SELECT SUM(fine_amount) as total FROM fines WHERE user_id = ? AND status = 'Pending'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error getting total pending fines: " + e.getMessage());
        }
        return 0.0;
    }

    // Get payment history for a user
    public List<KU2534814Payment> getUserPayments(int userId) {
        List<KU2534814Payment> payments = new ArrayList<>();
        try {
            String sql = "SELECT * FROM payments WHERE user_id = ? ORDER BY payment_date DESC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814Payment payment = new KU2534814Payment(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getObject("fine_id") != null ? rs.getInt("fine_id") : null,
                    rs.getDouble("amount"),
                    rs.getString("payment_method"),
                    rs.getString("status")
                );
                payment.setTransactionReference(rs.getString("transaction_reference"));
                payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                payment.setNotes(rs.getString("notes"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user payments: " + e.getMessage());
        }
        return payments;
    }

    // Get all pending fines (for admin/librarian)
    public List<KU2534814Fine> getAllPendingFines() {
        List<KU2534814Fine> fines = new ArrayList<>();
        try {
            String sql = "SELECT f.*, u.name as user_name FROM fines f JOIN users u ON f.user_id = u.id WHERE f.status = 'Pending' ORDER BY f.created_at ASC";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KU2534814Fine fine = new KU2534814Fine(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getDouble("fine_amount"),
                    rs.getInt("days_overdue"),
                    rs.getDouble("fine_rate")
                );
                fine.setStatus(rs.getString("status"));
                if (rs.getObject("borrow_transaction_id") != null) {
                    fine.setBorrowTransactionId(rs.getInt("borrow_transaction_id"));
                }
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all pending fines: " + e.getMessage());
        }
        return fines;
    }

    // Waive a fine (admin function)
    public boolean waiveFine(int fineId) {
        try {
            String sql = "UPDATE fines SET status = 'Waived' WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, fineId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error waiving fine: " + e.getMessage());
        }
        return false;
    }

    // Generate payment receipt
    public String generateReceipt(int paymentId) {
        try {
            String sql = "SELECT p.*, u.name, u.email, f.fine_amount, f.days_overdue " +
                        "FROM payments p " +
                        "JOIN users u ON p.user_id = u.id " +
                        "LEFT JOIN fines f ON p.fine_id = f.id " +
                        "WHERE p.id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, paymentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                StringBuilder receipt = new StringBuilder();
                receipt.append("========================================\n");
                receipt.append("      PAYMENT RECEIPT - KU2534814       \n");
                receipt.append("========================================\n");
                receipt.append("Transaction Ref: ").append(rs.getString("transaction_reference")).append("\n");
                receipt.append("Date: ").append(rs.getTimestamp("payment_date")).append("\n");
                receipt.append("----------------------------------------\n");
                receipt.append("User: ").append(rs.getString("name")).append("\n");
                receipt.append("Email: ").append(rs.getString("email")).append("\n");
                receipt.append("----------------------------------------\n");
                receipt.append("Amount Paid: $").append(String.format("%.2f", rs.getDouble("amount"))).append("\n");
                receipt.append("Payment Method: ").append(rs.getString("payment_method")).append("\n");
                receipt.append("Status: ").append(rs.getString("status")).append("\n");
                if (rs.getObject("days_overdue") != null) {
                    receipt.append("Days Overdue: ").append(rs.getInt("days_overdue")).append("\n");
                }
                receipt.append("========================================\n");
                receipt.append("Thank you for your payment!\n");
                receipt.append("========================================\n");
                
                return receipt.toString();
            }
        } catch (SQLException e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
        return "Receipt not found.";
    }
}
