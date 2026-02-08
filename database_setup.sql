-- Smart Library Management System Database
-- Student ID: KU2534814

-- Create database
CREATE DATABASE IF NOT EXISTS smart_library;
USE smart_library;

-- Books table
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(255),
    author VARCHAR(255),
    category VARCHAR(100),
    price DOUBLE,
    status ENUM('Available', 'Borrowed', 'Reserved') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    contact_number VARCHAR(20),
    user_type ENUM('Student', 'Faculty', 'Guest') NOT NULL,
    membership_status ENUM('Active', 'Suspended', 'Inactive') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Borrowing Transactions table
CREATE TABLE IF NOT EXISTS borrow_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('Active', 'Returned', 'Overdue') DEFAULT 'Active',
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Active', 'Fulfilled', 'Cancelled', 'Expired') DEFAULT 'Active',
    notification_sent BOOLEAN DEFAULT FALSE,
    expiry_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Fines and Payments table
CREATE TABLE IF NOT EXISTS fines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    borrow_transaction_id INT,
    fine_amount DECIMAL(10, 2) NOT NULL,
    days_overdue INT NOT NULL,
    fine_rate DECIMAL(10, 2) NOT NULL,
    status ENUM('Pending', 'Paid', 'Waived') DEFAULT 'Pending',
    payment_date TIMESTAMP NULL,
    payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (borrow_transaction_id) REFERENCES borrow_transactions(id) ON DELETE SET NULL
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    fine_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('Cash', 'Card', 'Online', 'Bank Transfer') NOT NULL,
    transaction_reference VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Pending', 'Completed', 'Failed', 'Refunded') DEFAULT 'Completed',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (fine_id) REFERENCES fines(id) ON DELETE SET NULL
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    notification_type ENUM('Due Date', 'Overdue', 'Reservation Available', 'Fine', 'General') NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- System Activity Log table
CREATE TABLE IF NOT EXISTS activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Book History table (for tracking all book-related activities)
CREATE TABLE IF NOT EXISTS book_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT,
    action VARCHAR(50) NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Sample data for books
INSERT INTO books (id, isbn, title, author, category, price, status) VALUES
(1, '978-0134685991', 'Effective Java', 'Joshua Bloch', 'Programming', 45.99, 'Available'),
(2, '978-0596009205', 'Head First Design Patterns', 'Eric Freeman', 'Software Engineering', 39.99, 'Available'),
(3, '978-0134494166', 'Clean Code', 'Robert C. Martin', 'Programming', 42.99, 'Available'),
(4, '978-0132350884', 'Clean Architecture', 'Robert C. Martin', 'Software Engineering', 38.99, 'Available'),
(5, '978-0201633610', 'Design Patterns', 'Gang of Four', 'Software Engineering', 54.99, 'Available');

-- Sample data for users
INSERT INTO users (id, name, email, contact_number, user_type, membership_status) VALUES
(1, 'John Doe', 'john@example.com', '+1234567890', 'Student', 'Active'),
(2, 'Jane Smith', 'jane@example.com', '+1234567891', 'Faculty', 'Active'),
(3, 'Bob Johnson', 'bob@example.com', '+1234567892', 'Guest', 'Active'),
(4, 'Alice Williams', 'alice@example.com', '+1234567893', 'Student', 'Active'),
(5, 'Dr. Brown', 'brown@example.com', '+1234567894', 'Faculty', 'Active');

-- Sample borrow transactions
INSERT INTO borrow_transactions (book_id, user_id, borrow_date, due_date, status) VALUES
(2, 1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 14 DAY), 'Active'),
(3, 4, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'Overdue');

-- Sample fines (for overdue books)
INSERT INTO fines (user_id, borrow_transaction_id, fine_amount, days_overdue, fine_rate, status) VALUES
(1, 1, 6.00, 6, 1.00, 'Pending'),
(4, 2, 18.00, 6, 1.00, 'Pending'),
(3, NULL, 15.00, 5, 2.00, 'Pending');

-- Sample activity log
INSERT INTO activity_log (user_id, action, entity_type, entity_id, details) VALUES
(1, 'BORROW_BOOK', 'Book', 2, 'Borrowed: Head First Design Patterns'),
(4, 'BORROW_BOOK', 'Book', 3, 'Borrowed: Clean Code');

-- Create indexes for better performance
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_status ON books(status);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_borrow_status ON borrow_transactions(status);
CREATE INDEX idx_borrow_dates ON borrow_transactions(borrow_date, due_date);
CREATE INDEX idx_reservations_status ON reservations(status);
CREATE INDEX idx_fines_status ON fines(status);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);

SELECT 'Database setup completed successfully!' AS Status;
SELECT 'Tables created: books, users, borrow_transactions, reservations, fines, payments, notifications, activity_log, book_history' AS Info;
