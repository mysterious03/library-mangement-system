-- ============================================================================
-- SMART LIBRARY MANAGEMENT SYSTEM - ADVANCED SQL ANALYTICAL REPORTS
-- Demonstrating JOINs, GROUP BY, HAVING, Subqueries, Aggregations, & Window functions
-- ============================================================================

-- ----------------------------------------------------------------------------
-- REPORT 1: MOST BORROWED BOOKS (Top 10 Popular Books)
-- Demonstrates: INNER JOIN, GROUP BY, COUNT, ORDER BY DESC
-- ----------------------------------------------------------------------------
SELECT 
    b.book_id,
    b.isbn,
    b.title,
    a.author_name,
    c.category_name,
    COUNT(i.issue_id) AS total_times_borrowed,
    COUNT(DISTINCT i.student_id) AS unique_borrowers
FROM BOOKS b
JOIN AUTHORS a ON b.author_id = a.author_id
JOIN CATEGORIES c ON b.category_id = c.category_id
LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id
LEFT JOIN ISSUES i ON bc.copy_id = i.copy_id
GROUP BY b.book_id, b.isbn, b.title, a.author_name, c.category_name
ORDER BY total_times_borrowed DESC, b.title ASC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- REPORT 2: MOST ACTIVE STUDENTS (Top Borrowers)
-- Demonstrates: Multiple JOINs, GROUP BY, HAVING, COUNT
-- ----------------------------------------------------------------------------
SELECT 
    s.student_id,
    s.register_number,
    u.name AS student_name,
    s.department,
    s.year,
    COUNT(i.issue_id) AS total_books_borrowed,
    SUM(CASE WHEN i.status IN ('ISSUED', 'OVERDUE') THEN 1 ELSE 0 END) AS currently_borrowed_count
FROM STUDENTS s
JOIN USERS u ON s.user_id = u.user_id
JOIN ISSUES i ON s.student_id = i.student_id
GROUP BY s.student_id, s.register_number, u.name, s.department, s.year
HAVING COUNT(i.issue_id) > 0
ORDER BY total_books_borrowed DESC, u.name ASC;

-- ----------------------------------------------------------------------------
-- REPORT 3: OVERDUE BOOKS (Books Exceeding Due Date with Estimated Fine)
-- Demonstrates: Filtering, DATEDIFF, Calculated Fields, Subquery
-- ----------------------------------------------------------------------------
SELECT 
    i.issue_id,
    b.title AS book_title,
    bc.accession_number,
    u.name AS student_name,
    s.register_number,
    u.email AS contact_email,
    u.phone AS contact_phone,
    i.issue_date,
    i.due_date,
    DATEDIFF(CURRENT_DATE, i.due_date) AS days_overdue,
    (DATEDIFF(CURRENT_DATE, i.due_date) * 5.0) AS estimated_fine_inr
FROM ISSUES i
JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id
JOIN BOOKS b ON bc.book_id = b.book_id
JOIN STUDENTS s ON i.student_id = s.student_id
JOIN USERS u ON s.user_id = u.user_id
WHERE (i.status = 'ISSUED' OR i.status = 'OVERDUE')
  AND i.due_date < CURRENT_DATE
ORDER BY days_overdue DESC;

-- ----------------------------------------------------------------------------
-- REPORT 4: STUDENTS WITH UNPAID FINES
-- Demonstrates: JOIN, GROUP BY, SUM, HAVING clause
-- ----------------------------------------------------------------------------
SELECT 
    s.student_id,
    s.register_number,
    u.name AS student_name,
    s.department,
    u.phone,
    COUNT(f.fine_id) AS unpaid_fines_count,
    SUM(f.amount) AS total_fine_amount
FROM STUDENTS s
JOIN USERS u ON s.user_id = u.user_id
JOIN FINES f ON s.student_id = f.student_id
WHERE f.payment_status = 'UNPAID'
GROUP BY s.student_id, s.register_number, u.name, s.department, u.phone
HAVING SUM(f.amount) > 0
ORDER BY total_fine_amount DESC;

-- ----------------------------------------------------------------------------
-- REPORT 5: BOOKS DISTRIBUTION BY CATEGORY
-- Demonstrates: Aggregate functions, GROUP BY, Subqueries
-- ----------------------------------------------------------------------------
SELECT 
    c.category_id,
    c.category_name,
    COUNT(DISTINCT b.book_id) AS total_titles,
    COUNT(bc.copy_id) AS total_physical_copies,
    SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies,
    SUM(CASE WHEN bc.status = 'ISSUED' THEN 1 ELSE 0 END) AS issued_copies
FROM CATEGORIES c
LEFT JOIN BOOKS b ON c.category_id = b.category_id
LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id
GROUP BY c.category_id, c.category_name
ORDER BY total_physical_copies DESC;

-- ----------------------------------------------------------------------------
-- REPORT 6: AVAILABLE BOOKS REPORT (Shelf Audit)
-- Demonstrates: JOINs, Filtering on availability, Sorting by Shelf
-- ----------------------------------------------------------------------------
SELECT 
    bc.accession_number,
    b.title AS book_title,
    b.isbn,
    a.author_name,
    c.category_name,
    bc.shelf_location,
    bc.status
FROM BOOK_COPIES bc
JOIN BOOKS b ON bc.book_id = b.book_id
JOIN AUTHORS a ON b.author_id = a.author_id
JOIN CATEGORIES c ON b.category_id = c.category_id
WHERE bc.status = 'AVAILABLE'
ORDER BY bc.shelf_location ASC, b.title ASC;

-- ----------------------------------------------------------------------------
-- REPORT 7: CURRENTLY ISSUED BOOKS REPORT
-- Demonstrates: Detailed Circulation Tracking
-- ----------------------------------------------------------------------------
SELECT 
    i.issue_id,
    b.title AS book_title,
    bc.accession_number,
    u.name AS issued_to_student,
    s.register_number,
    i.issue_date,
    i.due_date,
    CASE 
        WHEN i.due_date < CURRENT_DATE THEN 'OVERDUE'
        ELSE 'ACTIVE'
    END AS loan_status
FROM ISSUES i
JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id
JOIN BOOKS b ON bc.book_id = b.book_id
JOIN STUDENTS s ON i.student_id = s.student_id
JOIN USERS u ON s.user_id = u.user_id
WHERE i.status = 'ISSUED'
ORDER BY i.due_date ASC;

-- ----------------------------------------------------------------------------
-- REPORT 8: RESERVATION STATISTICS AND QUEUES
-- Demonstrates: JOIN, COUNT, GROUP BY
-- ----------------------------------------------------------------------------
SELECT 
    b.book_id,
    b.title AS book_title,
    COUNT(r.reservation_id) AS total_reservations,
    SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END) AS pending_queue_length,
    SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END) AS fulfilled_reservations,
    SUM(CASE WHEN r.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_reservations
FROM BOOKS b
JOIN RESERVATIONS r ON b.book_id = r.book_id
GROUP BY b.book_id, b.title
ORDER BY pending_queue_length DESC, total_reservations DESC;

-- ----------------------------------------------------------------------------
-- REPORT 9: MONTHLY BORROWING STATISTICS
-- Demonstrates: Date formatting, Aggregation, GROUP BY year/month
-- ----------------------------------------------------------------------------
SELECT 
    YEAR(issue_date) AS issue_year,
    MONTH(issue_date) AS issue_month,
    COUNT(issue_id) AS total_issues_count,
    COUNT(DISTINCT student_id) AS active_students_count
FROM ISSUES
GROUP BY YEAR(issue_date), MONTH(issue_date)
ORDER BY issue_year DESC, issue_month DESC;

-- ----------------------------------------------------------------------------
-- REPORT 10: FINE COLLECTION STATISTICS
-- Demonstrates: SUM, COUNT, AVG, Financial Summary
-- ----------------------------------------------------------------------------
SELECT 
    payment_status,
    COUNT(fine_id) AS total_fines_count,
    SUM(amount) AS total_fine_amount,
    AVG(amount) AS average_fine_amount,
    MIN(amount) AS min_fine_amount,
    MAX(amount) AS max_fine_amount
FROM FINES
GROUP BY payment_status;
