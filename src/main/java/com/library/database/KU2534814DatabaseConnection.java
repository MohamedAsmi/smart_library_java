package com.library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class KU2534814DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/smart_library";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;

    private KU2534814DatabaseConnection() {
        
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Database connected successfully!");
                initializeDatabase();
            } catch (ClassNotFoundException e) {
                System.out.println("⚠ MySQL JDBC Driver not found. Running in memory mode.");
                connection = null;
            } catch (SQLException e) {
                System.out.println("⚠ Database not available. Running in memory mode.");
                System.out.println("  (To use database features, ensure MySQL is running with database 'smart_library')");
                connection = null;
            }
        }
        return connection;
    }

    private static void initializeDatabase() {
        try {
            Statement stmt = connection.createStatement();
    
            String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
                "id INT PRIMARY KEY, " +
                "isbn VARCHAR(20) UNIQUE, " +
                "title VARCHAR(255), " +
                "author VARCHAR(255), " +
                "price DOUBLE" +
                ")";
            stmt.executeUpdate(createBooksTable);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "email VARCHAR(255) UNIQUE, " +
                "user_type VARCHAR(50)" +
                ")";
            stmt.executeUpdate(createUsersTable);

            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "book_id INT, " +
                "user_id INT, " +
                "action VARCHAR(50), " +
                "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (book_id) REFERENCES books(id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
            stmt.executeUpdate(createTransactionsTable);
            
            System.out.println("Database tables initialized successfully!");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
