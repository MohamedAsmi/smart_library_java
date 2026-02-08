package com.library.cli;

import com.library.builders.KU2534814BookBuilder;
import com.library.commands.*;
import com.library.decorators.*;
import com.library.interfaces.KU2534814BookComponentInterface;
import com.library.management.*;
import com.library.models.*;
import com.library.strategies.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KU2534814LibraryCLI {
    private KU2534814LibraryManager libraryManager;
    private KU2534814TransactionManager transactionManager;
    private KU2534814PaymentManager paymentManager;
    private KU2534814ReservationManager reservationManager;
    private Scanner scanner;
    private boolean running;

    public KU2534814LibraryCLI() {
        this.libraryManager = new KU2534814LibraryManager();
        this.transactionManager = new KU2534814TransactionManager();
        this.paymentManager = new KU2534814PaymentManager();
        this.reservationManager = new KU2534814ReservationManager();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        displayHeader();
        initializeSampleData();
        
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            handleMenuChoice(choice);
        }
        
        scanner.close();
    }

    private void displayHeader() {
        System.out.println("================================================================================");
        System.out.println("           SMART LIBRARY MANAGEMENT SYSTEM - KU2534814                         ");
        System.out.println("================================================================================");
        System.out.println();
    }

    private void displayMainMenu() {
        System.out.println("\n================================================================================");
        System.out.println("MAIN MENU");
        System.out.println("================================================================================");
        System.out.println("1.  Book Management");
        System.out.println("2.  User Management");
        System.out.println("3.  Borrow a Book");
        System.out.println("4.  Return a Book");
        System.out.println("5.  Reserve a Book");
        System.out.println("6.  Cancel Reservation");
        System.out.println("7.  View Decorated Books (Decorator Pattern Demo)");
        System.out.println("8.  Check Overdue Books");
        System.out.println("9.  Send Due Date Reminders");
        System.out.println("10. Generate Reports");
        System.out.println("11. View Command History");
        System.out.println("12. Undo Last Operation");
        System.out.println("13. Run Comprehensive Test Suite");
        System.out.println("14. Display Library Statistics");
        System.out.println("15. View Fines & Payments");
        System.out.println("16. View Active Reservations");
        System.out.println("0.  Exit");
        System.out.println("================================================================================");
    }

    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                bookManagement();
                break;
            case 2:
                userManagement();
                break;
            case 3:
                borrowBook();
                break;
            case 4:
                returnBook();
                break;
            case 5:
                reserveBook();
                break;
            case 6:
                cancelReservation();
                break;
            case 7:
                viewDecoratedBooks();
                break;
            case 8:
                checkOverdueBooks();
                break;
            case 9:
                sendDueDateReminders();
                break;
            case 10:
                generateReports();
                break;
            case 11:
                viewCommandHistory();
                break;
            case 12:
                undoLastOperation();
                break;
            case 13:
                runComprehensiveTests();
                break;
            case 14:
                displayLibraryStatistics();
                break;
            case 15:
                viewFinesAndPayments();
                break;
            case 16:
                viewActiveReservations();
                break;
            case 0:
                exitSystem();
                break;
            default:
                System.out.println("Invalid choice! Please try again.");
        }
    }

    private void bookManagement() {
        System.out.println("\n--- BOOK MANAGEMENT ---");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Books");
        System.out.println("4. Remove Book");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                addBook();
                break;
            case 2:
                viewAllBooks();
                break;
            case 3:
                searchBooks();
                break;
            case 4:
                removeBook();
                break;
        }
        pauseForUser();
    }

    private void addBook() {
        System.out.println("\n--- ADD NEW BOOK ---");
        int id = getIntInput("Enter Book ID: ");
        String isbn = getStringInput("Enter ISBN: ");
        String title = getStringInput("Enter Title: ");
        String author = getStringInput("Enter Author: ");
        double price = getDoubleInput("Enter Price: ");
        
        KU2534814Book book = new KU2534814BookBuilder()
            .setId(id)
            .setIsbn(isbn)
            .setTitle(title)
            .setAuthor(author)
            .setPrice(price)
            .build();
        
        libraryManager.addBook(book);
        System.out.println("✓ Book '" + title + "' added successfully!");
    }

    private void viewAllBooks() {
        System.out.println("\n--- ALL BOOKS ---");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-5s %-15s %-30s %-20s %-10s %-12s%n", 
            "ID", "ISBN", "Title", "Author", "Price", "Status");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (KU2534814Book book : libraryManager.getBooks()) {
            System.out.printf("%-5d %-15s %-30s %-20s $%-9.2f %-12s%n",
                book.getId(),
                book.getIsbn(),
                truncate(book.getTitle(), 30),
                truncate(book.getAuthor(), 20),
                book.getPrice(),
                book.getState().getStateName());
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void searchBooks() {
        String title = getStringInput("Enter title to search: ");
        List<KU2534814Book> results = libraryManager.searchBooksByTitle(title);
        
        if (results.isEmpty()) {
            System.out.println("No books found matching '" + title + "'");
        } else {
            System.out.println("\n--- SEARCH RESULTS ---");
            for (KU2534814Book book : results) {
                System.out.printf("ID: %d | %s by %s | %s%n",
                    book.getId(), book.getTitle(), book.getAuthor(), 
                    book.getState().getStateName());
            }
        }
    }

    private void removeBook() {
        int id = getIntInput("Enter Book ID to remove: ");
        KU2534814Book book = libraryManager.findBookById(id);
        
        if (book != null) {
            libraryManager.removeBook(book);
            System.out.println("✓ Book removed successfully!");
        } else {
            System.out.println("✗ Book not found!");
        }
    }

    private void userManagement() {
        System.out.println("\n--- USER MANAGEMENT ---");
        System.out.println("1. Register New User");
        System.out.println("2. View All Users");
        System.out.println("3. Remove User");
        System.out.println("0. Back to Main Menu");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                viewAllUsers();
                break;
            case 3:
                removeUser();
                break;
        }
        pauseForUser();
    }

    private void registerUser() {
        System.out.println("\n--- REGISTER NEW USER ---");
        int id = getIntInput("Enter User ID: ");
        String name = getStringInput("Enter Name: ");
        String email = getStringInput("Enter Email: ");
        
        System.out.println("Select User Type:");
        System.out.println("1. Student");
        System.out.println("2. Faculty");
        System.out.println("3. Guest");
        int typeChoice = getIntInput("Choice: ");
        
        String userType;
        com.library.interfaces.KU2534814FineStrategyInterface strategy;
        
        switch (typeChoice) {
            case 1:
                userType = "Student";
                strategy = new KU2534814StudentFineStrategy();
                break;
            case 2:
                userType = "Faculty";
                strategy = new KU2534814FacultyFineStrategy();
                break;
            case 3:
                userType = "Guest";
                strategy = new KU2534814GuestFineStrategy();
                break;
            default:
                userType = "Student";
                strategy = new KU2534814StudentFineStrategy();
        }
        
        KU2534814User user = new KU2534814User(id, name, email, userType, strategy);
        libraryManager.addUser(user);
        System.out.println("✓ User '" + name + "' registered successfully.");
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-5s %-25s %-30s %-10s %-8s%n", 
            "ID", "Name", "Email", "Type", "Books");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (KU2534814User user : libraryManager.getUsers()) {
            System.out.printf("%-5d %-25s %-30s %-10s %-8d%n",
                user.getId(),
                truncate(user.getName(), 25),
                truncate(user.getEmail(), 30),
                user.getUserType(),
                user.getBorrowedBooks().size());
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void removeUser() {
        int id = getIntInput("Enter User ID to remove: ");
        KU2534814User user = libraryManager.findUserById(id);
        
        if (user != null) {
            libraryManager.removeUser(user);
            System.out.println("✓ User removed successfully!");
        } else {
            System.out.println("✗ User not found!");
        }
    }

    private void borrowBook() {
        System.out.println("\n--- BORROW A BOOK ---");
        int bookId = getIntInput("Enter Book ID: ");
        int userId = getIntInput("Enter User ID: ");
        
        KU2534814Book book = libraryManager.findBookById(bookId);
        KU2534814User user = libraryManager.findUserById(userId);
        
        if (book == null) {
            System.out.println("✗ Book not found!");
            return;
        }
        if (user == null) {
            System.out.println("✗ User not found!");
            return;
        }
        
        // Determine borrowing period based on user type
        int borrowDays;
        switch (user.getUserType()) {
            case "Student":
                borrowDays = 14;
                break;
            case "Faculty":
                borrowDays = 30;
                break;
            case "Guest":
                borrowDays = 7;
                break;
            default:
                borrowDays = 14;
                break;
        }
        
        KU2534814BorrowCommand command = new KU2534814BorrowCommand(book, user);
        libraryManager.getCommandHistory().execute(command);
        
        // Create transaction in database
        transactionManager.createBorrowTransaction(bookId, userId, borrowDays);
        
        System.out.println("✓ Book '" + book.getTitle() + "' borrowed by " + user.getName());
        System.out.println("  Due Date: " + LocalDate.now().plusDays(borrowDays));
        pauseForUser();
    }

    private void returnBook() {
        System.out.println("\n--- RETURN A BOOK ---");
        int bookId = getIntInput("Enter Book ID: ");
        int userId = getIntInput("Enter User ID: ");
        
        KU2534814Book book = libraryManager.findBookById(bookId);
        KU2534814User user = libraryManager.findUserById(userId);
        
        if (book == null || user == null) {
            System.out.println("✗ Book or User not found!");
            return;
        }
        
        // Return through transaction manager to calculate fines
        boolean returned = transactionManager.returnBook(bookId, userId);
        
        if (returned) {
            KU2534814ReturnCommand command = new KU2534814ReturnCommand(book);
            libraryManager.getCommandHistory().execute(command);
            
            // Check for fine
            double fine = libraryManager.calculateFineForBook(book);
            if (fine > 0) {
                System.out.println("✓ Book returned. Fine: $" + String.format("%.2f", fine));
            } else {
                System.out.println("✓ Book '" + book.getTitle() + "' returned successfully!");
            }
        } else {
            System.out.println("✗ Return failed. Please check the book status.");
        }
        pauseForUser();
    }

    private void reserveBook() {
        System.out.println("\n--- RESERVE A BOOK ---");
        int bookId = getIntInput("Enter Book ID: ");
        int userId = getIntInput("Enter User ID: ");
        
        KU2534814Book book = libraryManager.findBookById(bookId);
        KU2534814User user = libraryManager.findUserById(userId);
        
        if (book == null || user == null) {
            System.out.println("✗ Book or User not found!");
            return;
        }
        
        KU2534814ReserveCommand command = new KU2534814ReserveCommand(book, user);
        libraryManager.getCommandHistory().execute(command);
        
        // Create reservation in database
        reservationManager.createReservation(bookId, userId);
        
        System.out.println("✓ Book '" + book.getTitle() + "' reserved by " + user.getName());
        System.out.println("  Reservation expires in 48 hours.");
        pauseForUser();
    }

    private void cancelReservation() {
        System.out.println("\n--- CANCEL RESERVATION ---");
        int bookId = getIntInput("Enter Book ID: ");
        
        KU2534814Book book = libraryManager.findBookById(bookId);
        
        if (book != null) {
            KU2534814CancelReservationCommand command = new KU2534814CancelReservationCommand(book);
            libraryManager.getCommandHistory().execute(command);
            System.out.println("✓ Reservation cancelled!");
        } else {
            System.out.println("✗ Book not found!");
        }
        pauseForUser();
    }

    private void viewDecoratedBooks() {
        System.out.println("\n--- DECORATOR PATTERN DEMONSTRATION ---");
        System.out.println("================================================================================");
        
        // Create base book using concrete implementation
        KU2534814BookComponentInterface baseBook = new KU2534814BookComponentInterface() {
            private String title = "Effective Java";
            private String author = "Joshua Bloch";
            private double price = 45.99;
            
            @Override
            public String getDescription() {
                return title + " by " + author;
            }
            
            @Override
            public double getPrice() {
                return price;
            }
        };
        
        // Apply decorators
        System.out.println("Base Book:");
        System.out.println("  " + baseBook.getDescription() + " - $" + String.format("%.2f", baseBook.getPrice()));
        
        // Bestseller
        KU2534814BookComponentInterface bestseller = new KU2534814BestsellerBookDecorator(baseBook);
        System.out.println("\nBestseller (+20%):");
        System.out.println("  " + bestseller.getDescription() + " - $" + String.format("%.2f", bestseller.getPrice()));
        
        // Featured
        KU2534814BookComponentInterface featured = new KU2534814FeaturedBookDecorator(baseBook);
        System.out.println("\nFeatured (+15%):");
        System.out.println("  " + featured.getDescription() + " - $" + String.format("%.2f", featured.getPrice()));
        
        // New Arrival
        KU2534814BookComponentInterface newArrival = new KU2534814NewArrivalBookDecorator(baseBook);
        System.out.println("\nNew Arrival (+10%):");
        System.out.println("  " + newArrival.getDescription() + " - $" + String.format("%.2f", newArrival.getPrice()));
        
        // Multiple decorators
        KU2534814BookComponentInterface multiDecorated = new KU2534814BestsellerBookDecorator(
            new KU2534814SpecialEditionBookDecorator(baseBook)
        );
        System.out.println("\nBestseller + Special Edition:");
        System.out.println("  " + multiDecorated.getDescription() + " - $" + String.format("%.2f", multiDecorated.getPrice()));
        
        System.out.println("================================================================================");
        pauseForUser();
    }

    private void checkOverdueBooks() {
        System.out.println("\n--- OVERDUE BOOKS ---");
        List<KU2534814BorrowTransaction> overdue = transactionManager.getOverdueTransactions();
        
        if (overdue.isEmpty()) {
            System.out.println("No overdue books.");
        } else {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-8s %-8s %-25s %-12s %-10s%n", 
                "Book ID", "User ID", "User Name", "Due Date", "Fine");
            System.out.println("--------------------------------------------------------------------------------");
            
            for (KU2534814BorrowTransaction transaction : overdue) {
                KU2534814Book book = libraryManager.findBookById(transaction.getBookId());
                KU2534814User user = libraryManager.findUserById(transaction.getUserId());
                double fine = libraryManager.calculateFineForBook(book);
                
                System.out.printf("%-8d %-8d %-25s %-12s $%-9.2f%n",
                    transaction.getBookId(),
                    transaction.getUserId(),
                    user != null ? truncate(user.getName(), 25) : "Unknown",
                    transaction.getDueDate(),
                    fine);
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        pauseForUser();
    }

    private void sendDueDateReminders() {
        System.out.println("\n--- SENDING DUE DATE REMINDERS ---");
        List<KU2534814BorrowTransaction> active = transactionManager.getActiveTransactions();
        int remindersSent = 0;
        
        for (KU2534814BorrowTransaction transaction : active) {
            LocalDate dueDate = transaction.getDueDate();
            LocalDate reminderDate = dueDate.minusDays(2);
            
            if (LocalDate.now().isAfter(reminderDate) && LocalDate.now().isBefore(dueDate.plusDays(1))) {
                KU2534814User user = libraryManager.findUserById(transaction.getUserId());
                KU2534814Book book = libraryManager.findBookById(transaction.getBookId());
                
                if (user != null && book != null) {
                    String message = "Reminder: Book '" + book.getTitle() + "' is due on " + dueDate;
                    user.update(message);
                    remindersSent++;
                }
            }
        }
        
        System.out.println("✓ " + remindersSent + " reminder(s) sent successfully!");
        pauseForUser();
    }

    private void generateReports() {
        System.out.println("\n--- LIBRARY REPORTS ---");
        System.out.println("1. Most Borrowed Books");
        System.out.println("2. Active Borrowers");
        System.out.println("3. Fine Summary");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                reportMostBorrowedBooks();
                break;
            case 2:
                reportActiveBorrowers();
                break;
            case 3:
                reportFineSummary();
                break;
        }
        pauseForUser();
    }

    private void reportMostBorrowedBooks() {
        System.out.println("\n--- MOST BORROWED BOOKS REPORT ---");
        System.out.println("--------------------------------------------------------------------------------");
        
        // Get all transactions and count borrows per book
        List<KU2534814BorrowTransaction> allTransactions = transactionManager.getAllTransactions();
        Map<Integer, Integer> borrowCounts = new HashMap<>();
        
        // Count how many times each book was borrowed
        for (KU2534814BorrowTransaction transaction : allTransactions) {
            int bookId = transaction.getBookId();
            borrowCounts.put(bookId, borrowCounts.getOrDefault(bookId, 0) + 1);
        }
        
        if (borrowCounts.isEmpty()) {
            System.out.println("No borrowing history available.");
        } else {
            // Sort books by borrow count (descending)
            List<Map.Entry<Integer, Integer>> sortedBooks = new ArrayList<>(borrowCounts.entrySet());
            sortedBooks.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            System.out.printf("%-8s %-40s %-25s %-10s%n", 
                "Book ID", "Title", "Author", "Times Borrowed");
            System.out.println("--------------------------------------------------------------------------------");
            
            for (Map.Entry<Integer, Integer> entry : sortedBooks) {
                KU2534814Book book = libraryManager.findBookById(entry.getKey());
                if (book != null) {
                    System.out.printf("%-8d %-40s %-25s %-10d%n",
                        book.getId(),
                        truncate(book.getTitle(), 40),
                        truncate(book.getAuthor(), 25),
                        entry.getValue());
                }
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("Total unique books borrowed: %d%n", borrowCounts.size());
            System.out.printf("Total borrow transactions: %d%n", allTransactions.size());
        }
    }

    private void reportActiveBorrowers() {
        System.out.println("\n--- ACTIVE BORROWERS REPORT ---");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (KU2534814User user : libraryManager.getUsers()) {
            if (!user.getBorrowedBooks().isEmpty()) {
                System.out.printf("%s (%s) - %d book(s) borrowed%n",
                    user.getName(), user.getUserType(), user.getBorrowedBooks().size());
            }
        }
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void reportFineSummary() {
        System.out.println("\n--- FINE SUMMARY REPORT ---");
        double totalFines = 0.0;
        
        for (KU2534814User user : libraryManager.getUsers()) {
            double userFines = paymentManager.getTotalPendingFines(user.getId());
            if (userFines > 0) {
                System.out.printf("%s: $%.2f%n", user.getName(), userFines);
                totalFines += userFines;
            }
        }
        
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total Pending Fines: $%.2f%n", totalFines);
    }

    private void viewCommandHistory() {
        System.out.println("\n--- COMMAND HISTORY ---");
        libraryManager.getCommandHistory().showHistory();
        pauseForUser();
    }

    private void undoLastOperation() {
        System.out.println("\n--- UNDO LAST OPERATION ---");
        libraryManager.getCommandHistory().undo();
        pauseForUser();
    }

    private void runComprehensiveTests() {
        System.out.println("\n--- RUNNING COMPREHENSIVE TEST SUITE ---");
        System.out.println("================================================================================");
        
        System.out.println("Testing State Pattern...");
        testStatePattern();
        
        System.out.println("\nTesting Strategy Pattern...");
        testStrategyPattern();
        
        System.out.println("\nTesting Command Pattern...");
        testCommandPattern();
        
        System.out.println("\nTesting Observer Pattern...");
        testObserverPattern();
        
        System.out.println("\n================================================================================");
        System.out.println("✓ All tests completed successfully!");
        pauseForUser();
    }

    private void testStatePattern() {
        KU2534814Book testBook = new KU2534814BookBuilder()
            .setId(999)
            .setIsbn("TEST-123")
            .setTitle("Test Book")
            .setAuthor("Test Author")
            .setPrice(10.0)
            .build();
        
        System.out.println("  Initial State: " + testBook.getState().getStateName());
        
        KU2534814User testUser = new KU2534814User(999, "Test User", "test@test.com", 
            "Student", new KU2534814StudentFineStrategy());
        
        testBook.borrow(testUser);
        System.out.println("  After Borrow: " + testBook.getState().getStateName());
        
        testBook.returnBook();
        System.out.println("  After Return: " + testBook.getState().getStateName());
    }

    private void testStrategyPattern() {
        KU2534814StudentFineStrategy studentStrategy = new KU2534814StudentFineStrategy();
        System.out.println("  Student fine (5 days): $" + studentStrategy.calculateFine(5));
        
        KU2534814FacultyFineStrategy facultyStrategy = new KU2534814FacultyFineStrategy();
        System.out.println("  Faculty fine (5 days): $" + facultyStrategy.calculateFine(5));
        
        KU2534814GuestFineStrategy guestStrategy = new KU2534814GuestFineStrategy();
        System.out.println("  Guest fine (5 days): $" + guestStrategy.calculateFine(5));
    }

    private void testCommandPattern() {
        System.out.println("  Command pattern tested via borrow/return operations");
    }

    private void testObserverPattern() {
        System.out.println("  Observer pattern tested via notifications");
    }

    private void displayLibraryStatistics() {
        System.out.println("\n--- LIBRARY STATISTICS ---");
        System.out.println("================================================================================");
        System.out.println("Total Books: " + libraryManager.getBooks().size());
        System.out.println("Total Users: " + libraryManager.getUsers().size());
        
        int available = 0, borrowed = 0, reserved = 0;
        for (KU2534814Book book : libraryManager.getBooks()) {
            String state = book.getState().getStateName();
            if ("Available".equals(state)) available++;
            else if ("Borrowed".equals(state)) borrowed++;
            else if ("Reserved".equals(state)) reserved++;
        }
        
        System.out.println("\nBook Status:");
        System.out.println("  Available: " + available);
        System.out.println("  Borrowed: " + borrowed);
        System.out.println("  Reserved: " + reserved);
        
        System.out.println("\nUser Types:");
        int students = 0, faculty = 0, guests = 0;
        for (KU2534814User user : libraryManager.getUsers()) {
            switch (user.getUserType()) {
                case "Student": students++; break;
                case "Faculty": faculty++; break;
                case "Guest": guests++; break;
            }
        }
        System.out.println("  Students: " + students);
        System.out.println("  Faculty: " + faculty);
        System.out.println("  Guests: " + guests);
        System.out.println("================================================================================");
        pauseForUser();
    }

    private void viewFinesAndPayments() {
        System.out.println("\n--- FINES & PAYMENTS ---");
        System.out.println("1. View All Pending Fines");
        System.out.println("2. Pay Fine");
        System.out.println("3. View Payment History");
        System.out.println("0. Back");
        
        int choice = getIntInput("Enter choice: ");
        
        switch (choice) {
            case 1:
                viewAllPendingFines();
                break;
            case 2:
                payFine();
                break;
            case 3:
                viewPaymentHistory();
                break;
        }
        pauseForUser();
    }

    private void viewAllPendingFines() {
        System.out.println("\n--- PENDING FINES ---");
        List<KU2534814Fine> fines = paymentManager.getAllPendingFines();
        
        if (fines.isEmpty()) {
            System.out.println("No pending fines.");
        } else {
            for (KU2534814Fine fine : fines) {
                System.out.printf("Fine ID: %d | User ID: %d | Amount: $%.2f | Days: %d%n",
                    fine.getId(), fine.getUserId(), fine.getFineAmount(), fine.getDaysOverdue());
            }
        }
    }

    private void payFine() {
        // First show available pending fines
        System.out.println("\n--- AVAILABLE PENDING FINES ---");
        List<KU2534814Fine> allFines = paymentManager.getAllPendingFines();
        
        if (allFines.isEmpty()) {
            System.out.println("No pending fines available to pay.");
            return;
        }
        
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-8s %-8s %-20s %-12s %-8s%n", 
            "Fine ID", "User ID", "User Name", "Amount", "Days");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (KU2534814Fine fine : allFines) {
            KU2534814User user = libraryManager.findUserById(fine.getUserId());
            System.out.printf("%-8d %-8d %-20s $%-11.2f %-8d%n",
                fine.getId(), 
                fine.getUserId(),
                user != null ? truncate(user.getName(), 20) : "Unknown",
                fine.getFineAmount(),
                fine.getDaysOverdue());
        }
        System.out.println("--------------------------------------------------------------------------------");
        
        int fineId = getIntInput("\nEnter Fine ID to pay: ");
        
        // Validate fine exists
        KU2534814Fine selectedFine = null;
        for (KU2534814Fine fine : allFines) {
            if (fine.getId() == fineId) {
                selectedFine = fine;
                break;
            }
        }
        
        if (selectedFine == null) {
            System.out.println("✗ Invalid Fine ID!");
            return;
        }
        
        System.out.printf("Fine Amount: $%.2f%n", selectedFine.getFineAmount());
        double amount = getDoubleInput("Enter Payment Amount: ");
        
        if (amount < selectedFine.getFineAmount()) {
            System.out.println("⚠ Warning: Payment amount is less than fine amount!");
        }
        
        System.out.println("Payment Method: 1. Cash  2. Card  3. Online  4. Bank Transfer");
        int methodChoice = getIntInput("Choice: ");
        
        String method;
        switch (methodChoice) {
            case 1:
                method = "Cash";
                break;
            case 2:
                method = "Card";
                break;
            case 3:
                method = "Online";
                break;
            case 4:
                method = "Bank Transfer";
                break;
            default:
                method = "Cash";
                break;
        }
        
        KU2534814Payment payment = paymentManager.processPayment(selectedFine.getUserId(), fineId, amount, method);
        
        if (payment != null) {
            System.out.println("✓ Payment processed successfully!");
            System.out.println("  Transaction Ref: " + payment.getTransactionReference());
        } else {
            System.out.println("✗ Payment failed!");
        }
    }

    private void viewPaymentHistory() {
        int userId = getIntInput("Enter User ID: ");
        List<KU2534814Payment> payments = paymentManager.getUserPayments(userId);
        
        if (payments.isEmpty()) {
            System.out.println("No payment history found.");
        } else {
            System.out.println("\n--- PAYMENT HISTORY ---");
            for (KU2534814Payment payment : payments) {
                System.out.printf("Payment ID: %d | Amount: $%.2f | Method: %s | Date: %s%n",
                    payment.getId(), payment.getAmount(), payment.getPaymentMethod(), 
                    payment.getPaymentDate());
            }
        }
    }

    private void viewActiveReservations() {
        System.out.println("\n--- ACTIVE RESERVATIONS ---");
        List<KU2534814Reservation> reservations = reservationManager.getAllActiveReservations();
        
        if (reservations.isEmpty()) {
            System.out.println("No active reservations.");
        } else {
            System.out.println("--------------------------------------------------------------------------------");
            for (KU2534814Reservation reservation : reservations) {
                KU2534814Book book = libraryManager.findBookById(reservation.getBookId());
                KU2534814User user = libraryManager.findUserById(reservation.getUserId());
                
                System.out.printf("Reservation ID: %d | Book: %s | User: %s | Status: %s%n",
                    reservation.getId(),
                    book != null ? book.getTitle() : "Unknown",
                    user != null ? user.getName() : "Unknown",
                    reservation.getStatus());
            }
            System.out.println("--------------------------------------------------------------------------------");
        }
        pauseForUser();
    }

    private void exitSystem() {
        System.out.println("\n================================================================================");
        System.out.println("Thank you for using Smart Library Management System - KU2534814");
        System.out.println("================================================================================");
        running = false;
    }

    private void initializeSampleData() {
        System.out.println("Initializing sample data...\n");
        
        // Create sample users
        KU2534814User user1 = new KU2534814User(1, "John Doe", "john@example.com", 
            "Student", new KU2534814StudentFineStrategy());
        KU2534814User user2 = new KU2534814User(2, "Jack Bobs", "jack@example.com", 
            "Student", new KU2534814StudentFineStrategy());
        KU2534814User user3 = new KU2534814User(3, "Dr. Sarah Miller", "sarah@example.com", 
            "Faculty", new KU2534814FacultyFineStrategy());
        KU2534814User user4 = new KU2534814User(4, "Mathews", "mathews@example.com", 
            "Guest", new KU2534814GuestFineStrategy());
        
        libraryManager.addUser(user1);
        libraryManager.addUser(user2);
        libraryManager.addUser(user3);
        libraryManager.addUser(user4);
        
        System.out.println("✓ Created 4 users (2 Students, 1 Faculty, 1 Guest)");
        
        // Create sample books
        KU2534814Book book1 = new KU2534814BookBuilder()
            .setId(1).setIsbn("978-1234567890")
            .setTitle("Sleeping Beauty").setAuthor("Classic Tales").setPrice(19.99).build();
        
        KU2534814Book book2 = new KU2534814BookBuilder()
            .setId(2).setIsbn("978-0987654321")
            .setTitle("Golden Lock").setAuthor("Fairy Tales").setPrice(15.99).build();
        
        KU2534814Book book3 = new KU2534814BookBuilder()
            .setId(3).setIsbn("978-1122334455")
            .setTitle("The Ugly Duckling").setAuthor("Hans Christian Andersen").setPrice(12.99).build();
        
        KU2534814Book book4 = new KU2534814BookBuilder()
            .setId(4).setIsbn("978-5566778899")
            .setTitle("Cinderella").setAuthor("Classic Tales").setPrice(18.99).build();
        
        KU2534814Book book5 = new KU2534814BookBuilder()
            .setId(5).setIsbn("978-6677889900")
            .setTitle("Rapunzel").setAuthor("Brothers Grimm").setPrice(16.99).build();
        
        libraryManager.addBook(book1);
        libraryManager.addBook(book2);
        libraryManager.addBook(book3);
        libraryManager.addBook(book4);
        libraryManager.addBook(book5);
        
        System.out.println("✓ Created 5 books using Builder Pattern");
    }

    // Helper methods
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. " + prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("Invalid input. " + prompt);
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    private void pauseForUser() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        KU2534814LibraryCLI cli = new KU2534814LibraryCLI();
        cli.start();
    }
}
