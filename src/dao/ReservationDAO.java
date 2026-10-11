package dao;

import database.DatabaseConnection;
import model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for RESERVATIONS table.
 * Implements Queue ordering and duplicate prevention.
 * Member 4: Reservations & Fines Module.
 */
public class ReservationDAO {

    public static class ReservationResult {
        public final boolean success;
        public final String message;
        public final int queueNumber;

        public ReservationResult(boolean success, String message, int queueNumber) {
            this.success = success;
            this.message = message;
            this.queueNumber = queueNumber;
        }
    }

    /**
     * Reserves a book if currently out of stock.
     */
    public ReservationResult reserveBook(int studentId, int bookId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Check if copies are currently available on shelf
            String checkAvailabilitySql = "SELECT COUNT(*) FROM BOOK_COPIES WHERE book_id = ? AND status = 'AVAILABLE'";
            try (PreparedStatement ps = conn.prepareStatement(checkAvailabilitySql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return new ReservationResult(false, "Physical copy currently available on shelf! Please visit the counter to issue directly.", 0);
                    }
                }
            }

            // 2. Prevent duplicate active reservation by same student
            String checkExistingSql = "SELECT COUNT(*) FROM RESERVATIONS WHERE student_id = ? AND book_id = ? AND status IN ('PENDING', 'APPROVED')";
            try (PreparedStatement ps = conn.prepareStatement(checkExistingSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return new ReservationResult(false, "You already have an active reservation for this book in the queue.", 0);
                    }
                }
            }

            // 3. Calculate next queue number for this book
            int nextQueue = 1;
            String nextQueueSql = "SELECT COALESCE(MAX(queue_number), 0) + 1 FROM RESERVATIONS WHERE book_id = ? AND status = 'PENDING'";
            try (PreparedStatement ps = conn.prepareStatement(nextQueueSql)) {
                ps.setInt(1, bookId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nextQueue = rs.getInt(1);
                    }
                }
            }

            // 4. Insert Reservation
            String insertSql = "INSERT INTO RESERVATIONS (student_id, book_id, status, queue_number) VALUES (?, ?, 'PENDING', ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, bookId);
                ps.setInt(3, nextQueue);
                ps.executeUpdate();
            }

            // 5. Audit Log
            String auditSql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES ((SELECT user_id FROM STUDENTS WHERE student_id = ?), ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, studentId);
                ps.setString(2, "Placed reservation for Book ID: " + bookId + " (Queue #" + nextQueue + ")");
                ps.executeUpdate();
            }

            conn.commit();
            return new ReservationResult(true, "Reservation placed successfully! You are #" + nextQueue + " in line.", nextQueue);

        } catch (SQLException e) {
            System.err.println("Reservation transaction error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return new ReservationResult(false, "System Error: " + e.getMessage(), 0);
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    public List<Reservation> getReservationsForStudent(int studentId) {
        String sql = "SELECT r.reservation_id, r.student_id, r.book_id, r.reservation_date, r.status, r.queue_number, " +
                     "b.title, b.isbn, u.name AS student_name, s.register_number " +
                     "FROM RESERVATIONS r " +
                     "JOIN BOOKS b ON r.book_id = b.book_id " +
                     "JOIN STUDENTS s ON r.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "WHERE r.student_id = ? " +
                     "ORDER BY r.reservation_date DESC";
        return queryReservations(sql, studentId);
    }

    public List<Reservation> getAllReservations() {
        String sql = "SELECT r.reservation_id, r.student_id, r.book_id, r.reservation_date, r.status, r.queue_number, " +
                     "b.title, b.isbn, u.name AS student_name, s.register_number " +
                     "FROM RESERVATIONS r " +
                     "JOIN BOOKS b ON r.book_id = b.book_id " +
                     "JOIN STUDENTS s ON r.student_id = s.student_id " +
                     "JOIN USERS u ON s.user_id = u.user_id " +
                     "ORDER BY r.reservation_date DESC";
        return queryReservations(sql, null);
    }

    public boolean cancelReservation(int reservationId, int studentId) {
        String sql = "UPDATE RESERVATIONS SET status = 'CANCELLED' WHERE reservation_id = ? AND (student_id = ? OR ? = 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setInt(2, studentId);
            ps.setInt(3, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Cancel reservation error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int reservationId, String newStatus) {
        String sql = "UPDATE RESERVATIONS SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update reservation status error: " + e.getMessage());
            return false;
        }
    }

    private List<Reservation> queryReservations(String sql, Integer studentId) {
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (studentId != null) {
                ps.setInt(1, studentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation(
                        rs.getInt("reservation_id"), rs.getInt("student_id"), rs.getInt("book_id"),
                        rs.getTimestamp("reservation_date"), rs.getString("status"), rs.getInt("queue_number")
                    );
                    r.setBookTitle(rs.getString("title"));
                    r.setIsbn(rs.getString("isbn"));
                    r.setStudentName(rs.getString("student_name"));
                    r.setRegisterNumber(rs.getString("register_number"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Query reservations error: " + e.getMessage());
        }
        return list;
    }
}
