package dao;

import database.DatabaseConnection;
import model.Student;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for STUDENTS table.
 * Demonstrates JDBC PreparedStatement, Transactions, and Joins.
 * Member 1: Student Module.
 */
public class StudentDAO {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Registers a new student using an ACID database transaction.
     */
    public boolean registerStudent(Student student) {
        String insertStudentSql = "INSERT INTO STUDENTS (user_id, register_number, department, year, section) VALUES (?, ?, ?, ?, ?)";
        String auditSql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Insert into USERS table
            int userId = userDAO.createUser(conn, student);
            if (userId <= 0) {
                conn.rollback();
                return false;
            }
            student.setUserId(userId);

            // 2. Insert into STUDENTS table
            try (PreparedStatement ps = conn.prepareStatement(insertStudentSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, student.getRegisterNumber().trim().toUpperCase());
                ps.setString(3, student.getDepartment().trim());
                ps.setInt(4, student.getYear());
                ps.setString(5, student.getSection().trim().toUpperCase());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setStudentId(rs.getInt(1));
                    }
                }
            }

            // 3. Insert into AUDIT_LOG
            try (PreparedStatement psAudit = conn.prepareStatement(auditSql)) {
                psAudit.setInt(1, userId);
                psAudit.setString(2, "Self-registered new student account (" + student.getRegisterNumber() + ")");
                psAudit.executeUpdate();
            }

            conn.commit(); // COMMIT TRANSACTION
            return true;

        } catch (SQLException e) {
            System.err.println("Student registration transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // ROLLBACK TRANSACTION
                } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    public Student findByUserId(int userId) {
        String sql = "SELECT s.student_id, s.user_id, s.register_number, s.department, s.year, s.section, " +
                     "u.name, u.email, u.password, u.phone, u.status, u.created_at " +
                     "FROM STUDENTS s JOIN USERS u ON s.user_id = u.user_id WHERE s.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find student by user ID error: " + e.getMessage());
        }
        return null;
    }

    public Student findByStudentId(int studentId) {
        String sql = "SELECT s.student_id, s.user_id, s.register_number, s.department, s.year, s.section, " +
                     "u.name, u.email, u.password, u.phone, u.status, u.created_at " +
                     "FROM STUDENTS s JOIN USERS u ON s.user_id = u.user_id WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find student by student ID error: " + e.getMessage());
        }
        return null;
    }

    public Student findByRegisterNumber(String regNo) {
        String sql = "SELECT s.student_id, s.user_id, s.register_number, s.department, s.year, s.section, " +
                     "u.name, u.email, u.password, u.phone, u.status, u.created_at " +
                     "FROM STUDENTS s JOIN USERS u ON s.user_id = u.user_id WHERE UPPER(s.register_number) = UPPER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find student by reg number error: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.user_id, s.register_number, s.department, s.year, s.section, " +
                     "u.name, u.email, u.password, u.phone, u.status, u.created_at " +
                     "FROM STUDENTS s JOIN USERS u ON s.user_id = u.user_id ORDER BY s.student_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all students error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStudentDetails(int studentId, String department, int year, String section) {
        String sql = "UPDATE STUDENTS SET department = ?, year = ?, section = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department);
            ps.setInt(2, year);
            ps.setString(3, section);
            ps.setInt(4, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update student error: " + e.getMessage());
            return false;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setRegisterNumber(rs.getString("register_number"));
        s.setDepartment(rs.getString("department"));
        s.setYear(rs.getInt("year"));
        s.setSection(rs.getString("section"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setPhone(rs.getString("phone"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
