package dao;

import database.DatabaseConnection;
import model.Fine;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FINES management.
 * Member 4: Fines & Reports Module.
 */
public class FineDAO {

    public List<Fine> getFinesForStudent(int studentId) {
        String sql = "SELECT f.fine_id, f.issue_id, f.student_id, f.amount, f.payment_status, f.payment_date, " +
                     "u.name AS student_name, s.register_number, b.title AS book_title, i.due_date, i.return_date " +
                     "FROM FINES f " +
                     "JOIN STUDENTS s ON f.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "JOIN ISSUES i ON f.issue_id = i.issue_id " +
                     "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
                     "JOIN BOOKS b ON bc.book_id = b.book_id " +
                     "WHERE f.student_id = ? " +
                     "ORDER BY f.payment_status DESC, f.fine_id DESC";
        return queryFines(sql, studentId, null);
    }

    public List<Fine> getAllFines(String filterStatus) {
        StringBuilder sql = new StringBuilder(
            "SELECT f.fine_id, f.issue_id, f.student_id, f.amount, f.payment_status, f.payment_date, " +
            "u.name AS student_name, s.register_number, b.title AS book_title, i.due_date, i.return_date " +
            "FROM FINES f " +
            "JOIN STUDENTS s ON f.student_id = s.student_id " +
            "JOIN USERS u ON s.user_id = u.user_id " +
            "JOIN ISSUES i ON f.issue_id = i.issue_id " +
            "JOIN BOOK_COPIES bc ON i.copy_id = bc.copy_id " +
            "JOIN BOOKS b ON bc.book_id = b.book_id "
        );

        if ("UNPAID".equalsIgnoreCase(filterStatus)) {
            sql.append("WHERE f.payment_status = 'UNPAID' ");
        } else if ("PAID".equalsIgnoreCase(filterStatus)) {
            sql.append("WHERE f.payment_status = 'PAID' ");
        }

        sql.append("ORDER BY f.fine_id DESC");
        return queryFines(sql.toString(), null, null);
    }

    public boolean markFineAsPaid(int fineId, int librarianUserId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            String updateSql = "UPDATE FINES SET payment_status = 'PAID', payment_date = ? WHERE fine_id = ? AND payment_status = 'UNPAID'";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.setInt(2, fineId);
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Audit Log
            String auditSql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, librarianUserId);
                ps.setString(2, "Marked Fine ID #" + fineId + " as PAID");
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Mark fine as paid error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    public double getTotalUnpaidFineForStudent(int studentId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM FINES WHERE student_id = ? AND payment_status = 'UNPAID'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get unpaid fine sum error: " + e.getMessage());
        }
        return 0.0;
    }

    private List<Fine> queryFines(String sql, Integer studentId, String status) {
        List<Fine> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (studentId != null) {
                ps.setInt(1, studentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Fine f = new Fine(
                        rs.getInt("fine_id"), rs.getInt("issue_id"), rs.getInt("student_id"),
                        rs.getDouble("amount"), rs.getString("payment_status"), rs.getDate("payment_date")
                    );
                    f.setStudentName(rs.getString("student_name"));
                    f.setRegisterNumber(rs.getString("register_number"));
                    f.setBookTitle(rs.getString("book_title"));
                    f.setDueDate(rs.getDate("due_date"));
                    f.setReturnDate(rs.getDate("return_date"));
                    list.add(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("Query fines error: " + e.getMessage());
        }
        return list;
    }
}
