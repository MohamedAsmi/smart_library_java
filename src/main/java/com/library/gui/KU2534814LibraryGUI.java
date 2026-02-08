package com.library.gui;

import com.library.builders.KU2534814BookBuilder;
import com.library.commands.*;
import com.library.management.KU2534814LibraryManager;
import com.library.models.KU2534814Book;
import com.library.models.KU2534814User;
import com.library.strategies.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KU2534814LibraryGUI extends JFrame {
    private KU2534814LibraryManager libraryManager;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTable userTable;
    private DefaultTableModel userTableModel;

    public KU2534814LibraryGUI() {
        libraryManager = new KU2534814LibraryManager();
        initializeGUI();
        loadSampleData();
    }

    private void initializeGUI() {
        setTitle("Smart Library Management System - KU2534814");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Books Tab
        JPanel booksPanel = createBooksPanel();
        tabbedPane.addTab("Books", booksPanel);

        // Users Tab
        JPanel usersPanel = createUsersPanel();
        tabbedPane.addTab("Users", usersPanel);

        // Transactions Tab
        JPanel transactionsPanel = createTransactionsPanel();
        tabbedPane.addTab("Transactions", transactionsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Book Table
        String[] columnNames = {"ID", "ISBN", "Title", "Author", "Price", "State"};
        bookTableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addBookBtn = new JButton("Add Book");
        JButton removeBookBtn = new JButton("Remove Book");
        JButton refreshBtn = new JButton("Refresh");

        addBookBtn.addActionListener(e -> addBookDialog());
        removeBookBtn.addActionListener(e -> removeSelectedBook());
        refreshBtn.addActionListener(e -> refreshBookTable());

        buttonsPanel.add(addBookBtn);
        buttonsPanel.add(removeBookBtn);
        buttonsPanel.add(refreshBtn);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // User Table
        String[] columnNames = {"ID", "Name", "Email", "User Type", "Borrowed Books"};
        userTableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addUserBtn = new JButton("Add User");
        JButton removeUserBtn = new JButton("Remove User");
        JButton refreshBtn = new JButton("Refresh");

        addUserBtn.addActionListener(e -> addUserDialog());
        removeUserBtn.addActionListener(e -> removeSelectedUser());
        refreshBtn.addActionListener(e -> refreshUserTable());

        buttonsPanel.add(addUserBtn);
        buttonsPanel.add(removeUserBtn);
        buttonsPanel.add(refreshBtn);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tabbed pane for different views
        JTabbedPane transactionTabs = new JTabbedPane();
        
        // Active Transactions Tab
        JPanel activeTransPanel = createActiveTransactionsView();
        transactionTabs.addTab("Active Transactions", activeTransPanel);
        
        // Fines & Payments Tab
        JPanel finesPanel = createFinesAndPaymentsView();
        transactionTabs.addTab("Fines & Payments", finesPanel);
        
        // Reservations Tab
        JPanel reservationsPanel = createReservationsView();
        transactionTabs.addTab("Reservations", reservationsPanel);
        
        // Transaction Actions Tab
        JPanel actionsPanel = createTransactionActionsView();
        transactionTabs.addTab("Actions", actionsPanel);
        
        panel.add(transactionTabs, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createActiveTransactionsView() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table for active transactions
        String[] columnNames = {"Transaction ID", "Book ID", "Book Title", "User ID", "User Name", "Borrow Date", "Due Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshActiveTransactions(model));
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshActiveTransactions(model);
        
        return panel;
    }

    private JPanel createFinesAndPaymentsView() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Split into two sections
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Pending Fines Table
        JPanel finesPanel = new JPanel(new BorderLayout());
        finesPanel.setBorder(BorderFactory.createTitledBorder("Pending Fines"));
        String[] fineColumns = {"Fine ID", "User ID", "User Name", "Amount", "Days Overdue", "Fine Rate", "Status"};
        DefaultTableModel fineModel = new DefaultTableModel(fineColumns, 0);
        JTable fineTable = new JTable(fineModel);
        finesPanel.add(new JScrollPane(fineTable), BorderLayout.CENTER);
        
        // Fine actions
        JPanel fineButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton payFineBtn = new JButton("Pay Fine");
        JButton waiveFineBtn = new JButton("Waive Fine");
        JButton refreshFinesBtn = new JButton("Refresh");
        payFineBtn.addActionListener(e -> processFinePayment(fineTable, fineModel));
        refreshFinesBtn.addActionListener(e -> refreshPendingFines(fineModel));
        fineButtonPanel.add(payFineBtn);
        fineButtonPanel.add(waiveFineBtn);
        fineButtonPanel.add(refreshFinesBtn);
        finesPanel.add(fineButtonPanel, BorderLayout.SOUTH);
        
        // Payment History Table
        JPanel paymentsPanel = new JPanel(new BorderLayout());
        paymentsPanel.setBorder(BorderFactory.createTitledBorder("Recent Payments"));
        String[] paymentColumns = {"Payment ID", "User", "Amount", "Method", "Transaction Ref", "Date", "Status"};
        DefaultTableModel paymentModel = new DefaultTableModel(paymentColumns, 0);
        JTable paymentTable = new JTable(paymentModel);
        paymentsPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        
        JPanel paymentButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshPaymentsBtn = new JButton("Refresh");
        refreshPaymentsBtn.addActionListener(e -> refreshRecentPayments(paymentModel));
        paymentButtonPanel.add(refreshPaymentsBtn);
        paymentsPanel.add(paymentButtonPanel, BorderLayout.SOUTH);
        
        splitPane.setTopComponent(finesPanel);
        splitPane.setBottomComponent(paymentsPanel);
        splitPane.setDividerLocation(250);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load initial data
        refreshPendingFines(fineModel);
        refreshRecentPayments(paymentModel);
        
        return panel;
    }

    private JPanel createReservationsView() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Reservations Table
        String[] columnNames = {"Reservation ID", "Book ID", "Book Title", "User ID", "User Name", "Date", "Status", "Expires"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel Selected");
        JButton refreshBtn = new JButton("Refresh");
        cancelBtn.addActionListener(e -> cancelSelectedReservation(table, model));
        refreshBtn.addActionListener(e -> refreshReservations(model));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshReservations(model);
        
        return panel;
    }

    private JPanel createTransactionActionsView() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Borrow Book
        gbc.gridx = 0;
        gbc.gridy = 0;
        JButton borrowBtn = new JButton("Borrow Book");
        borrowBtn.setPreferredSize(new Dimension(200, 40));
        borrowBtn.addActionListener(e -> borrowBookDialog());
        panel.add(borrowBtn, gbc);
        
        // Return Book
        gbc.gridy = 1;
        JButton returnBtn = new JButton("Return Book");
        returnBtn.setPreferredSize(new Dimension(200, 40));
        returnBtn.addActionListener(e -> returnBookDialog());
        panel.add(returnBtn, gbc);
        
        // Reserve Book
        gbc.gridy = 2;
        JButton reserveBtn = new JButton("Reserve Book");
        reserveBtn.setPreferredSize(new Dimension(200, 40));
        reserveBtn.addActionListener(e -> reserveBookDialog());
        panel.add(reserveBtn, gbc);
        
        // Cancel Reservation
        gbc.gridy = 3;
        JButton cancelReservationBtn = new JButton("Cancel Reservation");
        cancelReservationBtn.setPreferredSize(new Dimension(200, 40));
        cancelReservationBtn.addActionListener(e -> cancelReservationDialog());
        panel.add(cancelReservationBtn, gbc);
        
        // View Command History
        gbc.gridy = 4;
        JButton viewHistoryBtn = new JButton("View Command History");
        viewHistoryBtn.setPreferredSize(new Dimension(200, 40));
        viewHistoryBtn.addActionListener(e -> viewCommandHistory());
        panel.add(viewHistoryBtn, gbc);
        
        return panel;
    }

    // Refresh methods for new tables
    private void refreshActiveTransactions(DefaultTableModel model) {
        model.setRowCount(0);
        // TODO: Load from TransactionManager
        // For now, show sample data
        model.addRow(new Object[]{1, 2, "Head First Design Patterns", 1, "John Doe", "2026-01-30", "2026-02-13", "Active"});
    }

    private void refreshPendingFines(DefaultTableModel model) {
        model.setRowCount(0);
        // TODO: Load from PaymentManager
        // Sample data
        model.addRow(new Object[]{1, 1, "John Doe", "$5.00", 5, "$1.00", "Pending"});
    }

    private void refreshRecentPayments(DefaultTableModel model) {
        model.setRowCount(0);
        // TODO: Load from PaymentManager
        // Sample data
    }

    private void refreshReservations(DefaultTableModel model) {
        model.setRowCount(0);
        // TODO: Load from ReservationManager
        // Sample data
    }

    private void processFinePayment(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String[] methods = {"Cash", "Card", "Online", "Bank Transfer"};
            String method = (String) JOptionPane.showInputDialog(this, 
                "Select Payment Method:", "Process Payment", 
                JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
            
            if (method != null) {
                JOptionPane.showMessageDialog(this, "Payment processed successfully!");
                refreshPendingFines(model);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a fine to pay.");
        }
    }

    private void cancelSelectedReservation(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Cancel this reservation?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled!");
                refreshReservations(model);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
        }
    }

    private void addBookDialog() {
        JTextField idField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField priceField = new JTextField();

        Object[] message = {
            "ID:", idField,
            "ISBN:", isbnField,
            "Title:", titleField,
            "Author:", authorField,
            "Price:", priceField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                KU2534814Book book = new KU2534814BookBuilder()
                    .setId(Integer.parseInt(idField.getText()))
                    .setIsbn(isbnField.getText())
                    .setTitle(titleField.getText())
                    .setAuthor(authorField.getText())
                    .setPrice(Double.parseDouble(priceField.getText()))
                    .build();
                libraryManager.addBook(book);
                refreshBookTable();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage());
            }
        }
    }

    private void removeSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) bookTableModel.getValueAt(selectedRow, 0);
            KU2534814Book book = libraryManager.findBookById(id);
            if (book != null) {
                libraryManager.removeBook(book);
                refreshBookTable();
                JOptionPane.showMessageDialog(this, "Book removed successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to remove.");
        }
    }

    private void addUserDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        String[] userTypes = {"Student", "Faculty", "Guest"};
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);

        Object[] message = {
            "ID:", idField,
            "Name:", nameField,
            "Email:", emailField,
            "User Type:", userTypeCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String userType = (String) userTypeCombo.getSelectedItem();
                KU2534814User user = new KU2534814User(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    emailField.getText(),
                    userType,
                    getFineStrategy(userType)
                );
                libraryManager.addUser(user);
                refreshUserTable();
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
            }
        }
    }

    private void removeSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) userTableModel.getValueAt(selectedRow, 0);
            KU2534814User user = libraryManager.findUserById(id);
            if (user != null) {
                libraryManager.removeUser(user);
                refreshUserTable();
                JOptionPane.showMessageDialog(this, "User removed successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to remove.");
        }
    }

    private void borrowBookDialog() {
        JTextField bookIdField = new JTextField();
        JTextField userIdField = new JTextField();

        Object[] message = {
            "Book ID:", bookIdField,
            "User ID:", userIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Borrow Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                KU2534814Book book = libraryManager.findBookById(Integer.parseInt(bookIdField.getText()));
                KU2534814User user = libraryManager.findUserById(Integer.parseInt(userIdField.getText()));
                
                if (book != null && user != null) {
                    KU2534814BorrowCommand command = new KU2534814BorrowCommand(book, user);
                    libraryManager.getCommandHistory().execute(command);
                    refreshBookTable();
                    JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Book or User not found!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void returnBookDialog() {
        JTextField bookIdField = new JTextField();

        Object[] message = {
            "Book ID:", bookIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Return Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                KU2534814Book book = libraryManager.findBookById(Integer.parseInt(bookIdField.getText()));
                
                if (book != null) {
                    KU2534814ReturnCommand command = new KU2534814ReturnCommand(book);
                    libraryManager.getCommandHistory().execute(command);
                    refreshBookTable();
                    
                    double fine = libraryManager.calculateFineForBook(book);
                    if (fine > 0) {
                        JOptionPane.showMessageDialog(this, "Book returned. Fine: $" + fine);
                    } else {
                        JOptionPane.showMessageDialog(this, "Book returned successfully!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void reserveBookDialog() {
        JTextField bookIdField = new JTextField();
        JTextField userIdField = new JTextField();

        Object[] message = {
            "Book ID:", bookIdField,
            "User ID:", userIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Reserve Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                KU2534814Book book = libraryManager.findBookById(Integer.parseInt(bookIdField.getText()));
                KU2534814User user = libraryManager.findUserById(Integer.parseInt(userIdField.getText()));
                
                if (book != null && user != null) {
                    KU2534814ReserveCommand command = new KU2534814ReserveCommand(book, user);
                    libraryManager.getCommandHistory().execute(command);
                    refreshBookTable();
                    JOptionPane.showMessageDialog(this, "Book reserved successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Book or User not found!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void cancelReservationDialog() {
        JTextField bookIdField = new JTextField();

        Object[] message = {
            "Book ID:", bookIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Cancel Reservation", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                KU2534814Book book = libraryManager.findBookById(Integer.parseInt(bookIdField.getText()));
                
                if (book != null) {
                    KU2534814CancelReservationCommand command = new KU2534814CancelReservationCommand(book);
                    libraryManager.getCommandHistory().execute(command);
                    refreshBookTable();
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void viewCommandHistory() {
        libraryManager.getCommandHistory().showHistory();
    }

    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        for (KU2534814Book book : libraryManager.getBooks()) {
            bookTableModel.addRow(new Object[]{
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getState().getStateName()
            });
        }
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (KU2534814User user : libraryManager.getUsers()) {
            userTableModel.addRow(new Object[]{
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType(),
                user.getBorrowedBooks().size()
            });
        }
    }

    private com.library.interfaces.KU2534814FineStrategyInterface getFineStrategy(String userType) {
        switch (userType) {
            case "Student":
                return new KU2534814StudentFineStrategy();
            case "Faculty":
                return new KU2534814FacultyFineStrategy();
            case "Guest":
                return new KU2534814GuestFineStrategy();
            default:
                return new KU2534814StudentFineStrategy();
        }
    }

    private void loadSampleData() {
        // Sample books
        KU2534814Book book1 = new KU2534814BookBuilder()
            .setId(1)
            .setIsbn("978-0134685991")
            .setTitle("Effective Java")
            .setAuthor("Joshua Bloch")
            .setPrice(45.99)
            .build();
        
        KU2534814Book book2 = new KU2534814BookBuilder()
            .setId(2)
            .setIsbn("978-0596009205")
            .setTitle("Head First Design Patterns")
            .setAuthor("Eric Freeman")
            .setPrice(39.99)
            .build();

        libraryManager.addBook(book1);
        libraryManager.addBook(book2);

        // Sample users
        KU2534814User user1 = new KU2534814User(1, "John Doe", "john@example.com", "Student", new KU2534814StudentFineStrategy());
        KU2534814User user2 = new KU2534814User(2, "Jane Smith", "jane@example.com", "Faculty", new KU2534814FacultyFineStrategy());

        libraryManager.addUser(user1);
        libraryManager.addUser(user2);

        refreshBookTable();
        refreshUserTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KU2534814LibraryGUI gui = new KU2534814LibraryGUI();
            gui.setVisible(true);
        });
    }
}
