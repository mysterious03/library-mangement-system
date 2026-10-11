package dao;

import database.DatabaseConnection;
import model.Author;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    public List<Author> getAllAuthors() {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT author_id, author_name, biography FROM AUTHORS ORDER BY author_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Author(rs.getInt("author_id"), rs.getString("author_name"), rs.getString("biography")));
            }
        } catch (SQLException e) {
            System.err.println("Get all authors error: " + e.getMessage());
        }
        return list;
    }

    public Author findById(int id) {
        String sql = "SELECT author_id, author_name, biography FROM AUTHORS WHERE author_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Author(rs.getInt("author_id"), rs.getString("author_name"), rs.getString("biography"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find author error: " + e.getMessage());
        }
        return null;
    }

    public int addAuthor(Author a) {
        String sql = "INSERT INTO AUTHORS (author_name, biography) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getAuthorName());
            ps.setString(2, a.getBiography());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Add author error: " + e.getMessage());
        }
        return -1;
    }
}
