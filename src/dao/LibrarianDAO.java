package dao;

import database.DatabaseConnection;
import model.Librarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LIBRARIANS table.
 * Member 2: Librarian & Catalog Module.
 */
public class LibrarianDAO {

    private final UserDAO userDAO = new UserDAO();

    public Librarian findByUserId(int userId) {
        String sql = "SELECT l.librarian_id, l.user_id, l.employee_id, " +
                     "u.name, u.email, u.password, u.phone, u.status, u.created_at " +
                     "FROM LIBRARIANS l JOIN USERS u ON l.user_id = u.user_id WHERE l.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find librarian by user ID error: " + e.getMessage());
        }
        return null;
    }

    public List<Librarian> getAllLibrarians() {
        List<Librarian> list = new ArrayList<>();
        String sql = "SELECT l.librarian_id, l.user_id, l.employee_id, " +
                     "u.name, u.email, u.password, u.phone, u.status, u.created_at " +
                     "FROM LIBRARIANS l JOIN USERS u ON l.user_id = u.user_id ORDER BY l.librarian_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Get all librarians error: " + e.getMessage());
        }
        return list;
    }

    public boolean createLibrarian(Librarian librarian) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int userId = userDAO.createUser(conn, librarian);
            if (userId <= 0) {
                conn.rollback();
                return false;
            }
            librarian.setUserId(userId);

            String sql = "INSERT INTO LIBRARIANS (user_id, employee_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, librarian.getEmployeeId().trim().toUpperCase());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        librarian.setLibrarianId(rs.getInt(1));
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Create librarian transaction error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    private Librarian mapRow(ResultSet rs) throws SQLException {
        Librarian l = new Librarian();
        l.setLibrarianId(rs.getInt("librarian_id"));
        l.setUserId(rs.getInt("user_id"));
        l.setEmployeeId(rs.getString("employee_id"));
        l.setName(rs.getString("name"));
        l.setEmail(rs.getString("email"));
        l.setPassword(rs.getString("password"));
        l.setPhone(rs.getString("phone"));
        l.setStatus(rs.getString("status"));
        l.setCreatedAt(rs.getTimestamp("created_at"));
        return l;
    }
}
