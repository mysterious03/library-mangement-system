package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Analytical Reports & Dashboard Statistics.
 * Executes the 10 Complex SQL Analytical Queries.
 * Member 4: Reports, Analytics & Fines Module.
 */
public class ReportDAO {

    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM BOOKS) AS total_books, " +
                     "(SELECT COUNT(*) FROM BOOK_COPIES WHERE status = 'AVAILABLE') AS available_copies, " +
                     "(SELECT COUNT(*) FROM ISSUES WHERE status IN ('ISSUED', 'OVERDUE')) AS issued_books, " +
                     "(SELECT COUNT(*) FROM ISSUES WHERE (status = 'ISSUED' OR status = 'OVERDUE') AND due_date < CURRENT_DATE) AS overdue_books, " +
                     "(SELECT COUNT(*) FROM STUDENTS) AS total_students, " +
                     "(SELECT COUNT(*) FROM RESERVATIONS WHERE status = 'PENDING') AS active_reservations, " +
                     "(SELECT COALESCE(SUM(amount), 0) FROM FINES WHERE payment_status = 'UNPAID') AS pending_fines_amount, " +
                     "(SELECT COALESCE(SUM(amount), 0) FROM FINES WHERE payment_status = 'PAID') AS collected_fines_amount";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalBooks", rs.getInt("total_books"));
                stats.put("availableCopies", rs.getInt("available_copies"));
                stats.put("issuedBooks", rs.getInt("issued_books"));
                stats.put("overdueBooks", rs.getInt("overdue_books"));
                stats.put("totalStudents", rs.getInt("total_students"));
                stats.put("activeReservations", rs.getInt("active_reservations"));
                stats.put("pendingFines", rs.getDouble("pending_fines_amount"));
                stats.put("collectedFines", rs.getDouble("collected_fines_amount"));
            }
        } catch (SQLException e) {
            System.err.println("Get system statistics error: " + e.getMessage());
        }
        return stats;
    }

    /**
     * Executes a generic reporting query and returns tabular data with column headers.
     */
    public List<Map<String, String>> executeReport(String sql) {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    String colName = meta.getColumnLabel(i);
                    Object val = rs.getObject(i);
                    row.put(colName, val != null ? val.toString() : "-");
                }
                rows.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Execute report error: " + e.getMessage());
        }
        return rows;
    }

    // 1. Most Borrowed Books
    public List<Map<String, String>> getMostBorrowedBooks() {
        String sql = "SELECT b.book_id AS \"Book ID\", b.isbn AS \"ISBN\", b.title AS \"Title\", " +
                     "a.author_name AS \"Author\", c.category_name AS \"Category\", " +
                     "COUNT(i.issue_id) AS \"Times Borrowed\", COUNT(DISTINCT i.student_id) AS \"Unique Students\" " +
                     "FROM BOOKS b " +
                     "JOIN AUTHORS a ON b.author_id = a.author_id " +
                     "JOIN CATEGORIES c ON b.category_id = c.category_id " +
                     "LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id " +
                     "LEFT JOIN ISSUES i ON bc.copy_id = i.copy_id " +
                     "GROUP BY b.book_id, b.isbn, b.title, a.author_name, c.category_name " +
                     "ORDER BY COUNT(i.issue_id) DESC, b.title ASC LIMIT 10";
        return executeReport(sql);
    }

    // 2. Most Active Students
    public List<Map<String, String>> getMostActiveStudents() {
        String sql = "SELECT s.register_number AS \"Register No\", u.name AS \"Student Name\", " +
                     "s.department AS \"Department\", s.year AS \"Year\", " +
                     "COUNT(i.issue_id) AS \"Total Borrowed\", " +
                     "SUM(CASE WHEN i.status IN ('ISSUED', 'OVERDUE') THEN 1 ELSE 0 END) AS \"Currently Borrowed\" " +
                     "FROM STUDENTS s " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "JOIN ISSUES i ON s.student_id = i.student_id " +
                     "GROUP BY s.student_id, s.register_number, u.name, s.department, s.year " +
                     "HAVING COUNT(i.issue_id) > 0 " +
                     "ORDER BY COUNT(i.issue_id) DESC, u.name ASC LIMIT 10";
        return executeReport(sql);
    }

    // 3. Overdue Books
    public List<Map<String, String>> getOverdueBooksReport() {
        String sql = "SELECT b.title AS \"Book Title\", bc.accession_number AS \"Accession No\", " +
                     "u.name AS \"Student\", s.register_number AS \"Reg No\", u.phone AS \"Phone\", " +
                     "i.issue_date AS \"Issue Date\", i.due_date AS \"Due Date\", " +
                     "DATEDIFF('DAY', i.due_date, CURRENT_DATE) AS \"Days Overdue\", " +
                     "(DATEDIFF('DAY', i.due_date, CURRENT_DATE) * 5.0) AS \"Fine (INR)\" " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE (i.status = 'ISSUED' OR i.status = 'OVERDUE') AND i.due_date < CURRENT_DATE " +
                     "ORDER BY DATEDIFF('DAY', i.due_date, CURRENT_DATE) DESC";
        return executeReport(sql);
    }

    // 4. Students with Unpaid Fines
    public List<Map<String, String>> getUnpaidFinesReport() {
        String sql = "SELECT s.register_number AS \"Register No\", u.name AS \"Student Name\", " +
                     "s.department AS \"Department\", u.phone AS \"Phone\", " +
                     "COUNT(f.fine_id) AS \"Unpaid Fines Count\", " +
                     "SUM(f.amount) AS \"Total Due (INR)\" " +
                     "FROM STUDENTS s " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "JOIN FINES f ON s.student_id = f.student_id " +
                     "WHERE f.payment_status = 'UNPAID' " +
                     "GROUP BY s.student_id, s.register_number, u.name, s.department, u.phone " +
                     "HAVING SUM(f.amount) > 0 " +
                     "ORDER BY \"Total Due (INR)\" DESC";
        return executeReport(sql);
    }

    // 5. Books by Category
    public List<Map<String, String>> getBooksByCategory() {
        String sql = "SELECT c.category_name AS \"Category\", " +
                     "COUNT(DISTINCT b.book_id) AS \"Total Titles\", " +
                     "COUNT(bc.copy_id) AS \"Total Copies\", " +
                     "SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS \"Available\", " +
                     "SUM(CASE WHEN bc.status = 'ISSUED' THEN 1 ELSE 0 END) AS \"Issued\" " +
                     "FROM CATEGORIES c " +
                     "LEFT JOIN BOOKS b ON c.category_id = b.category_id " +
                     "LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id " +
                     "GROUP BY c.category_id, c.category_name " +
                     "ORDER BY \"Total Copies\" DESC";
        return executeReport(sql);
    }

    // 6. Available Books (Shelf Audit)
    public List<Map<String, String>> getAvailableBooksReport() {
        String sql = "SELECT bc.accession_number AS \"Accession No\", b.title AS \"Book Title\", " +
                     "a.author_name AS \"Author\", c.category_name AS \"Category\", " +
                     "bc.shelf_location AS \"Shelf Location\" " +
                     "FROM BOOK_COPIES bc " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN AUTHORS a ON b.author_id = a.author_id " +
                     "JOIN CATEGORIES c ON b.category_id = c.category_id " +
                     "WHERE bc.status = 'AVAILABLE' " +
                     "ORDER BY bc.shelf_location ASC, b.title ASC";
        return executeReport(sql);
    }

    // 7. Issued Books
    public List<Map<String, String>> getIssuedBooksReport() {
        String sql = "SELECT b.title AS \"Book Title\", bc.accession_number AS \"Accession No\", " +
                     "u.name AS \"Student\", s.register_number AS \"Reg No\", " +
                     "i.issue_date AS \"Issue Date\", i.due_date AS \"Due Date\", " +
                     "CASE WHEN i.due_date < CURRENT_DATE THEN 'OVERDUE' ELSE 'ACTIVE' END AS \"Loan Status\" " +
                     "FROM ISSUES i " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "JOIN STUDENTS s ON i.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE i.status = 'ISSUED' OR i.status = 'OVERDUE' " +
                     "ORDER BY i.due_date ASC";
        return executeReport(sql);
    }

    // 8. Reservation Statistics
    public List<Map<String, String>> getReservationStatsReport() {
        String sql = "SELECT b.title AS \"Book Title\", " +
                     "COUNT(r.reservation_id) AS \"Total Reservations\", " +
                     "SUM(CASE WHEN r.status = 'PENDING' THEN 1 ELSE 0 END) AS \"Pending In Queue\", " +
                     "SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END) AS \"Fulfilled\", " +
                     "SUM(CASE WHEN r.status = 'CANCELLED' THEN 1 ELSE 0 END) AS \"Cancelled\" " +
                     "FROM BOOKS b " +
                     "JOIN RESERVATIONS r ON b.book_id = r.book_id " +
                     "GROUP BY b.book_id, b.title " +
                     "ORDER BY \"Pending In Queue\" DESC, \"Total Reservations\" DESC";
        return executeReport(sql);
    }

    // 9. Monthly Borrowing Statistics
    public List<Map<String, String>> getMonthlyBorrowingReport() {
        String sql = "SELECT YEAR(issue_date) AS \"Year\", MONTH(issue_date) AS \"Month\", " +
                     "COUNT(issue_id) AS \"Total Issues\", " +
                     "COUNT(DISTINCT student_id) AS \"Active Borrowers\" " +
                     "FROM ISSUES " +
                     "GROUP BY YEAR(issue_date), MONTH(issue_date) " +
                     "ORDER BY \"Year\" DESC, \"Month\" DESC";
        return executeReport(sql);
    }

    // 10. Fine Collection Statistics
    public List<Map<String, String>> getFineCollectionReport() {
        String sql = "SELECT payment_status AS \"Payment Status\", " +
                     "COUNT(fine_id) AS \"Count\", " +
                     "SUM(amount) AS \"Total Amount (INR)\", " +
                     "AVG(amount) AS \"Average Amount (INR)\", " +
                     "MIN(amount) AS \"Min Amount (INR)\", " +
                     "MAX(amount) AS \"Max Amount (INR)\" " +
                     "FROM FINES " +
                     "GROUP BY payment_status";
        return executeReport(sql);
    }
}
