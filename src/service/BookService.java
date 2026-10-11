package service;

import dao.AuthorDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.PublisherDAO;
import model.Author;
import model.Book;
import model.BookCopy;
import model.Category;
import model.Publisher;

import java.util.List;

/**
 * Service managing Book Catalog, Inventory and Physical Copies.
 * Member 2: Books & Catalog Management.
 */
public class BookService {

    private final BookDAO bookDAO = new BookDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PublisherDAO publisherDAO = new PublisherDAO();

    public List<Book> searchBooks(String query, Integer categoryId, Integer authorId, String availability) {
        return bookDAO.searchBooks(query, categoryId, authorId, availability);
    }

    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }

    public String addBook(String isbn, String title, int authorId, int categoryId, int publisherId,
                          int publicationYear, String edition, String language, String description,
                          int initialCopies, String initialShelf, int librarianUserId) {
        // Validation
        if (isbn == null || isbn.trim().isEmpty()) return "ISBN is required.";
        if (title == null || title.trim().isEmpty()) return "Book title is required.";
        if (authorId <= 0) return "Please select a valid author.";
        if (categoryId <= 0) return "Please select a valid category.";
        if (publisherId <= 0) return "Please select a valid publisher.";
        if (publicationYear < 1800 || publicationYear > 2030) return "Invalid publication year.";
        if (initialCopies < 1) return "Initial copy count must be at least 1.";

        // Prevent duplicate ISBN
        if (bookDAO.findByIsbn(isbn) != null) {
            return "A book with ISBN '" + isbn + "' already exists in catalog.";
        }

        Book book = new Book();
        book.setIsbn(isbn.trim());
        book.setTitle(title.trim());
        book.setAuthorId(authorId);
        book.setCategoryId(categoryId);
        book.setPublisherId(publisherId);
        book.setPublicationYear(publicationYear);
        book.setEdition(edition != null && !edition.trim().isEmpty() ? edition.trim() : "1st Edition");
        book.setLanguage(language != null && !language.trim().isEmpty() ? language.trim() : "English");
        book.setDescription(description != null ? description.trim() : "");

        boolean ok = bookDAO.addBookWithCopies(book, initialCopies, initialShelf, librarianUserId);
        return ok ? "SUCCESS" : "Failed to add book due to database error.";
    }

    public boolean updateBook(Book book) {
        return bookDAO.updateBook(book);
    }

    public boolean deleteBook(int bookId) {
        return bookDAO.deleteBook(bookId);
    }

    public List<BookCopy> getCopiesForBook(int bookId) {
        return bookDAO.getCopiesForBook(bookId);
    }

    public List<BookCopy> getAllCopies() {
        return bookDAO.getAllCopies();
    }

    public boolean addCopy(int bookId, String accessionNo, String shelf) {
        return bookDAO.addCopy(bookId, accessionNo, shelf);
    }

    public boolean updateCopy(int copyId, String shelf, String status) {
        return bookDAO.updateCopy(copyId, shelf, status);
    }

    public List<Author> getAllAuthors() { return authorDAO.getAllAuthors(); }
    public List<Category> getAllCategories() { return categoryDAO.getAllCategories(); }
    public List<Publisher> getAllPublishers() { return publisherDAO.getAllPublishers(); }
}
