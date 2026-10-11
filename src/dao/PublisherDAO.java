package dao;

import database.DatabaseConnection;
import model.Publisher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PublisherDAO {

    public List<Publisher> getAllPublishers() {
        List<Publisher> list = new ArrayList<>();
        String sql = "SELECT publisher_id, publisher_name, address, contact FROM PUBLISHERS ORDER BY publisher_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Publisher(rs.getInt("publisher_id"), rs.getString("publisher_name"), rs.getString("address"), rs.getString("contact")));
            }
        } catch (SQLException e) {
            System.err.println("Get all publishers error: " + e.getMessage());
        }
        return list;
    }

    public Publisher findById(int id) {
        String sql = "SELECT publisher_id, publisher_name, address, contact FROM PUBLISHERS WHERE publisher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Publisher(rs.getInt("publisher_id"), rs.getString("publisher_name"), rs.getString("address"), rs.getString("contact"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find publisher error: " + e.getMessage());
        }
        return null;
    }

    public int addPublisher(Publisher p) {
        String sql = "INSERT INTO PUBLISHERS (publisher_name, address, contact) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPublisherName());
            ps.setString(2, p.getAddress());
            ps.setString(3, p.getContact());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Add publisher error: " + e.getMessage());
        }
        return -1;
    }
}
