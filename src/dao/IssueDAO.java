package dao;

import database.DatabaseConnection;
import model.Issue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ISSUES circulation operations.
 * Implements full ACID Transactions, Eligibility Rules, and Fine Calculations.
 * Member 2 & 4: Circulation & Fines Management.
 */
public class IssueDAO {

    public static class IssueResult {
        public final boolean success;
        public final String message;
        public final int issueId;

        public IssueResult(boolean success, String message, int issueId) {
            this.success = success;
            this.message = message;
            this.issueId = issueId;
        }
    }

    public static class ReturnResult {
        public final boolean success;
        public final String message;
        public final double fineAmount;
        public final int overdueDays;

        public ReturnResult(boolean success, String message, double fineAmount, int overdueDays) {
            this.success = success;
            this.message = message;
            this.fineAmount = fineAmount;
            this.overdueDays = overdueDays;
        }
    }

    /**
     * Issues a physical book copy to a student in a strict ACID transaction.
     */
    public IssueResult issueBook(int studentId, int bookId, int librarianUserId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Verify Student exists and is ACTIVE
            String studentCheckSql = "SELECT u.status, s.register_number FROM STUDENTS s JOIN USERS u ON s.user_id = u.user_id WHERE s.student_id = ?";
            String regNumber = "";
            try (PreparedStatement ps = conn.prepareStatement(studentCheckSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return new IssueResult(false, "Validation Failed: Student ID not found.", -1);
                    }
                    if (!"ACTIVE".equalsIgnoreCase(rs.getString("status"))) {
                        conn.rollback();
                        return new IssueResult(false, "Access Denied: Student account is suspended or inactive.", -1);
                    }
                    regNumber = rs.getString("register_number");
                }
            }

            // 2. Check for Unpaid Fines
            String fineCheckSql = "SELECT COALESCE(SUM(amount), 0) FROM FINES WHERE student_id = ? AND payment_status = 'UNPAID'";
            try (PreparedStatement ps = conn.prepareStatement(fineCheckSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getDouble(1) > 0) {
                        conn.rollback();
                        return new IssueResult(false, "Borrowing Blocked: Student has unpaid fines of INR " + rs.getDouble(1) + ". Please clear dues first.", -1);
                    }
                }
            }

            // 3. Check Active Borrowing Limit (Max 3 books)
            String activeLoansSql = "SELECT COUNT(*) FROM ISSUES WHERE student_id = ? AND status IN ('ISSUED', 'OVERDUE')";
            try (PreparedStatement ps = conn.prepareStatement(activeLoansSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) >= 3) {
                        conn.rollback();
                        return new IssueResult(false, "Borrowing Blocked: Student has reached the maximum quota of 3 books.", -1);
                    }
                }
            }

            // 4. Check for existing Overdue Books
            String overdueCheckSql = "SELECT COUNT(*) FROM ISSUES WHERE student_id = ? AND status = 'ISSUED' AND due_date < CURRENT_DATE";
            try (PreparedStatement ps = conn.prepareStatement(overdueCheckSql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return new IssueResult(false, "Borrowing Blocked: Student has overdue books that must be returned first.", -1);
                    }
                }
            }

            // 5. Find an Available Physical Copy
            int copyId = -1;
            String accessionNo = "";
            String findCopySql = "SELECT copy_id, accession_number FROM BOOK_COPIES WHERE book_id = ? AND status = 'AVAILABLE' ORDER BY copy_id ASC LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(findCopySql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        copyId = rs.getInt("copy_id");
                        accessionNo = rs.getString("accession_number");
                    }
                }
            }

            if (copyId <= 0) {
                conn.rollback();
                return new IssueResult(false, "Inventory Notice: All copies of this book are currently issued. Student may place a reservation.", -1);
            }

            // 6. Create Issue Record
            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(14); // 14 days loan period
            int issueId = -1;

            String insertIssueSql = "INSERT INTO ISSUES (student_id, copy_id, issue_date, due_date, return_date, status) VALUES (?, ?, ?, ?, NULL, 'ISSUED')";
            try (PreparedStatement ps = conn.prepareStatement(insertIssueSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, studentId);
                ps.setInt(2, copyId);
                ps.setDate(3, Date.valueOf(today));
                ps.setDate(4, Date.valueOf(dueDate));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        issueId = rs.getInt(1);
                    }
                }
            }

            // 7. Update Physical Copy Status
            String updateCopySql = "UPDATE BOOK_COPIES SET status = 'ISSUED' WHERE copy_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateCopySql)) {
                ps.setInt(1, copyId);
                ps.executeUpdate();
            }

            // 8. If student had an active reservation for this book, complete it
            String updateResSql = "UPDATE RESERVATIONS SET status = 'COMPLETED' WHERE student_id = ? AND book_id = ? AND status IN ('PENDING', 'APPROVED')";
            try (PreparedStatement ps = conn.prepareStatement(updateResSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, bookId);
                ps.executeUpdate();
            }

            // 9. Write to Audit Trail
            String auditSql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, librarianUserId);
                ps.setString(2, "Issued copy " + accessionNo + " to student " + regNumber + " (Issue ID: " + issueId + ", Due: " + dueDate + ")");
                ps.executeUpdate();
            }

            conn.commit(); // COMMIT TRANSACTION
            return new IssueResult(true, "Book successfully issued! Copy: " + accessionNo + " | Due Date: " + dueDate, issueId);

        } catch (SQLException e) {
            System.err.println("Issue book transaction error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return new IssueResult(false, "System Error: Transaction rolled back (" + e.getMessage() + ")", -1);
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    /**
     * Processes book return, calculates overdue fine, updates inventory, and handles reservations.
     */
    public ReturnResult returnBook(int issueId, int librarianUserId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Fetch Issue Details
            int copyId = -1;
            int studentId = -1;
            int bookId = -1;
            Date dueDate = null;
            Date returnDate = null;
            String currentStatus = "";

            String findIssueSql = "SELECT i.copy_id, i.student_id, i.due_date, i.return_date, i.status, bc.book_id " +
                                  "FROM ISSUES i JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id WHERE i.issue_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(findIssueSql)) {
                ps.setInt(1, issueId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return new ReturnResult(false, "Return Failed: Issue record not found.", 0, 0);
                    }
                    copyId = rs.getInt("copy_id");
                    studentId = rs.getInt("student_id");
                    dueDate = rs.getDate("due_date");
                    returnDate = rs.getDate("return_date");
                    currentStatus = rs.getString("status");
                    bookId = rs.getInt("book_id");
                }
            }

            if ("RETURNED".equalsIgnoreCase(currentStatus) || returnDate != null) {
                conn.rollback();
                return new ReturnResult(false, "Notice: Book copy has already been returned previously.", 0, 0);
            }

            // 2. Compute Overdue Days and Fine Amount (₹5 / day)
            LocalDate today = LocalDate.now();
            LocalDate due = dueDate.toLocalDate();
            long daysOverdue = ChronoUnit.DAYS.between(due, today);
            int overdueDays = (int) Math.max(0, daysOverdue);
            double dailyFineRate = 5.0; // ₹5 / day
            double fineAmount = overdueDays * dailyFineRate;

            // 3. Update Issue Record
            String updateIssueSql = "UPDATE ISSUES SET return_date = ?, status = 'RETURNED' WHERE issue_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateIssueSql)) {
                ps.setDate(1, Date.valueOf(today));
                ps.setInt(2, issueId);
                ps.executeUpdate();
            }

            // 4. Update Copy Status to AVAILABLE
            String updateCopySql = "UPDATE BOOK_COPIES SET status = 'AVAILABLE' WHERE copy_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateCopySql)) {
                ps.setInt(1, copyId);
                ps.executeUpdate();
            }

            // 5. Insert Fine Record if overdue
            if (fineAmount > 0) {
                String insertFineSql = "INSERT INTO FINES (issue_id, student_id, amount, payment_status, payment_date) VALUES (?, ?, ?, 'UNPAID', NULL)";
                try (PreparedStatement ps = conn.prepareStatement(insertFineSql)) {
                    ps.setInt(1, issueId);
                    ps.setInt(2, studentId);
                    ps.setDouble(3, fineAmount);
                    ps.executeUpdate();
                }
            }

            // 6. Check for Pending Reservations for this book
            int pendingReservations = 0;
            String checkResSql = "SELECT COUNT(*) FROM RESERVATIONS WHERE book_id = ? AND status = 'PENDING'";
            try (PreparedStatement ps = conn.prepareStatement(checkResSql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        pendingReservations = rs.getInt(1);
                    }
                }
            }

            // 7. Audit Trail
            String auditSql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, librarianUserId);
                ps.setString(2, "Processed return for Issue ID: " + issueId + ". Overdue: " + overdueDays + " days, Fine: INR " + fineAmount);
                ps.executeUpdate();
            }

            conn.commit(); // COMMIT TRANSACTION

            String msg = "Book returned successfully!";
            if (fineAmount > 0) {
                msg += " Overdue by " + overdueDays + " day(s). Fine incurred: INR " + fineAmount + " (Recorded as UNPAID).";
            }
            if (pendingReservations > 0) {
                msg += " [NOTE: " + pendingReservations + " student(s) currently in reservation queue for this title].";
            }

            return new ReturnResult(true, msg, fineAmount, overdueDays);

        } catch (SQLException e) {
            System.err.println("Return book transaction error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return new ReturnResult(false, "System Error: Transaction rolled back (" + e.getMessage() + ")", 0, 0);
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    public List<Issue> getActiveIssuesForStudent(int studentId) {
        String sql = "SELECT i.issue_id, i.student_id, i.copy_id, i.issue_date, i.due_date, i.return_date, i.status, " +
                     "b.title, bc.accession_number, bc.shelf_location, u.name AS student_name, s.register_number " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE i.student_id = ? AND i.status IN ('ISSUED', 'OVERDUE') " +
                     "ORDER BY i.due_date ASC";
        return queryIssues(sql, studentId);
    }

    public List<Issue> getBorrowingHistoryForStudent(int studentId) {
        String sql = "SELECT i.issue_id, i.student_id, i.copy_id, i.issue_date, i.due_date, i.return_date, i.status, " +
                     "b.title, bc.accession_number, bc.shelf_location, u.name AS student_name, s.register_number " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE i.student_id = ? " +
                     "ORDER BY i.issue_date DESC";
        return queryIssues(sql, studentId);
    }

    public List<Issue> getAllActiveIssues() {
        String sql = "SELECT i.issue_id, i.student_id, i.copy_id, i.issue_date, i.due_date, i.return_date, i.status, " +
                     "b.title, bc.accession_number, bc.shelf_location, u.name AS student_name, s.register_number " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE i.status IN ('ISSUED', 'OVERDUE') " +
                     "ORDER BY i.due_date ASC";
        return queryIssues(sql, null);
    }

    public List<Issue> getOverdueIssues() {
        String sql = "SELECT i.issue_id, i.student_id, i.copy_id, i.issue_date, i.due_date, i.return_date, i.status, " +
                     "b.title, bc.accession_number, bc.shelf_location, u.name AS student_name, s.register_number " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE (i.status = 'ISSUED' OR i.status = 'OVERDUE') AND i.due_date < CURRENT_DATE " +
                     "ORDER BY i.due_date ASC";
        return queryIssues(sql, null);
    }

    public List<Issue> getRecentIssues(int limit) {
        String sql = "SELECT i.issue_id, i.student_id, i.copy_id, i.issue_date, i.due_date, i.return_date, i.status, " +
                     "b.title, bc.accession_number, bc.shelf_location, u.name AS student_name, s.register_number " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "ORDER BY i.issue_id DESC LIMIT " + limit;
        return queryIssues(sql, null);
    }

    private List<Issue> queryIssues(String sql, Integer param) {
        List<Issue> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Issue issue = new Issue(
                        rs.getInt("issue_id"), rs.getInt("student_id"), rs.getInt("copy_id"),
                        rs.getDate("issue_date"), rs.getDate("due_date"), rs.getDate("return_date"),
                        rs.getString("status")
                    );
                    issue.setBookTitle(rs.getString("title"));
                    issue.setAccessionNumber(rs.getString("accession_number"));
                    issue.setShelfLocation(rs.getString("shelf_location"));
                    issue.setStudentName(rs.getString("student_name"));
                    issue.setRegisterNumber(rs.getString("register_number"));

                    // Calculate overdue days and fine on the fly
                    if (issue.getDueDate() != null) {
                        LocalDate due = issue.getDueDate().toLocalDate();
                        LocalDate refDate = issue.getReturnDate() != null ? issue.getReturnDate().toLocalDate() : LocalDate.now();
                        long diff = ChronoUnit.DAYS.between(due, refDate);
                        if (diff > 0) {
                            issue.setOverdueDays((int) diff);
                            issue.setFineAmount(diff * 5.0);
                        } else {
                            issue.setOverdueDays(0);
                            issue.setFineAmount(0.0);
                        }
                    }
                    list.add(issue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Query issues error: " + e.getMessage());
        }
        return list;
    }
}
