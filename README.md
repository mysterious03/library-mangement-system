# Smart Library Management System (SLMS)

> **College Semester Project — 4-Member Team Architecture**  
> **Strict Technology Constraint:** Built exclusively with **HTML5, CSS3, Java SE 17, JDBC, and SQL / PL-SQL**.  
> **Zero External Frameworks:** Zero React, Node, Python, PHP, Angular, Vue, TypeScript, Tailwind, or MongoDB.

---

## 1. Project Overview & Purpose

The **Smart Library Management System** is a complete digital campus library solution engineered for college academic evaluation. It allows **Students**, **Librarians**, and **Administrators** to manage books, physical accessions, borrowing, returns, waiting reservations, overdue fines, and SQL analytical reports.

Unlike a basic CRUD project, SLMS demonstrates:
* **Object-Oriented Programming (OOP):** Encapsulation, inheritance (`Student` & `Librarian` subclassing `User`), and clean service-DAO decoupling.
* **JDBC Best Practices:** `PreparedStatement`, `ResultSet`, connection lifecycle management, and explicit ACID transaction demarcations (`setAutoCommit(false)`, `commit()`, `rollback()`).
* **Database Normalization (3NF):** Relational integrity across 12 normalized tables with Primary Keys, Foreign Keys, `ON DELETE CASCADE / SET NULL`, `CHECK`, and `UNIQUE` constraints.
* **PL/SQL Stored Routines & Triggers:** Procedures for `ISSUE_BOOK`, `RETURN_BOOK`, `RESERVE_BOOK`, functions for `CALCULATE_FINE` and `GET_BOOK_AVAILABILITY`, and triggers for inventory status synchronization and security audit logging.
* **Complex Analytical SQL:** 10 SQL reports utilizing `JOIN`, `GROUP BY`, `HAVING`, `ORDER BY`, `COUNT`, `SUM`, `AVG`, and subqueries.
* **Role-Based Access Control (RBAC):** Strict security boundaries, session tracking with HTTP-only cookies, and SHA-256 password hashing.

---

## 2. 4-Member Project Division & Contributions

This project is partitioned into 4 distinct, cohesive modules suitable for a 4-member semester team:

| Team Member | Module & Responsibilities | Key Source Files & Deliverables |
| :--- | :--- | :--- |
| **Member 1** | **Authentication & Student Portal**<br>• User login, logout & session lifecycle<br>• Student self-registration with duplicate checks<br>• Student dashboard, profile & borrowing history<br>• Role-Based Access Control (RBAC) security | `src/model/User.java`<br>`src/model/Student.java`<br>`src/dao/UserDAO.java`<br>`src/dao/StudentDAO.java`<br>`src/service/AuthService.java`<br>`src/controller/PublicController.java`<br>`src/controller/StudentController.java`<br>`src/util/PasswordUtils.java` |
| **Member 2** | **Book Catalog & Librarian Circulation**<br>• Book metadata & copy accession management<br>• Catalog search with multi-parameter filters<br>• Transactional book issuing with eligibility validation<br>• Transactional book returns & inventory restock | `src/model/Book.java`<br>`src/model/BookCopy.java`<br>`src/model/Issue.java`<br>`src/dao/BookDAO.java`<br>`src/dao/IssueDAO.java`<br>`src/service/BookService.java`<br>`src/service/IssueService.java`<br>`src/controller/LibrarianController.java` |
| **Member 3** | **Database, SQL/PL-SQL & JDBC Architecture**<br>• 12 Normalized relational tables (3NF)<br>• PL/SQL Stored Procedures, Functions & Triggers<br>• JDBC connection pooling & transaction handling<br>• Admin user management & security audit trail | `sql/schema.sql`<br>`sql/plsql_procedures.sql`<br>`sql/sample_data.sql`<br>`src/database/DatabaseConnection.java`<br>`src/database/DatabaseInitializer.java`<br>`src/model/AuditLog.java`<br>`src/dao/AuditDAO.java`<br>`src/controller/AdminController.java` |
| **Member 4** | **Reservations, Fines, SQL Reports & Testing**<br>• Out-of-stock reservation queue management<br>• Overdue fine calculation (₹5/day) & clearance<br>• 10 SQL analytical reporting queries<br>• System test suites, validation & documentation | `src/model/Reservation.java`<br>`src/model/Fine.java`<br>`sql/reports.sql`<br>`src/dao/ReservationDAO.java`<br>`src/dao/FineDAO.java`<br>`src/dao/ReportDAO.java`<br>`src/service/ReservationService.java`<br>`src/service/FineService.java`<br>`src/service/ReportService.java`<br>`src/controller/ReportController.java` |

---

## 3. Technology Stack Compliance

| Layer | Permitted Technology | Implementation Details |
| :--- | :--- | :--- |
| **Frontend UI** | **HTML5 + CSS3** | Semantic HTML5, responsive CSS grid/flexbox, custom design tokens, zero JavaScript frameworks or Tailwind. |
| **Application Logic** | **Java SE 17** | Pure Java OOP architecture, `com.sun.net.httpserver.HttpServer` standard library web server. Zero Spring/Node. |
| **Database Connectivity**| **JDBC** | `PreparedStatement`, `ResultSet`, connection manager, explicit ACID transaction management. |
| **Database Engine** | **SQL / PL-SQL** | Pre-configured with pure-Java embedded relational engine for zero-setup execution, plus full Oracle / MySQL scripts in `sql/`. |

---

## 4. Default Demo Accounts

The database comes pre-seeded with **32 books**, **75+ physical copies**, **12 students**, **2 librarians**, **1 administrator**, active loans, overdue fines, and reservation queues:

| Role | Email Address | Password | Sample User |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@library.edu` | `admin123` | Dr. Rajesh Sharma (Full system & user audit privileges) |
| **Librarian** | `librarian@library.edu` | `librarian123` | Mrs. Ananya Sen (Circulation, issue, return & fines counter) |
| **Student** | `aarav.patel@student.edu` | `student123` | Aarav Patel (`21CS001` - Computer Science, 3rd Year) |
| **Student** | `aditya.c@student.edu` | `student123` | Aditya Choudhury (`23CS077` - Overdue loan demo account) |

---

## 5. Quick Start & Execution Guide

### Option A: Using Windows Batch Script (Recommended)
1. Double-click `run.bat` or open PowerShell in the project directory:
   ```powershell
   .\run.bat
   ```
2. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

### Option B: Using Linux / macOS Shell Script
```bash
chmod +x build.sh run.sh
./run.sh
```

### Option C: Manual Command-Line Compilation & Launch
```bash
# Compile source files to bin/
javac -encoding UTF-8 -cp "lib/h2.jar;lib/mysql-connector-j.jar;src" -d bin src/model/*.java src/dao/*.java src/service/*.java src/util/*.java src/controller/*.java src/database/*.java src/Main.java

# Launch Application
java -cp "bin;lib/h2.jar;lib/mysql-connector-j.jar" Main 8080
```

---

## 6. Complete 17-Step Project Demonstration Flow

Follow this exact walkthrough to demonstrate all requirements to professors/evaluators:

1. **Student Registration:**
   * Go to `http://localhost:8080/register`.
   * Register a new student: Name `Ravi Kumar`, Email `ravi@student.edu`, Password `password123`, Reg No `24CS999`, Dept `Computer Science`, Year `2`, Section `A`.
   * Notice instant validation, duplicate check prevention, and password hashing.
2. **Student Login:**
   * Go to `http://localhost:8080/login` and sign in with `ravi@student.edu` / `password123`.
   * Lands on the customized **Student Dashboard**.
3. **Search for Books:**
   * Click **Search Books** in the sidebar.
   * Search query `"Operating"` or filter by Category `"Computer Science"`.
   * Real-time availability indicator shows available vs. total physical copies on shelves.
4. **View Book Details:**
   * Click on any title (e.g., *Modern Operating Systems*) to view ISBN, edition, publisher, language, synopsis, and shelf rack.
5. **Reserve Out-of-Stock Book:**
   * Search for *Designing Data-Intensive Applications*.
   * Because all copies are issued, click **Reserve Book**.
   * System queues the student and assigns an incremental queue position (e.g. `#4`).
   * Attempting to reserve again displays a duplicate prevention warning.
6. **Student Fines & Borrowed Books:**
   * Check **My Borrowed Books**, **My Fines**, and **My Reservations** to verify accurate personal statements.
   * Click **Sign Out**.
7. **Librarian Login:**
   * Sign in with `librarian@library.edu` / `librarian123`.
   * Lands on the **Librarian Dashboard** showing circulation metrics, overdue counts, and active loans.
8. **Issue a Book (Eligibility Checks):**
   * Go to **Issue Book**.
   * Select student `Aarav Patel (21CS001)` and select book *Introduction to Algorithms (CLRS)*.
   * Click **Execute Issue Transaction**.
   * System checks: (a) Student active status, (b) borrowing quota (< 3 books), (c) zero overdue loans, (d) zero unpaid fines, (e) selects available copy `ACC-00201`, marks copy as `ISSUED`, creates issue record with 14-day due date, and records audit entry.
9. **Verify Reduced Inventory:**
   * View **Manage Books**; notice available copy count automatically decremented.
10. **Process Book Return (Automatic Overdue Fine Calculation):**
    * Go to **Return Book**.
    * Select overdue issue `#17` (*Ishaan Gupta*, 8 days overdue).
    * Click **Process Return**.
    * System records today's return date, calculates late days (8 days), computes fine: `8 × ₹5 = ₹40.00`, updates copy to `AVAILABLE`, inserts unpaid fine record into `FINES` table, and logs audit trail.
11. **Collect Fine Payment:**
    * Go to **Fine Management**.
    * Locate the fine of `INR 40.00` for Ishaan Gupta.
    * Click **Mark as Paid**.
    * Receipt status updates to `PAID` with today's payment timestamp.
12. **Manage Book Copies:**
    * Go to **Physical Book Copies**.
    * Add a new physical copy accession `ACC-00109` to shelf rack `Shelf CS-A1`.
13. **Librarian Analytical Reports:**
    * Go to **Analytical Reports**. Switch between reports 1 through 10.
    * Click **Sign Out**.
14. **Administrator Login:**
    * Sign in with `admin@library.edu` / `admin123`.
    * Lands on **Admin Dashboard** displaying total titles, enrolled students, active circulation loans, overdue violations, and fine collections.
15. **User Management:**
    * Go to **Manage Users**.
    * Toggle user account status between `ACTIVE` and `SUSPENDED`.
16. **Staff Librarian Creation:**
    * Go to **Manage Librarians**.
    * Create a new staff account (Employee ID: `LIB-2026-10`).
17. **Demonstrate PL/SQL & Security Audit Trail:**
    * Go to **Security Audit Logs** to show the live audit trail of every login, registration, issue, return, and payment!
    * Open `sql/plsql_procedures.sql` to demonstrate procedures `ISSUE_BOOK`, `RETURN_BOOK`, function `CALCULATE_FINE`, and triggers to the professor!

---

## 7. Database Architecture & Schema Summary

The relational database is normalized to **Third Normal Form (3NF)**:

```
USERS (user_id PK, name, email UNIQUE, password, phone, role, status, created_at)
  ├── STUDENTS (student_id PK, user_id FK, register_number UNIQUE, department, year, section)
  └── LIBRARIANS (librarian_id PK, user_id FK, employee_id UNIQUE)

AUTHORS (author_id PK, author_name, biography)
CATEGORIES (category_id PK, category_name UNIQUE, description)
PUBLISHERS (publisher_id PK, publisher_name, address, contact)

BOOKS (book_id PK, isbn UNIQUE, title, author_id FK, category_id FK, publisher_id FK, ...)
  └── BOOK_COPIES (copy_id PK, book_id FK, accession_number UNIQUE, shelf_location, status)
        └── ISSUES (issue_id PK, student_id FK, copy_id FK, issue_date, due_date, return_date, status)
              └── FINES (fine_id PK, issue_id FK, student_id FK, amount, payment_status, payment_date)

RESERVATIONS (reservation_id PK, student_id FK, book_id FK, reservation_date, status, queue_number)
AUDIT_LOG (log_id PK, user_id FK, action, action_date)
```

---

## 8. Directory Structure

```
adil/
├── src/
│   ├── model/                  # Encapsulated OOP Domain Entities
│   │   ├── User.java           # Base User (Inheritance superclass)
│   │   ├── Student.java        # Subclasses User
│   │   ├── Librarian.java      # Subclasses User
│   │   ├── Book.java
│   │   ├── BookCopy.java
│   │   ├── Author.java
│   │   ├── Category.java
│   │   ├── Publisher.java
│   │   ├── Issue.java
│   │   ├── Reservation.java
│   │   ├── Fine.java
│   │   └── AuditLog.java
│   │
│   ├── dao/                    # JDBC Data Access Objects (PreparedStatements & Transactions)
│   │   ├── UserDAO.java
│   │   ├── StudentDAO.java
│   │   ├── LibrarianDAO.java
│   │   ├── BookDAO.java
│   │   ├── IssueDAO.java
│   │   ├── ReservationDAO.java
│   │   ├── FineDAO.java
│   │   ├── AuthorDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── PublisherDAO.java
│   │   ├── ReportDAO.java
│   │   └── AuditDAO.java
│   │
│   ├── service/                # Business Logic Services
│   │   ├── AuthService.java
│   │   ├── BookService.java
│   │   ├── IssueService.java
│   │   ├── ReservationService.java
│   │   ├── FineService.java
│   │   ├── ReportService.java
│   │   └── UserService.java
│   │
│   ├── controller/             # HTTP Routing, Sessions & HTML Rendering
│   │   ├── WebServer.java      # Pure Java SE HttpServer (port 8080)
│   │   ├── SessionManager.java # In-memory session & flash message manager
│   │   ├── HtmlRenderer.java   # Semantic HTML5 & CSS3 Page Template Engine
│   │   ├── PublicController.java
│   │   ├── StudentController.java
│   │   ├── LibrarianController.java
│   │   ├── AdminController.java
│   │   └── ReportController.java
│   │
│   ├── database/               # JDBC Connection & Initializer
│   │   ├── DatabaseConnection.java
│   │   └── DatabaseInitializer.java
│   │
│   ├── util/                   # Utility Classes
│   │   └── PasswordUtils.java  # SHA-256 Cryptographic Hashing
│   │
│   └── Main.java               # Application Entry Point
│
├── web/
│   └── css/
│       └── style.css           # 100% Pure CSS3 Design System
│
├── sql/
│   ├── schema.sql              # Relational Schema DDL & Views
│   ├── plsql_procedures.sql    # Oracle/MySQL PL/SQL Procedures, Functions & Triggers
│   ├── sample_data.sql         # 32 Books, 12 Students, Copies & Fines Seed Data
│   └── reports.sql             # 10 SQL Analytical Queries
│
├── lib/
│   ├── h2.jar                  # Embedded Pure-Java Relational Database & JDBC Driver
│   └── mysql-connector-j.jar   # MySQL 8.x JDBC Driver (Optional external server)
│
├── build.bat                   # Windows Compilation Script
├── run.bat                     # Windows Launch Script
├── build.sh                    # Linux/macOS Compilation Script
├── run.sh                      # Linux/macOS Launch Script
├── PROJECT_REPORT.md           # Full Semester Project Documentation
└── README.md                   # Project Reference & Setup Guide
```

---

## 9. Academic Project Certification

This project conforms strictly to standard college curriculum requirements:
* **No forbidden frameworks:** Built with zero Node.js, React, Angular, Vue, Python, PHP, or Tailwind.
* **Pure core technologies:** HTML5, CSS3, Java, JDBC, and SQL/PL-SQL.
* **Suitable for 4-member semester defense:** Comprehensive code division, thorough test cases, and complete report included in [`PROJECT_REPORT.md`](file:///c:/Users/ASUS/OneDrive/Desktop/adil/PROJECT_REPORT.md).

---

## 10. Deployment Options

For complete step-by-step instructions across all platforms, see [`DEPLOYMENT_GUIDE.md`](file:///c:/Users/ASUS/OneDrive/Desktop/adil/DEPLOYMENT_GUIDE.md).

Quick options:
- **Free Cloud Deploy (Render.com / Railway):** Push to GitHub and deploy using the included multi-stage [`Dockerfile`](file:///c:/Users/ASUS/OneDrive/Desktop/adil/Dockerfile).
- **Executable JAR:** Double-click [`package.bat`](file:///c:/Users/ASUS/OneDrive/Desktop/adil/package.bat) to create `smart-library.jar`, then run `java -jar smart-library.jar`.
- **College Lab / Wi-Fi LAN:** Run `run.bat` and share your local IP (`http://<your-ip>:8080`) with evaluators.

