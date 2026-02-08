package com.library.management;

import com.library.commands.KU2534814CommandHistory;
import com.library.database.KU2534814DatabaseConnection;
import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;
import com.library.observers.KU2534814NotificationManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class KU2534814LibraryManager {
    private List<KU2534814Book> books;
    private List<KU2534814User> users;
    private KU2534814CommandHistory commandHistory;
    private KU2534814NotificationManager notificationManager;
    private Connection connection;

    public KU2534814LibraryManager() {
        this.books = new ArrayList<>();
        this.users = new ArrayList<>();
        this.commandHistory = new KU2534814CommandHistory();
        this.notificationManager = new KU2534814NotificationManager();
        this.connection = KU2534814DatabaseConnection.getConnection();
        loadBooksFromDatabase();
        loadUsersFromDatabase();
    }

    // Book Operations
    public void addBook(KU2534814Book book) {
        books.add(book);
        saveBooksToDatabase(book);
        notificationManager.sendNotification("New book added: " + book.getTitle());
    }

    public void removeBook(KU2534814Book book) {
        books.remove(book);
        deleteBookFromDatabase(book.getId());
        notificationManager.sendNotification("Book removed: " + book.getTitle());
    }

    public KU2534814Book findBookById(int id) {
        for (KU2534814Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    public KU2534814Book findBookByIsbn(String isbn) {
        for (KU2534814Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    public List<KU2534814Book> searchBooksByTitle(String title) {
        List<KU2534814Book> result = new ArrayList<>();
        for (KU2534814Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    // User Operations
    public void addUser(KU2534814User user) {
        users.add(user);
        saveUsersToDatabase(user);
        notificationManager.attach(user);
        notificationManager.sendNotification("Welcome " + user.getName() + " to Smart Library!");
    }

    public void removeUser(KU2534814User user) {
        users.remove(user);
        deleteUserFromDatabase(user.getId());
        notificationManager.detach(user);
    }

    public KU2534814User findUserById(int id) {
        for (KU2534814User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    // Fine Calculation
    public double calculateFineForBook(KU2534814Book book) {
        if (book.getBorrowedBy() != null && book.getDueDate() != null) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(book.getDueDate())) {
                int daysOverdue = (int) ChronoUnit.DAYS.between(book.getDueDate(), today);
                return book.getBorrowedBy().calculateFine(daysOverdue);
            }
        }
        return 0.0;
    }

    // Database Operations
    private void loadBooksFromDatabase() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            
            while (rs.next()) {
                KU2534814Book book = new KU2534814Book(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    private void saveBooksToDatabase(KU2534814Book book) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO books (id, isbn, title, author, price) VALUES (?, ?, ?, ?, ?)"
            );
            pstmt.setInt(1, book.getId());
            pstmt.setString(2, book.getIsbn());
            pstmt.setString(3, book.getTitle());
            pstmt.setString(4, book.getAuthor());
            pstmt.setDouble(5, book.getPrice());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving book: " + e.getMessage());
        }
    }

    private void deleteBookFromDatabase(int id) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM books WHERE id = ?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    private void loadUsersFromDatabase() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            // User loading would require strategy initialization based on user_type
        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private void saveUsersToDatabase(KU2534814User user) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO users (id, name, email, user_type) VALUES (?, ?, ?, ?)"
            );
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getUserType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    private void deleteUserFromDatabase(int id) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    // Getters
    public List<KU2534814Book> getBooks() {
        return books;
    }

    public List<KU2534814User> getUsers() {
        return users;
    }

    public KU2534814CommandHistory getCommandHistory() {
        return commandHistory;
    }

    public KU2534814NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
