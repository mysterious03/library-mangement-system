# SMART LIBRARY MANAGEMENT SYSTEM (SLMS)
## Comprehensive College Semester Project Documentation & Technical Report

---

### **PROJECT DETAILS**
* **Project Title:** Smart Library Management System (SLMS)
* **Academic Level:** Undergraduate / Graduate Degree Semester Project
* **Team Structure:** 4-Member Collaborative Engineering Team
* **Primary Technologies:** HTML5, CSS3, Java SE 17, JDBC, SQL / PL-SQL
* **Architecture:** Model-View-Controller (MVC) with Data Access Object (DAO) Pattern

---

## 1. ABSTRACT

Modern educational institutions require efficient, reliable, and auditable digital library systems capable of managing book cataloging, physical accession tracking, borrower quotas, overdue fines, and analytical reporting. The **Smart Library Management System (SLMS)** is engineered to meet these requirements while demonstrating rigorous database normalization, stored database procedural logic, clean object-oriented programming (OOP), and transaction-safe Java Database Connectivity (JDBC).

Developed under strict technology constraints prohibiting high-level abstractions or JavaScript web frameworks, SLMS uses **HTML5** for structure, **CSS3** for an advanced responsive interface, **Java SE 17** for all domain rules and controller routing, **JDBC** for persistence, and **SQL / PL-SQL** for transactional routines and database triggers. The system enforces strict Role-Based Access Control (RBAC) across three distinct actors: **Students**, **Librarians**, and **Administrators**.

---

## 2. 4-MEMBER TEAM CONTRIBUTION MATRIX

To ensure equal academic contribution and clear modular separation, the system architecture is partitioned across four specialized domains:

```
┌────────────────────────────────────────────────────────────────────────┐
│                   SMART LIBRARY MANAGEMENT SYSTEM                      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
    ┌──────────────────┬────────────┴───────┬──────────────────┐
    ▼                  ▼                    ▼                  ▼
┌──────────────┐ ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   MEMBER 1   │ │   MEMBER 2   │    │   MEMBER 3   │    │   MEMBER 4   │
│Authentication│ │Book Catalog &│    │Database, JDBC│    │Reservations, │
│& Student App │ │Circulation   │    │& PL/SQL Core │    │Fines & Reports
└──────────────┘ └──────────────┘    └──────────────┘    └──────────────┘
```

### **Member 1: Authentication & Student Portal Module**
* **Responsibilities:**
  * Implementation of base user model (`User.java`) and polymorphic student specialization (`Student.java`).
  * Cryptographic hashing of passwords using SHA-256 (`PasswordUtils.java`).
  * User authentication, session management, and HTTP cookie lifecycle (`SessionManager.java`).
  * Student self-registration with duplicate prevention checks (`StudentDAO.java`).
  * Student portal interfaces: Dashboard, personal loans list, reservation status, and fine receipts.
  * Role-Based Access Control (RBAC) authorization guards.

### **Member 2: Book Catalog & Circulation Desk Module**
* **Responsibilities:**
  * Domain modeling of `Book.java`, `BookCopy.java`, and `Issue.java`.
  * Multi-attribute catalog search engine with title, author, category, ISBN, and availability filtering (`BookDAO.java`).
  * Physical copy accession tracking with shelf rack location mapping.
  * Transaction-safe book issuing with borrower quota validation, unpaid fine checks, and overdue debt verification (`IssueDAO.java`).
  * Transaction-safe book returns, restock status synchronization, and inventory recalculation.
  * Librarian management dashboards and circulation desk views.

### **Member 3: Database Architecture, PL/SQL & JDBC Core Module**
* **Responsibilities:**
  * Design of 12 normalized relational database tables conforming to 3NF standards.
  * Formulation of PL/SQL stored procedures (`ISSUE_BOOK`, `RETURN_BOOK`, `RESERVE_BOOK`).
  * Formulation of PL/SQL functions (`CALCULATE_FINE`, `GET_BOOK_AVAILABILITY`).
  * Implementation of database triggers for inventory state enforcement and audit trail logging.
  * JDBC connection factory with explicit ACID transaction boundaries (`DatabaseConnection.java`).
  * Automated database schema creation and sample seed script execution on startup (`DatabaseInitializer.java`).
  * Administrator console, staff librarian onboarding, and security audit trail.

### **Member 4: Reservations, Fines, Analytical Reports & Testing Module**
* **Responsibilities:**
  * Out-of-stock reservation queue management with FIFO queue ordering (`ReservationDAO.java`).
  * Overdue fine calculation logic (₹5.00/day) and payment status reconciliation (`FineDAO.java`).
  * Formulation and execution of 10 complex SQL analytical reports leveraging `JOIN`, `GROUP BY`, `HAVING`, and subqueries (`ReportDAO.java`).
  * Analytical reporting interface with dynamic HTML table generation.
  * Comprehensive test suite execution, validation scenarios, and edge-case verification.

---

## 3. RELATIONAL DATABASE DESIGN & NORMALIZATION

The database schema eliminates update anomalies, insertion anomalies, and deletion anomalies through formal normalization.

### **Normalization Walkthrough**

1. **First Normal Form (1NF):**
   * All column values are atomic (e.g., student name, phone numbers, and addresses contain single values).
   * Repeating groups are completely avoided; physical book copies are modeled in a separate table (`BOOK_COPIES`) rather than stored as an array or comma-separated list in `BOOKS`.
   * Each table possesses a designated Primary Key.

2. **Second Normal Form (2NF):**
   * The schema satisfies 1NF.
   * All non-key attributes are fully functionally dependent on the entire primary key (no partial dependencies on composite keys).
   * For instance, `ISSUES` references `copy_id` and `student_id` through artificial surrogate primary keys (`issue_id`), ensuring single-attribute key dependencies.

3. **Third Normal Form (3NF):**
   * The schema satisfies 2NF.
   * Every non-prime attribute is non-transitively dependent on every candidate key (no transitive dependencies).
   * Author names and biographies are extracted into `AUTHORS` (referenced via `author_id` in `BOOKS`), preventing book records from redundantly duplicating author bios.
   * Category descriptions are separated into `CATEGORIES`, and publisher information into `PUBLISHERS`.

---

## 4. RELATIONAL DATA DICTIONARY

The database comprises 12 interconnected tables:

| Table Name | Primary Key | Foreign Keys | Key Constraints & Purpose |
| :--- | :--- | :--- | :--- |
| **`SYSTEM_SETTINGS`** | `setting_key` | None | Stores global configurable parameters (daily fine rate, loan days limit). |
| **`USERS`** | `user_id` | None | Master authentication table; `email UNIQUE`, `CHECK (role IN ('STUDENT','LIBRARIAN','ADMIN'))`. |
| **`STUDENTS`** | `student_id` | `user_id` &rarr; `USERS` | Student extension; `register_number UNIQUE`, `CHECK (year BETWEEN 1 AND 5)`. |
| **`LIBRARIANS`** | `librarian_id` | `user_id` &rarr; `USERS` | Staff extension; `employee_id UNIQUE`. |
| **`AUTHORS`** | `author_id` | None | Author catalog information. |
| **`CATEGORIES`** | `category_id` | None | Subject classification; `category_name UNIQUE`. |
| **`PUBLISHERS`** | `publisher_id` | None | Publisher contact and address registry. |
| **`BOOKS`** | `book_id` | `author_id`, `category_id`, `publisher_id` | Master title catalog; `isbn UNIQUE`. |
| **`BOOK_COPIES`** | `copy_id` | `book_id` &rarr; `BOOKS` | Individual physical inventory units; `accession_number UNIQUE`, `CHECK (status)`. |
| **`ISSUES`** | `issue_id` | `student_id`, `copy_id` | Circulation loan transactions with loan dates and return dates. |
| **`RESERVATIONS`**| `reservation_id` | `student_id`, `book_id` | Out-of-stock queue registrations; `queue_number` ordering. |
| **`FINES`** | `fine_id` | `issue_id`, `student_id` | Overdue fee assessments; `CHECK (amount >= 0)`, `CHECK (payment_status)`. |
| **`AUDIT_LOG`** | `log_id` | `user_id` &rarr; `USERS` | Immutable system security and transaction audit trail. |

---

## 5. PL/SQL STORED PROCEDURES & TRIGGERS SPECIFICATION

The project provides complete PL/SQL scripts compatible with Oracle PL/SQL and MySQL stored routines in [`sql/plsql_procedures.sql`](file:///c:/Users/ASUS/OneDrive/Desktop/adil/sql/plsql_procedures.sql):

### **1. Procedure: `ISSUE_BOOK`**
* **Signature:** `(p_student_id, p_book_id, p_librarian_id, OUT p_issue_id, OUT p_status_msg)`
* **Logic:**
  1. Verifies student exists and account is `ACTIVE`.
  2. Queries `FINES` to verify zero unpaid dues (`unpaid_fines = 0`).
  3. Queries `ISSUES` to ensure active loans do not exceed 3 books.
  4. Queries `ISSUES` to ensure student holds no overdue books.
  5. Selects the first physical copy where `status = 'AVAILABLE'`.
  6. Inserts loan transaction into `ISSUES` with `due_date = SYSDATE + 14`.
  7. Updates physical copy `status = 'ISSUED'`.
  8. Automatically fulfills pending reservation if present.
  9. Writes transaction into `AUDIT_LOG` and commits. Rolls back on error.

### **2. Procedure: `RETURN_BOOK`**
* **Signature:** `(p_issue_id, p_librarian_id, OUT p_fine_amount, OUT p_status_msg)`
* **Logic:**
  1. Validates that issue record is not already marked `RETURNED`.
  2. Calculates days elapsed past due date: `overdue_days = MAX(0, return_date - due_date)`.
  3. Computes fine: `overdue_days * 5.0`.
  4. Sets `return_date = SYSDATE` and `status = 'RETURNED'`.
  5. Updates physical copy `status = 'AVAILABLE'`.
  6. If fine > 0, inserts record into `FINES` with status `UNPAID`.
  7. Checks if students are waiting in `RESERVATIONS` queue.
  8. Logs action to `AUDIT_LOG` and commits transaction.

### **3. Function: `CALCULATE_FINE`**
* **Signature:** `(p_due_date, p_return_date, p_rate_per_day) RETURN NUMBER`
* **Logic:** Returns `MAX(0, TRUNC(p_return_date) - TRUNC(p_due_date)) * p_rate_per_day`.

### **4. Procedure: `RESERVE_BOOK`**
* **Signature:** `(p_student_id, p_book_id, OUT p_reservation_id, OUT p_status_msg)`
* **Logic:**
  1. Calls `GET_BOOK_AVAILABILITY`; rejects reservation if copies are currently available on shelf.
  2. Checks for existing active reservation by same student to avoid duplicates.
  3. Computes next queue position: `COALESCE(MAX(queue_number), 0) + 1`.
  4. Inserts into `RESERVATIONS` with status `PENDING`.

### **5. Database Triggers**
* `TRG_AUDIT_STUDENT_REGISTER`: Automatically logs every new student account into `AUDIT_LOG`.
* `TRG_CHECK_COPY_AVAILABLE`: Prevents issuing book copies marked as `LOST` or `MAINTENANCE`.
* `TRG_PREVENT_DUP_RESERVATION`: Database-level constraint preventing duplicate active reservations.

---

## 6. THE 10 ADVANCED SQL ANALYTICAL REPORTS

The project includes 10 complex analytical queries demonstrating mastery of relational data processing in [`sql/reports.sql`](file:///c:/Users/ASUS/OneDrive/Desktop/adil/sql/reports.sql):

1. **Most Borrowed Books:** Identifies the top 10 most popular titles using `INNER JOIN`, `GROUP BY`, `COUNT(i.issue_id)`, and `COUNT(DISTINCT i.student_id)`.
2. **Most Active Students:** Analyzes circulation activity per student using multiple joins, `GROUP BY`, `HAVING COUNT(i.issue_id) > 0`, and conditional sums.
3. **Overdue Books with Calculated Fines:** Computes late fees dynamically for active loans exceeding due dates using `DATEDIFF` and student contact joins.
4. **Students with Unpaid Fines:** Summarizes financial liabilities per student using `GROUP BY`, `SUM(f.amount)`, and `HAVING SUM(f.amount) > 0`.
5. **Books Distribution by Category:** Aggregates titles, total physical copies, available copies, and issued copies across all catalog categories.
6. **Available Books Report (Shelf Audit):** Displays inventory available for immediate browsing sorted by physical shelf location.
7. **Currently Issued Books Status:** Real-time circulation manifest showing borrowers, return due dates, and loan states.
8. **Reservation Statistics and Queues:** Tracks reservation demand, active queue lengths, completed loans, and cancellations per title.
9. **Monthly Borrowing Trends:** Measures seasonal circulation spikes by aggregating issues by `YEAR(issue_date)` and `MONTH(issue_date)`.
10. **Fine Collection Statistics:** Financial reconciliation analyzing total assessed fines, collected revenue, average penalty, and pending dues.

---

## 7. SYSTEM TEST CASES & VERIFICATION MATRIX

| Test ID | Test Scenario | Expected Outcome | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Student self-registration with valid credentials | User and Student records inserted transactionally; audit log created | Account created; redirected to sign in | **PASS** |
| **TC-02** | Student registration with duplicate Register Number | Transaction rolls back; friendly error displayed | Rejected: Register Number already exists | **PASS** |
| **TC-03** | Authentication with invalid password | Access denied; generic error message shown | Access denied | **PASS** |
| **TC-04** | Role-based unauthorized URL access (Student &rarr; `/admin/dashboard`) | Redirected to `/student/dashboard` | Unauthorized redirect enforced | **PASS** |
| **TC-05** | Issuing book to student with &ge; 3 active loans | Transaction blocked: loan quota exceeded | Blocked with quota notice | **PASS** |
| **TC-06** | Issuing book to student with unpaid fines | Transaction blocked: unpaid fine clearance required | Blocked: student has unpaid fine | **PASS** |
| **TC-07** | Issuing book with 0 available physical copies | Transaction blocked; recommends reservation queue | Blocked: No copies available | **PASS** |
| **TC-08** | Return overdue book (e.g. 11 days late) | Calculated fine (`11 × ₹5 = ₹55`) inserted as `UNPAID`; copy marked `AVAILABLE` | Fine created; copy restocked | **PASS** |
| **TC-09** | Reserve out-of-stock book | Queue position assigned incrementally (#1, #2, etc.) | Enqueued with position # | **PASS** |
| **TC-10** | Duplicate reservation by same student | Transaction blocked: already in queue | Blocked duplicate reservation | **PASS** |
| **TC-11** | Fine clearance ("Mark as Paid") | Payment status changed to `PAID`, payment date recorded | Fine cleared with today's date | **PASS** |
| **TC-12** | Live execution of SQL Report 1 (Most Borrowed) | Returns top borrowed titles grouped by count | 10 rows aggregated correctly | **PASS** |

---

## 8. VIVA VOCE QUESTIONS & ANSWERS (EXAMINATION PREPARATION)

**Q1: Why did you use `PreparedStatement` instead of `Statement`?**  
*Answer:* `PreparedStatement` pre-compiles the SQL query structure in the database engine and safely binds parameters via placeholders (`?`). This eliminates SQL Injection attacks completely and improves performance through query execution plan caching.

**Q2: How are database transactions handled in your Java application?**  
*Answer:* Transactions are explicitly demarcated using `connection.setAutoCommit(false)`. Multiple interdependent database updates (e.g., updating physical copy status, creating issue record, and writing audit log) are grouped together. If all statements execute successfully, `connection.commit()` persists the changes. If any exception occurs, `connection.rollback()` restores the database to its previous consistent state.

**Q3: How does the system handle concurrent book issues?**  
*Answer:* The book issue operation runs within a database transaction. When fetching an available copy, the query filters for `status = 'AVAILABLE'` and immediately updates the status to `'ISSUED'`. Database row-level locks prevent two librarians from issuing the exact same physical copy simultaneously.

**Q4: How does the application prevent duplicate ISBNs and Register Numbers?**  
*Answer:* Prevention is implemented at two levels: (1) Application-level validation queries check for existence before executing insertion, and (2) Database-level `UNIQUE` constraints on `BOOKS.isbn` and `STUDENTS.register_number` enforce integrity even in edge-case race conditions.

**Q5: How is Role-Based Access Control (RBAC) enforced without web frameworks?**  
*Answer:* When a user logs in, Java's `SessionManager` generates a cryptographically random UUID token and transmits it to the browser via an `HttpOnly` session cookie. On each subsequent request, the `MasterRouter` extracts the session token, retrieves the associated `User` object, checks the user's role (`STUDENT`, `LIBRARIAN`, `ADMIN`), and rejects or redirects unauthorized route requests before any controller logic executes.

---

## 9. CONCLUSION

The **Smart Library Management System** successfully fulfills all academic and functional requirements. By adhering strictly to core technologies—**HTML5, CSS3, Java, JDBC, and SQL/PL-SQL**—the project demonstrates a solid mastery of software architecture, data modeling, database security, and transaction management suitable for an exemplary college semester project.
