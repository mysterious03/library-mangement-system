package dao;

import database.DatabaseConnection;
import model.Book;
import model.BookCopy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Books and Physical Copies.
 * Demonstrates SQL Joins, Aggregations, Transactions, and Search Filtering.
 * Member 2: Book Catalog & Copy Management.
 */
public class BookDAO {

    public List<Book> searchBooks(String query, Integer categoryId, Integer authorId, String availability) {
        List<Book> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT b.book_id, b.isbn, b.title, b.author_id, a.author_name, " +
            "b.category_id, c.category_name, b.publisher_id, p.publisher_name, " +
            "b.publication_year, b.edition, b.language, b.description, " +
            "COUNT(bc.copy_id) AS total_copies, " +
            "SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies, " +
            "MIN(bc.shelf_location) AS sample_shelf " +
            "FROM BOOKS b " +
            "JOIN AUTHORS a ON b.author_id = a.author_id " +
            "JOIN CATEGORIES c ON b.category_id = c.category_id " +
            "JOIN PUBLISHERS p ON b.publisher_id = p.publisher_id " +
            "LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            String q = "%" + query.trim().toLowerCase() + "%";
            sql.append("AND (LOWER(b.title) LIKE ? OR LOWER(b.isbn) LIKE ? OR LOWER(a.author_name) LIKE ? OR LOWER(c.category_name) LIKE ? OR LOWER(p.publisher_name) LIKE ?) ");
            for (int i = 0; i < 5; i++) params.add(q);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND b.category_id = ? ");
            params.add(categoryId);
        }

        if (authorId != null && authorId > 0) {
            sql.append("AND b.author_id = ? ");
            params.add(authorId);
        }

        sql.append("GROUP BY b.book_id, b.isbn, b.title, b.author_id, a.author_name, " +
                   "b.category_id, c.category_name, b.publisher_id, p.publisher_name, " +
                   "b.publication_year, b.edition, b.language, b.description ");

        if ("AVAILABLE".equalsIgnoreCase(availability)) {
            sql.append("HAVING SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) > 0 ");
        } else if ("UNAVAILABLE".equalsIgnoreCase(availability)) {
            sql.append("HAVING SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) = 0 ");
        }

        sql.append("ORDER BY b.title ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search books error: " + e.getMessage());
        }
        return list;
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT b.book_id, b.isbn, b.title, b.author_id, a.author_name, " +
                     "b.category_id, c.category_name, b.publisher_id, p.publisher_name, " +
                     "b.publication_year, b.edition, b.language, b.description, " +
                     "COUNT(bc.copy_id) AS total_copies, " +
                     "SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies, " +
                     "MIN(bc.shelf_location) AS sample_shelf " +
                     "FROM BOOKS b " +
                     "JOIN AUTHORS a ON b.author_id = a.author_id " +
                     "JOIN CATEGORIES c ON b.category_id = c.category_id " +
                     "JOIN PUBLISHERS p ON b.publisher_id = p.publisher_id " +
                     "LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id " +
                     "WHERE b.book_id = ? " +
                     "GROUP BY b.book_id, b.isbn, b.title, b.author_id, a.author_name, " +
                     "b.category_id, c.category_name, b.publisher_id, p.publisher_name, " +
                     "b.publication_year, b.edition, b.language, b.description";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get book by ID error: " + e.getMessage());
        }
        return null;
    }

    public Book findByIsbn(String isbn) {
        String sql = "SELECT b.book_id, b.isbn, b.title, b.author_id, a.author_name, " +
                     "b.category_id, c.category_name, b.publisher_id, p.publisher_name, " +
                     "b.publication_year, b.edition, b.language, b.description, " +
                     "COUNT(bc.copy_id) AS total_copies, " +
                     "SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies, " +
                     "MIN(bc.shelf_location) AS sample_shelf " +
                     "FROM BOOKS b " +
                     "JOIN AUTHORS a ON b.author_id = a.author_id " +
                     "JOIN CATEGORIES c ON b.category_id = c.category_id " +
                     "JOIN PUBLISHERS p ON b.publisher_id = p.publisher_id " +
                     "LEFT JOIN BOOK_COPIES bc ON b.book_id = bc.book_id " +
                     "WHERE LOWER(b.isbn) = LOWER(?) " +
                     "GROUP BY b.book_id, b.isbn, b.title, b.author_id, a.author_name, " +
                     "b.category_id, c.category_name, b.publisher_id, p.publisher_name, " +
                     "b.publication_year, b.edition, b.language, b.description";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find book by ISBN error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Transactionally inserts a new book and initial physical copies.
     */
    public boolean addBookWithCopies(Book book, int initialCopies, String initialShelf, int librarianUserId) {
        String insertBookSql = "INSERT INTO BOOKS (isbn, title, author_id, category_id, publisher_id, publication_year, edition, language, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertCopySql = "INSERT INTO BOOK_COPIES (book_id, accession_number, shelf_location, status) VALUES (?, ?, ?, 'AVAILABLE')";
        String auditSql = "INSERT INTO AUDIT_LOG (user_id, action) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            int bookId = -1;
            try (PreparedStatement ps = conn.prepareStatement(insertBookSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, book.getIsbn().trim());
                ps.setString(2, book.getTitle().trim());
                ps.setInt(3, book.getAuthorId());
                ps.setInt(4, book.getCategoryId());
                ps.setInt(5, book.getPublisherId());
                ps.setInt(6, book.getPublicationYear());
                ps.setString(7, book.getEdition() != null ? book.getEdition() : "1st Edition");
                ps.setString(8, book.getLanguage() != null ? book.getLanguage() : "English");
                ps.setString(9, book.getDescription());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        bookId = rs.getInt(1);
                        book.setBookId(bookId);
                    }
                }
            }

            if (bookId <= 0) {
                conn.rollback();
                return false;
            }

            // Create initial physical copies
            if (initialCopies > 0) {
                try (PreparedStatement psCopy = conn.prepareStatement(insertCopySql)) {
                    for (int i = 1; i <= initialCopies; i++) {
                        String accNum = "ACC-" + String.format("%03d", bookId) + "-" + String.format("%02d", i);
                        psCopy.setInt(1, bookId);
                        psCopy.setString(2, accNum);
                        psCopy.setString(3, initialShelf != null ? initialShelf : "General Rack");
                        psCopy.addBatch();
                    }
                    psCopy.executeBatch();
                }
            }

            // Audit Trail
            try (PreparedStatement psAudit = conn.prepareStatement(auditSql)) {
                psAudit.setInt(1, librarianUserId);
                psAudit.setString(2, "Added new title: " + book.getTitle() + " with " + initialCopies + " copies");
                psAudit.executeUpdate();
            }

            conn.commit(); // COMMIT TRANSACTION
            return true;
        } catch (SQLException e) {
            System.err.println("Add book transaction error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            DatabaseConnection.close(conn);
        }
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE BOOKS SET isbn = ?, title = ?, author_id = ?, category_id = ?, publisher_id = ?, " +
                     "publication_year = ?, edition = ?, language = ?, description = ? WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn().trim());
            ps.setString(2, book.getTitle().trim());
            ps.setInt(3, book.getAuthorId());
            ps.setInt(4, book.getCategoryId());
            ps.setInt(5, book.getPublisherId());
            ps.setInt(6, book.getPublicationYear());
            ps.setString(7, book.getEdition());
            ps.setString(8, book.getLanguage());
            ps.setString(9, book.getDescription());
            ps.setInt(10, book.getBookId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update book error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM BOOKS WHERE book_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete book error: " + e.getMessage());
            return false;
        }
    }

    public List<BookCopy> getCopiesForBook(int bookId) {
        List<BookCopy> list = new ArrayList<>();
        String sql = "SELECT bc.copy_id, bc.book_id, bc.accession_number, bc.shelf_location, bc.status, b.title " +
                     "FROM BOOK_COPIES bc JOIN BOOKS b ON bc.book_id = b.book_id WHERE bc.book_id = ? ORDER BY bc.copy_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookCopy c = new BookCopy(
                        rs.getInt("copy_id"), rs.getInt("book_id"),
                        rs.getString("accession_number"), rs.getString("shelf_location"),
                        rs.getString("status")
                    );
                    c.setBookTitle(rs.getString("title"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get copies for book error: " + e.getMessage());
        }
        return list;
    }

    public List<BookCopy> getAllCopies() {
        List<BookCopy> list = new ArrayList<>();
        String sql = "SELECT bc.copy_id, bc.book_id, bc.accession_number, bc.shelf_location, bc.status, b.title " +
                     "FROM BOOK_COPIES bc JOIN BOOKS b ON bc.book_id = b.book_id ORDER BY bc.accession_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                BookCopy c = new BookCopy(
                    rs.getInt("copy_id"), rs.getInt("book_id"),
                    rs.getString("accession_number"), rs.getString("shelf_location"),
                    rs.getString("status")
                );
                c.setBookTitle(rs.getString("title"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Get all copies error: " + e.getMessage());
        }
        return list;
    }

    public boolean addCopy(int bookId, String accessionNumber, String shelfLocation) {
        String sql = "INSERT INTO BOOK_COPIES (book_id, accession_number, shelf_location, status) VALUES (?, ?, ?, 'AVAILABLE')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setString(2, accessionNumber.trim());
            ps.setString(3, shelfLocation.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add copy error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCopy(int copyId, String shelfLocation, String status) {
        String sql = "UPDATE BOOK_COPIES SET shelf_location = ?, status = ? WHERE copy_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shelfLocation.trim());
            ps.setString(2, status.trim());
            ps.setInt(3, copyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update copy error: " + e.getMessage());
            return false;
        }
    }

    public BookCopy findAvailableCopy(int bookId) {
        String sql = "SELECT copy_id, book_id, accession_number, shelf_location, status FROM BOOK_COPIES WHERE book_id = ? AND status = 'AVAILABLE' ORDER BY copy_id ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookCopy(
                        rs.getInt("copy_id"), rs.getInt("book_id"),
                        rs.getString("accession_number"), rs.getString("shelf_location"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Find available copy error: " + e.getMessage());
        }
        return null;
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthorId(rs.getInt("author_id"));
        b.setAuthorName(rs.getString("author_name"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        b.setPublisherId(rs.getInt("publisher_id"));
        b.setPublisherName(rs.getString("publisher_name"));
        b.setPublicationYear(rs.getInt("publication_year"));
        b.setEdition(rs.getString("edition"));
        b.setLanguage(rs.getString("language"));
        b.setDescription(rs.getString("description"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setShelfLocation(rs.getString("sample_shelf"));
        return b;
    }
}
