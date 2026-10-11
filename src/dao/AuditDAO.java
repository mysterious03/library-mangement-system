package dao;

import database.DatabaseConnection;
import model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    public boolean logAction(Integer userId, String action) {
        String sql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null && userId > 0) {
                ps.setInt(1, userId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, action);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Audit log error: " + e.getMessage());
            return false;
        }
    }

    public List<AuditLog> getRecentLogs(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT a.log_id, a.user_id, a.action, a.action_date, u.name AS user_name, u.role AS user_role " +
                     "FROM AUDIT_LOG a LEFT JOIN USERS u ON a.user_id = u.user_id " +
                     "ORDER BY a.log_id DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AuditLog(
                        rs.getInt("log_id"),
                        (Integer) rs.getObject("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_role"),
                        rs.getString("action"),
                        rs.getTimestamp("action_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Get recent logs error: " + e.getMessage());
        }
        return list;
    }
}
