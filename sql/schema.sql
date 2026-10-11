-- ============================================================================
-- SMART LIBRARY MANAGEMENT SYSTEM - RELATIONAL DATABASE SCHEMA
-- Compatible with Oracle, MySQL, and H2 (ANSI SQL Standard)
-- ============================================================================

-- Drop views if existing
DROP VIEW IF EXISTS VW_OVERDUE_BOOKS;
DROP VIEW IF EXISTS VW_ACTIVE_ISSUES;
DROP VIEW IF EXISTS VW_BOOK_DETAILS;
DROP VIEW IF EXISTS VW_STUDENT_SUMMARY;

-- Drop tables in reverse foreign key order
DROP TABLE IF EXISTS AUDIT_LOG;
DROP TABLE IF EXISTS FINES;
DROP TABLE IF EXISTS RESERVATIONS;
DROP TABLE IF EXISTS ISSUES;
DROP TABLE IF EXISTS BOOK_COPIES;
DROP TABLE IF EXISTS BOOKS;
DROP TABLE IF EXISTS PUBLISHERS;
DROP TABLE IF EXISTS CATEGORIES;
DROP TABLE IF EXISTS AUTHORS;
DROP TABLE IF EXISTS LIBRARIANS;
DROP TABLE IF EXISTS STUDENTS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS SYSTEM_SETTINGS;

-- ----------------------------------------------------------------------------
-- 1. SYSTEM_SETTINGS Table (Configurable Library Rules)
-- ----------------------------------------------------------------------------
CREATE TABLE SYSTEM_SETTINGS (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- ----------------------------------------------------------------------------
-- 2. USERS Table (Base Authentication & RBAC)
-- ----------------------------------------------------------------------------
CREATE TABLE USERS (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'LIBRARIAN', 'ADMIN')),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 3. STUDENTS Table (1:1 with USERS for role = 'STUDENT')
-- ----------------------------------------------------------------------------
CREATE TABLE STUDENTS (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    register_number VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    year INT NOT NULL CHECK (year BETWEEN 1 AND 5),
    section VARCHAR(10) NOT NULL,
    CONSTRAINT fk_students_users FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 4. LIBRARIANS Table (1:1 with USERS for role = 'LIBRARIAN')
-- ----------------------------------------------------------------------------
CREATE TABLE LIBRARIANS (
    librarian_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT fk_librarians_users FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 5. AUTHORS Table
-- ----------------------------------------------------------------------------
CREATE TABLE AUTHORS (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(150) NOT NULL,
    biography VARCHAR(1000)
);

-- ----------------------------------------------------------------------------
-- 6. CATEGORIES Table
-- ----------------------------------------------------------------------------
CREATE TABLE CATEGORIES (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

-- ----------------------------------------------------------------------------
-- 7. PUBLISHERS Table
-- ----------------------------------------------------------------------------
CREATE TABLE PUBLISHERS (
    publisher_id INT AUTO_INCREMENT PRIMARY KEY,
    publisher_name VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    contact VARCHAR(100)
);

-- ----------------------------------------------------------------------------
-- 8. BOOKS Table
-- ----------------------------------------------------------------------------
CREATE TABLE BOOKS (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    author_id INT NOT NULL,
    category_id INT NOT NULL,
    publisher_id INT NOT NULL,
    publication_year INT CHECK (publication_year >= 1800),
    edition VARCHAR(50),
    language VARCHAR(50) DEFAULT 'English',
    description VARCHAR(1500),
    CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES AUTHORS(author_id),
    CONSTRAINT fk_books_category FOREIGN KEY (category_id) REFERENCES CATEGORIES(category_id),
    CONSTRAINT fk_books_publisher FOREIGN KEY (publisher_id) REFERENCES PUBLISHERS(publisher_id)
);

-- ----------------------------------------------------------------------------
-- 9. BOOK_COPIES Table (Physical Items per Book)
-- ----------------------------------------------------------------------------
CREATE TABLE BOOK_COPIES (
    copy_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    accession_number VARCHAR(50) NOT NULL UNIQUE,
    shelf_location VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ISSUED', 'RESERVED', 'LOST', 'MAINTENANCE')),
    CONSTRAINT fk_copies_book FOREIGN KEY (book_id) REFERENCES BOOKS(book_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 10. ISSUES Table (Circulation Transactions)
-- ----------------------------------------------------------------------------
CREATE TABLE ISSUES (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    copy_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'ISSUED' CHECK (status IN ('ISSUED', 'RETURNED', 'OVERDUE')),
    CONSTRAINT fk_issues_student FOREIGN KEY (student_id) REFERENCES STUDENTS(student_id),
    CONSTRAINT fk_issues_copy FOREIGN KEY (copy_id) REFERENCES BOOK_COPIES(copy_id)
);

-- ----------------------------------------------------------------------------
-- 11. RESERVATIONS Table
-- ----------------------------------------------------------------------------
CREATE TABLE RESERVATIONS (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'COMPLETED', 'CANCELLED')),
    queue_number INT NOT NULL,
    CONSTRAINT fk_reservations_student FOREIGN KEY (student_id) REFERENCES STUDENTS(student_id),
    CONSTRAINT fk_reservations_book FOREIGN KEY (book_id) REFERENCES BOOKS(book_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 12. FINES Table
-- ----------------------------------------------------------------------------
CREATE TABLE FINES (
    fine_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL,
    student_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    payment_status VARCHAR(20) DEFAULT 'UNPAID' CHECK (payment_status IN ('UNPAID', 'PAID')),
    payment_date DATE,
    CONSTRAINT fk_fines_issue FOREIGN KEY (issue_id) REFERENCES ISSUES(issue_id) ON DELETE CASCADE,
    CONSTRAINT fk_fines_student FOREIGN KEY (student_id) REFERENCES STUDENTS(student_id)
);

-- ----------------------------------------------------------------------------
-- 13. AUDIT_LOG Table
-- ----------------------------------------------------------------------------
CREATE TABLE AUDIT_LOG (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES USERS(user_id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------------------
-- INDEXES FOR QUERY OPTIMIZATION
-- ----------------------------------------------------------------------------
CREATE INDEX idx_books_title ON BOOKS(title);
CREATE INDEX idx_books_isbn ON BOOKS(isbn);
CREATE INDEX idx_books_category ON BOOKS(category_id);
CREATE INDEX idx_copies_status ON BOOK_COPIES(status);
CREATE INDEX idx_issues_student ON ISSUES(student_id);
CREATE INDEX idx_issues_status ON ISSUES(status);
CREATE INDEX idx_fines_student_status ON FINES(student_id, payment_status);
CREATE INDEX idx_reservations_book_status ON RESERVATIONS(book_id, status);

-- ----------------------------------------------------------------------------
-- DATABASE VIEWS
-- ----------------------------------------------------------------------------

-- View: Book Details with real-time copy counts
CREATE VIEW VW_BOOK_DETAILS AS
SELECT 
    b.book_id,
    b.isbn,
    b.title,
    a.author_id,
    a.author_name,
    c.category_id,
    c.category_name,
    p.publisher_id,
    p.publisher_name,
    b.publication_year,
    b.edition,
    b.language,
    b.description,
    COUNT(bc.copy_id) AS total_copies,
    SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies
FROM BOOKS b
JOIN AUTHORS a ON b.author_id = a.author_id
JOIN CATEGORIES c ON b.category_id = c.category_id
JOIN PUBLISHERS p ON b.publisher_id = p.publisher_id
LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id
GROUP BY 
    b.book_id, b.isbn, b.title, a.author_id, a.author_name, 
    c.category_id, c.category_name, p.publisher_id, p.publisher_name, 
    b.publication_year, b.edition, b.language, b.description;

-- View: Active Issues with Student and Book Details
CREATE VIEW VW_ACTIVE_ISSUES AS
SELECT 
    i.issue_id,
    i.student_id,
    s.register_number,
    u.name AS student_name,
    u.email AS student_email,
    b.book_id,
    b.title AS book_title,
    bc.copy_id,
    bc.accession_number,
    bc.shelf_location,
    i.issue_date,
    i.due_date,
    i.status,
    DATEDIFF('DAY', i.due_date, CURRENT_DATE) AS overdue_days
FROM ISSUES i
JOIN STUDENTS s ON i.student_id = s.student_id
JOIN USERS u ON s.user_id = u.user_id
JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id
JOIN BOOKS b ON bc.book_id = b.book_id
WHERE i.status = 'ISSUED' OR i.status = 'OVERDUE';

-- View: Overdue Books Only
CREATE VIEW VW_OVERDUE_BOOKS AS
SELECT * FROM VW_ACTIVE_ISSUES
WHERE due_date < CURRENT_DATE;

-- View: Student Summary
CREATE VIEW VW_STUDENT_SUMMARY AS
SELECT 
    s.student_id,
    s.register_number,
    u.name AS student_name,
    u.email,
    s.department,
    s.year,
    s.section,
    (SELECT COUNT(*) FROM ISSUES WHERE student_id = s.student_id AND status IN ('ISSUED', 'OVERDUE')) AS active_borrowed_count,
    (SELECT COALESCE(SUM(amount), 0) FROM FINES WHERE student_id = s.student_id AND payment_status = 'UNPAID') AS unpaid_fine_amount,
    (SELECT COUNT(*) FROM RESERVATIONS WHERE student_id = s.student_id AND status = 'PENDING') AS active_reservations_count
FROM STUDENTS s
JOIN USERS u ON s.user_id = u.user_id;
