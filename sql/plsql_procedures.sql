-- ============================================================================
-- SMART LIBRARY MANAGEMENT SYSTEM - PL/SQL PROCEDURES, FUNCTIONS & TRIGGERS
-- Target: Oracle PL/SQL & MySQL Stored Routines Compatible
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. FUNCTION: GET_BOOK_AVAILABILITY
-- Returns count of available physical copies for a given book_id
-- ----------------------------------------------------------------------------
-- Oracle PL/SQL Syntax:
CREATE OR REPLACE FUNCTION GET_BOOK_AVAILABILITY(p_book_id IN NUMBER)
RETURN NUMBER
IS
    v_available_count NUMBER := 0;
BEGIN
    SELECT COUNT(*)
    INTO v_available_count
    FROM BOOK_COPIES
    WHERE book_id = p_book_id AND status = 'AVAILABLE';
    
    RETURN v_available_count;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

-- ----------------------------------------------------------------------------
-- 2. FUNCTION: CALCULATE_FINE
-- Computes fine amount based on due date, return date, and daily fine rate
-- ----------------------------------------------------------------------------
-- Oracle PL/SQL Syntax:
CREATE OR REPLACE FUNCTION CALCULATE_FINE(
    p_due_date IN DATE,
    p_return_date IN DATE,
    p_rate_per_day IN NUMBER DEFAULT 5.0
)
RETURN NUMBER
IS
    v_overdue_days NUMBER := 0;
    v_fine_amount NUMBER := 0.0;
BEGIN
    IF p_return_date > p_due_date THEN
        v_overdue_days := TRUNC(p_return_date) - TRUNC(p_due_date);
        v_fine_amount := v_overdue_days * p_rate_per_day;
    ELSE
        v_fine_amount := 0.0;
    END IF;
    
    RETURN v_fine_amount;
END;
/

-- ----------------------------------------------------------------------------
-- 3. PROCEDURE: ISSUE_BOOK
-- Validates eligibility, checks availability, selects physical copy, 
-- creates issue record, updates copy status, and records audit trail.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE ISSUE_BOOK(
    p_student_id   IN  NUMBER,
    p_book_id      IN  NUMBER,
    p_librarian_id IN  NUMBER,
    p_issue_id     OUT NUMBER,
    p_status_msg   OUT VARCHAR2
)
IS
    v_active_issues   NUMBER := 0;
    v_unpaid_fines    NUMBER := 0;
    v_copy_id         NUMBER := NULL;
    v_due_date        DATE;
    v_max_books       NUMBER := 3;
    v_loan_days       NUMBER := 14;
    v_student_exists  NUMBER := 0;
    v_has_overdue     NUMBER := 0;
BEGIN
    -- Step 1: Verify Student Exists and Active
    SELECT COUNT(*) INTO v_student_exists 
    FROM STUDENTS s 
    JOIN USERS u ON s.user_id = u.user_id 
    WHERE s.student_id = p_student_id AND u.status = 'ACTIVE';

    IF v_student_exists = 0 THEN
        p_issue_id := -1;
        p_status_msg := 'ERROR: Student does not exist or is inactive.';
        RETURN;
    END IF;

    -- Step 2: Check Student Unpaid Fines
    SELECT COALESCE(SUM(amount), 0) INTO v_unpaid_fines
    FROM FINES
    WHERE student_id = p_student_id AND payment_status = 'UNPAID';

    IF v_unpaid_fines > 0 THEN
        p_issue_id := -2;
        p_status_msg := 'ERROR: Student has unpaid fines of INR ' || TO_CHAR(v_unpaid_fines) || '. Clear fines before borrowing.';
        RETURN;
    END IF;

    -- Step 3: Check Current Active Loans & Overdue Books
    SELECT COUNT(*) INTO v_active_issues
    FROM ISSUES
    WHERE student_id = p_student_id AND status IN ('ISSUED', 'OVERDUE');

    IF v_active_issues >= v_max_books THEN
        p_issue_id := -3;
        p_status_msg := 'ERROR: Student has reached maximum borrowing limit (' || TO_CHAR(v_max_books) || ' books).';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_has_overdue
    FROM ISSUES
    WHERE student_id = p_student_id AND status = 'ISSUED' AND due_date < SYSDATE;

    IF v_has_overdue > 0 THEN
        p_issue_id := -4;
        p_status_msg := 'ERROR: Student has overdue books. Cannot issue new books.';
        RETURN;
    END IF;

    -- Step 4: Find an Available Physical Copy
    BEGIN
        SELECT copy_id INTO v_copy_id
        FROM BOOK_COPIES
        WHERE book_id = p_book_id AND status = 'AVAILABLE'
        ORDER BY copy_id
        FETCH FIRST 1 ROWS ONLY;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_copy_id := NULL;
    END;

    IF v_copy_id IS NULL THEN
        p_issue_id := -5;
        p_status_msg := 'ERROR: No physical copies currently available. You may place a reservation.';
        RETURN;
    END IF;

    -- Step 5: Execute Issue Transaction
    v_due_date := TRUNC(SYSDATE) + v_loan_days;

    INSERT INTO ISSUES (student_id, copy_id, issue_date, due_date, return_date, status)
    VALUES (p_student_id, v_copy_id, TRUNC(SYSDATE), v_due_date, NULL, 'ISSUED')
    RETURNING issue_id INTO p_issue_id;

    -- Update Physical Copy status
    UPDATE BOOK_COPIES
    SET status = 'ISSUED'
    WHERE copy_id = v_copy_id;

    -- Audit Log
    INSERT INTO AUDIT_LOG (user_id, action, action_date)
    VALUES (p_librarian_id, 'Issued Book ID ' || p_book_id || ' (Copy ' || v_copy_id || ') to Student ' || p_student_id, SYSTIMESTAMP);

    COMMIT;
    p_status_msg := 'SUCCESS: Book issued successfully. Due date: ' || TO_CHAR(v_due_date, 'YYYY-MM-DD');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_issue_id := -99;
        p_status_msg := 'TRANSACTION FAILED: ' || SQLERRM;
END;
/

-- ----------------------------------------------------------------------------
-- 4. PROCEDURE: RETURN_BOOK
-- Records return date, calculates overdue days, calculates fine, 
-- updates copy status to AVAILABLE, and handles reservations.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE RETURN_BOOK(
    p_issue_id     IN  NUMBER,
    p_librarian_id IN  NUMBER,
    p_fine_amount  OUT NUMBER,
    p_status_msg   OUT VARCHAR2
)
IS
    v_copy_id         NUMBER;
    v_book_id         NUMBER;
    v_student_id      NUMBER;
    v_due_date        DATE;
    v_current_status  VARCHAR2(20);
    v_rate_per_day    NUMBER := 5.0;
    v_fine            NUMBER := 0.0;
    v_res_count       NUMBER := 0;
BEGIN
    -- Verify Issue record exists and is active
    SELECT copy_id, student_id, due_date, status
    INTO v_copy_id, v_student_id, v_due_date, v_current_status
    FROM ISSUES
    WHERE issue_id = p_issue_id;

    IF v_current_status = 'RETURNED' THEN
        p_fine_amount := 0;
        p_status_msg := 'NOTICE: Book has already been returned.';
        RETURN;
    END IF;

    -- Calculate Fine if Overdue
    v_fine := CALCULATE_FINE(v_due_date, TRUNC(SYSDATE), v_rate_per_day);
    p_fine_amount := v_fine;

    -- Update Issue record
    UPDATE ISSUES
    SET return_date = TRUNC(SYSDATE),
        status = 'RETURNED'
    WHERE issue_id = p_issue_id;

    -- Update Copy status
    UPDATE BOOK_COPIES
    SET status = 'AVAILABLE'
    WHERE copy_id = v_copy_id;

    -- Insert Fine record if overdue
    IF v_fine > 0 THEN
        INSERT INTO FINES (issue_id, student_id, amount, payment_status, payment_date)
        VALUES (p_issue_id, v_student_id, v_fine, 'UNPAID', NULL);
    END IF;

    -- Get book_id for reservation check
    SELECT book_id INTO v_book_id FROM BOOK_COPIES WHERE copy_id = v_copy_id;

    -- Check if there are pending reservations for this book
    SELECT COUNT(*) INTO v_res_count
    FROM RESERVATIONS
    WHERE book_id = v_book_id AND status = 'PENDING';

    -- Log to Audit Trail
    INSERT INTO AUDIT_LOG (user_id, action, action_date)
    VALUES (p_librarian_id, 'Processed return for Issue ID ' || p_issue_id || '. Fine: INR ' || TO_CHAR(v_fine), SYSTIMESTAMP);

    COMMIT;
    IF v_res_count > 0 THEN
        p_status_msg := 'SUCCESS: Book returned. Fine: INR ' || TO_CHAR(v_fine) || '. NOTE: There are pending reservations for this title!';
    ELSE
        p_status_msg := 'SUCCESS: Book returned successfully. Fine: INR ' || TO_CHAR(v_fine);
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_fine_amount := 0;
        p_status_msg := 'ERROR: Issue record not found.';
    WHEN OTHERS THEN
        ROLLBACK;
        p_fine_amount := 0;
        p_status_msg := 'TRANSACTION FAILED: ' || SQLERRM;
END;
/

-- ----------------------------------------------------------------------------
-- 5. PROCEDURE: RESERVE_BOOK
-- Places a reservation for a student when all copies are borrowed
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE RESERVE_BOOK(
    p_student_id     IN  NUMBER,
    p_book_id        IN  NUMBER,
    p_reservation_id OUT NUMBER,
    p_status_msg     OUT VARCHAR2
)
IS
    v_available_copies NUMBER := 0;
    v_existing_res     NUMBER := 0;
    v_next_queue       NUMBER := 1;
BEGIN
    -- Check if physical copies are already available
    v_available_copies := GET_BOOK_AVAILABILITY(p_book_id);
    IF v_available_copies > 0 THEN
        p_reservation_id := -1;
        p_status_msg := 'NOTICE: Book is currently available on shelf! Please visit the counter to borrow directly.';
        RETURN;
    END IF;

    -- Check if student already has an active reservation for this book
    SELECT COUNT(*) INTO v_existing_res
    FROM RESERVATIONS
    WHERE student_id = p_student_id AND book_id = p_book_id AND status IN ('PENDING', 'APPROVED');

    IF v_existing_res > 0 THEN
        p_reservation_id := -2;
        p_status_msg := 'ERROR: You already have an active reservation for this book.';
        RETURN;
    END IF;

    -- Calculate next queue number
    SELECT COALESCE(MAX(queue_number), 0) + 1 INTO v_next_queue
    FROM RESERVATIONS
    WHERE book_id = p_book_id AND status = 'PENDING';

    -- Insert reservation
    INSERT INTO RESERVATIONS (student_id, book_id, reservation_date, status, queue_number)
    VALUES (p_student_id, p_book_id, SYSTIMESTAMP, 'PENDING', v_next_queue)
    RETURNING reservation_id INTO p_reservation_id;

    COMMIT;
    p_status_msg := 'SUCCESS: Book reserved. Your queue position is #' || TO_CHAR(v_next_queue);

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_reservation_id := -99;
        p_status_msg := 'RESERVATION FAILED: ' || SQLERRM;
END;
/

-- ----------------------------------------------------------------------------
-- 6. TRIGGERS
-- ----------------------------------------------------------------------------

-- Trigger 1: Automatically log new student creation in AUDIT_LOG
CREATE OR REPLACE TRIGGER TRG_AUDIT_STUDENT_REGISTER
AFTER INSERT ON STUDENTS
FOR EACH ROW
BEGIN
    INSERT INTO AUDIT_LOG (user_id, action, action_date)
    VALUES (:NEW.user_id, 'New student registered with Reg No: ' || :NEW.register_number, SYSTIMESTAMP);
END;
/

-- Trigger 2: Prevent issuing book copies that are marked as LOST or MAINTENANCE
CREATE OR REPLACE TRIGGER TRG_CHECK_COPY_AVAILABLE
BEFORE INSERT ON ISSUES
FOR EACH ROW
DECLARE
    v_copy_status VARCHAR2(20);
BEGIN
    SELECT status INTO v_copy_status
    FROM BOOK_COPIES
    WHERE copy_id = :NEW.copy_id;

    IF v_copy_status NOT IN ('AVAILABLE', 'RESERVED') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Cannot issue copy: current status is ' || v_copy_status);
    END IF;
END;
/

-- Trigger 3: Prevent duplicate active reservation for same student and book
CREATE OR REPLACE TRIGGER TRG_PREVENT_DUP_RESERVATION
BEFORE INSERT ON RESERVATIONS
FOR EACH ROW
DECLARE
    v_active_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_active_count
    FROM RESERVATIONS
    WHERE student_id = :NEW.student_id 
      AND book_id = :NEW.book_id 
      AND status IN ('PENDING', 'APPROVED');

    IF v_active_count > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Student already has an active reservation for this book.');
    END IF;
END;
/
