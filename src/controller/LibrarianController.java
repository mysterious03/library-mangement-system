package controller;

import dao.StudentDAO;
import model.Book;
import model.BookCopy;
import model.Fine;
import model.Issue;
import model.Reservation;
import model.Student;
import model.User;
import service.BookService;
import service.FineService;
import service.IssueService;
import service.ReportService;
import service.ReservationService;

import java.util.List;
import java.util.Map;

/**
 * Controller for Librarian Circulation & Catalog Operations.
 * Member 2 & 4: Books, Issues, Returns, Fines, Reservations.
 */
public class LibrarianController {

    private final BookService bookService = new BookService();
    private final IssueService issueService = new IssueService();
    private final ReservationService reservationService = new ReservationService();
    private final FineService fineService = new FineService();
    private final ReportService reportService = new ReportService();
    private final StudentDAO studentDAO = new StudentDAO();

    public String renderDashboard(User user, String flashSuccess, String flashError) {
        Map<String, Object> stats = reportService.getSystemStatistics();
        List<Issue> activeIssues = issueService.getAllActiveIssues();
        List<Issue> overdueIssues = issueService.getOverdueIssues();
        List<Reservation> reservations = reservationService.getAllReservations();

        StringBuilder content = new StringBuilder();

        // 1. Stat Cards
        content.append("<div class=\"stats-grid\">\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Total Titles</div><div class=\"stat-value\">").append(stats.getOrDefault("totalBooks", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-blue\">&#128218;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Available Copies</div><div class=\"stat-value\">").append(stats.getOrDefault("availableCopies", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-green\">&#10003;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Currently Issued</div><div class=\"stat-value\">").append(stats.getOrDefault("issuedBooks", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-purple\">&#128214;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Overdue Books</div><div class=\"stat-value\" style=\"color: var(--danger);\">").append(stats.getOrDefault("overdueBooks", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-red\">&#9888;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Pending Reservations</div><div class=\"stat-value\">").append(stats.getOrDefault("activeReservations", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-amber\">&#128339;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Pending Fines Due</div><div class=\"stat-value\" style=\"color: var(--danger);\">INR ").append(String.format("%.2f", (Double) stats.getOrDefault("pendingFines", 0.0))).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-red\">&#9878;</div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        // 2. Quick Action Toolbar
        content.append("<div class=\"quick-actions\">\n")
            .append("  <a href=\"/librarian/issue\" class=\"btn btn-primary\">&#128214; Issue Book</a>\n")
            .append("  <a href=\"/librarian/return\" class=\"btn btn-success\">&#8617; Process Return</a>\n")
            .append("  <a href=\"/librarian/add-book\" class=\"btn btn-secondary\">+ Add New Book</a>\n")
            .append("  <a href=\"/librarian/fines\" class=\"btn btn-secondary\">&#9878; Collect Fines</a>\n")
            .append("  <a href=\"/librarian/overdue\" class=\"btn btn-secondary\">&#9888; View Overdue</a>\n")
            .append("  <a href=\"/librarian/reports\" class=\"btn btn-secondary\">&#128202; Analytical Reports</a>\n")
            .append("</div>\n");

        // 3. Active Loans Table
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\">\n")
            .append("    <h2>Active Circulation Loans (").append(activeIssues.size()).append(" records)</h2>\n")
            .append("    <a href=\"/librarian/issue\" class=\"btn btn-sm btn-primary\">+ Issue Book</a>\n")
            .append("  </div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Issue ID</th><th>Student Name</th><th>Reg No</th><th>Book Title</th><th>Accession No</th><th>Issue Date</th><th>Due Date</th><th>Status</th><th>Actions</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (Issue i : activeIssues) {
            boolean isOverdue = i.getOverdueDays() > 0;
            content.append("        <tr>\n")
                .append("          <td>#").append(i.getIssueId()).append("</td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(i.getStudentName())).append("</strong></td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(i.getRegisterNumber())).append("</code></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(i.getBookTitle())).append("</td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(i.getAccessionNumber())).append("</code></td>\n")
                .append("          <td>").append(i.getIssueDate()).append("</td>\n")
                .append("          <td>").append(i.getDueDate()).append("</td>\n")
                .append("          <td><span class=\"badge ").append(isOverdue ? "badge-danger" : "badge-success").append("\">").append(isOverdue ? "OVERDUE (" + i.getOverdueDays() + "d)" : "ACTIVE").append("</span></td>\n")
                .append("          <td>\n")
                .append("            <form method=\"POST\" action=\"/librarian/return\" style=\"display: inline;\">\n")
                .append("              <input type=\"hidden\" name=\"issueId\" value=\"").append(i.getIssueId()).append("\">\n")
                .append("              <button type=\"submit\" class=\"btn btn-sm btn-success\">Process Return</button>\n")
                .append("            </form>\n")
                .append("          </td>\n        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Librarian Dashboard", "/librarian/dashboard", user, flashSuccess, flashError, content.toString());
    }

    public String renderManageBooks(User user, String query, String flashSuccess, String flashError) {
        List<Book> books = bookService.searchBooks(query, null, null, null);

        StringBuilder content = new StringBuilder();
        content.append("<div style=\"display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;\">\n")
            .append("  <form method=\"GET\" action=\"/librarian/books\" style=\"display: flex; gap: 0.5rem;\">\n")
            .append("    <input type=\"text\" name=\"q\" class=\"form-input\" value=\"").append(HtmlRenderer.escape(query)).append("\" placeholder=\"Search title, author, ISBN...\" style=\"width: 320px;\">\n")
            .append("    <button type=\"submit\" class=\"btn btn-primary\">Search</button>\n")
            .append("  </form>\n")
            .append("  <a href=\"/librarian/add-book\" class=\"btn btn-primary\">+ Add New Book</a>\n")
            .append("</div>\n");

        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Catalog Master (").append(books.size()).append(" titles)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Book ID</th><th>ISBN</th><th>Title</th><th>Author</th><th>Category</th><th>Publisher</th><th>Year</th><th>Copies (Avail/Total)</th><th>Actions</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (Book b : books) {
            content.append("        <tr>\n")
                .append("          <td>").append(b.getBookId()).append("</td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(b.getIsbn())).append("</code></td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(b.getTitle())).append("</strong></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(b.getAuthorName())).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(b.getCategoryName())).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(b.getPublisherName())).append("</td>\n")
                .append("          <td>").append(b.getPublicationYear()).append("</td>\n")
                .append("          <td><strong>").append(b.getAvailableCopies()).append("</strong> / ").append(b.getTotalCopies()).append("</td>\n")
                .append("          <td>\n")
                .append("            <a href=\"/librarian/copies?bookId=").append(b.getBookId()).append("\" class=\"btn btn-sm btn-secondary\">Manage Copies</a>\n")
                .append("          </td>\n        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Manage Books", "/librarian/books", user, flashSuccess, flashError, content.toString());
    }

    public String renderAddBookForm(User user, String flashSuccess, String flashError) {
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"form-card\">\n")
            .append("  <h2 style=\"margin-bottom: 1.5rem; color: var(--primary-dark);\">Add New Book to Catalog</h2>\n")
            .append("  <form method=\"POST\" action=\"/librarian/add-book\">\n")
            .append("    <div class=\"form-grid\">\n")
            .append("      <div class=\"form-group full-width\">\n")
            .append("        <label class=\"form-label\">Book Title *</label>\n")
            .append("        <input type=\"text\" name=\"title\" class=\"form-input\" required placeholder=\"e.g. Distributed Operating Systems\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">ISBN (Unique 13 or 10 digit) *</label>\n")
            .append("        <input type=\"text\" name=\"isbn\" class=\"form-input\" required placeholder=\"e.g. 9780130888938\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Publication Year *</label>\n")
            .append("        <input type=\"number\" name=\"publicationYear\" class=\"form-input\" required min=\"1800\" max=\"2030\" value=\"2022\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Author *</label>\n")
            .append("        <select name=\"authorId\" class=\"form-select\" required>\n")
            .append("          <option value=\"\">-- Select Author --</option>\n");
        for (var a : bookService.getAllAuthors()) {
            content.append("          <option value=\"").append(a.getAuthorId()).append("\">").append(HtmlRenderer.escape(a.getAuthorName())).append("</option>\n");
        }
        content.append("        </select>\n      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Category *</label>\n")
            .append("        <select name=\"categoryId\" class=\"form-select\" required>\n")
            .append("          <option value=\"\">-- Select Category --</option>\n");
        for (var c : bookService.getAllCategories()) {
            content.append("          <option value=\"").append(c.getCategoryId()).append("\">").append(HtmlRenderer.escape(c.getCategoryName())).append("</option>\n");
        }
        content.append("        </select>\n      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Publisher *</label>\n")
            .append("        <select name=\"publisherId\" class=\"form-select\" required>\n")
            .append("          <option value=\"\">-- Select Publisher --</option>\n");
        for (var p : bookService.getAllPublishers()) {
            content.append("          <option value=\"").append(p.getPublisherId()).append("\">").append(HtmlRenderer.escape(p.getPublisherName())).append("</option>\n");
        }
        content.append("        </select>\n      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Edition</label>\n")
            .append("        <input type=\"text\" name=\"edition\" class=\"form-input\" value=\"1st Edition\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Language</label>\n")
            .append("        <input type=\"text\" name=\"language\" class=\"form-input\" value=\"English\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\">Initial Physical Copies *</label>\n")
            .append("        <input type=\"number\" name=\"initialCopies\" class=\"form-input\" required min=\"1\" value=\"2\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group full-width\">\n")
            .append("        <label class=\"form-label\">Shelf Location *</label>\n")
            .append("        <input type=\"text\" name=\"initialShelf\" class=\"form-input\" required placeholder=\"e.g. Shelf CS-B2\">\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group full-width\">\n")
            .append("        <label class=\"form-label\">Description / Synopsis</label>\n")
            .append("        <textarea name=\"description\" class=\"form-textarea\" placeholder=\"Book abstract and contents summary...\"></textarea>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("    <div class=\"form-actions\">\n")
            .append("      <a href=\"/librarian/books\" class=\"btn btn-secondary\">Cancel</a>\n")
            .append("      <button type=\"submit\" class=\"btn btn-primary\">Save Book &amp; Generate Barcode Copies</button>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        return HtmlRenderer.renderAppPage("Add Book", "/librarian/add-book", user, flashSuccess, flashError, content.toString());
    }

    public String renderManageCopies(User user, Integer bookId, String flashSuccess, String flashError) {
        List<BookCopy> copies = (bookId != null && bookId > 0) ? bookService.getCopiesForBook(bookId) : bookService.getAllCopies();
        List<Book> allBooks = bookService.searchBooks("", null, null, null);

        StringBuilder content = new StringBuilder();

        // Add Copy Form
        content.append("<div class=\"form-card\" style=\"max-width: 100%; margin-bottom: 2rem; padding: 1.5rem;\">\n")
            .append("  <h3 style=\"margin-bottom: 1rem; color: var(--primary); font-size: 1.1rem;\">+ Register New Physical Copy</h3>\n")
            .append("  <form method=\"POST\" action=\"/librarian/add-copy\" style=\"display: flex; gap: 1rem; flex-wrap: wrap; align-items: flex-end;\">\n")
            .append("    <div style=\"flex: 2 1 250px;\">\n")
            .append("      <label class=\"form-label\">Select Book</label>\n")
            .append("      <select name=\"bookId\" class=\"form-select\" required>\n");
        for (Book b : allBooks) {
            boolean sel = bookId != null && bookId == b.getBookId();
            content.append("        <option value=\"").append(b.getBookId()).append("\" ").append(sel ? "selected" : "").append(">").append(HtmlRenderer.escape(b.getTitle())).append(" (").append(b.getIsbn()).append(")</option>\n");
        }
        content.append("      </select>\n    </div>\n")
            .append("    <div style=\"flex: 1 1 180px;\">\n")
            .append("      <label class=\"form-label\">Accession Number</label>\n")
            .append("      <input type=\"text\" name=\"accessionNumber\" class=\"form-input\" required placeholder=\"e.g. ACC-99901\">\n")
            .append("    </div>\n")
            .append("    <div style=\"flex: 1 1 180px;\">\n")
            .append("      <label class=\"form-label\">Shelf Location</label>\n")
            .append("      <input type=\"text\" name=\"shelfLocation\" class=\"form-input\" required placeholder=\"e.g. Shelf CS-A1\">\n")
            .append("    </div>\n")
            .append("    <div>\n")
            .append("      <button type=\"submit\" class=\"btn btn-primary\">Add Copy</button>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        // Copies Table
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Physical Book Copies (").append(copies.size()).append(" accessions)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Copy ID</th><th>Accession Number</th><th>Book Title</th><th>Shelf Location</th><th>Status</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (BookCopy c : copies) {
            boolean avail = "AVAILABLE".equalsIgnoreCase(c.getStatus());
            content.append("        <tr>\n")
                .append("          <td>#").append(c.getCopyId()).append("</td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(c.getAccessionNumber())).append("</code></td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(c.getBookTitle())).append("</strong></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(c.getShelfLocation())).append("</td>\n")
                .append("          <td><span class=\"badge ").append(avail ? "badge-success" : "badge-neutral").append("\">").append(c.getStatus()).append("</span></td>\n")
                .append("        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Book Copies", "/librarian/copies", user, flashSuccess, flashError, content.toString());
    }

    public String renderIssuePage(User user, String flashSuccess, String flashError) {
        List<Student> students = studentDAO.getAllStudents();
        List<Book> books = bookService.searchBooks("", null, null, "AVAILABLE");

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"form-card\">\n")
            .append("  <h2 style=\"margin-bottom: 0.5rem; color: var(--primary-dark);\">Book Issue Counter</h2>\n")
            .append("  <p style=\"color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;\">\n")
            .append("    System automatically enforces library loan rules: verifies student enrollment, blocks if active loans &ge; 3, " +
                     "    blocks if overdue books exist, and blocks if unpaid fines are pending.\n")
            .append("  </p>\n")
            .append("  <form method=\"POST\" action=\"/librarian/issue\">\n")
            .append("    <div class=\"form-group\">\n")
            .append("      <label class=\"form-label\">Select Enrolled Student *</label>\n")
            .append("      <select name=\"studentId\" class=\"form-select\" required>\n")
            .append("        <option value=\"\">-- Select Student (Register No / Name) --</option>\n");
        for (Student s : students) {
            content.append("        <option value=\"").append(s.getStudentId()).append("\">").append(s.getRegisterNumber()).append(" - ").append(HtmlRenderer.escape(s.getName())).append(" (").append(HtmlRenderer.escape(s.getDepartment())).append(")</option>\n");
        }
        content.append("      </select>\n    </div>\n")
            .append("    <div class=\"form-group\">\n")
            .append("      <label class=\"form-label\">Select Available Book Title *</label>\n")
            .append("      <select name=\"bookId\" class=\"form-select\" required>\n")
            .append("        <option value=\"\">-- Select Book to Issue --</option>\n");
        for (Book b : books) {
            content.append("        <option value=\"").append(b.getBookId()).append("\">").append(HtmlRenderer.escape(b.getTitle())).append(" (").append(b.getAvailableCopies()).append(" available)</option>\n");
        }
        content.append("      </select>\n    </div>\n")
            .append("    <div style=\"background: #f8fafc; padding: 1rem; border-radius: var(--radius-sm); margin: 1.5rem 0; font-size: 0.85rem; color: var(--text-muted);\">\n")
            .append("      &#8226; <strong>Loan Period:</strong> 14 Days from today<br>\n")
            .append("      &#8226; <strong>Overdue Rate:</strong> INR 5.00 / day late fine\n")
            .append("    </div>\n")
            .append("    <div class=\"form-actions\">\n")
            .append("      <a href=\"/librarian/dashboard\" class=\"btn btn-secondary\">Cancel</a>\n")
            .append("      <button type=\"submit\" class=\"btn btn-primary\">Execute Issue Transaction</button>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        return HtmlRenderer.renderAppPage("Issue Book", "/librarian/issue", user, flashSuccess, flashError, content.toString());
    }

    public String renderReturnPage(User user, String flashSuccess, String flashError) {
        List<Issue> activeIssues = issueService.getAllActiveIssues();

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"form-card\" style=\"max-width: 800px;\">\n")
            .append("  <h2 style=\"margin-bottom: 0.5rem; color: var(--primary-dark);\">Book Return &amp; Fine Processing Counter</h2>\n")
            .append("  <p style=\"color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;\">\n")
            .append("    Returning a book automatically calculates overdue days, assigns fine (if overdue), updates physical copy status to AVAILABLE, and alerts if reservations exist.\n")
            .append("  </p>\n")
            .append("  <form method=\"POST\" action=\"/librarian/return\">\n")
            .append("    <div class=\"form-group\">\n")
            .append("      <label class=\"form-label\">Select Active Issue Record *</label>\n")
            .append("      <select name=\"issueId\" class=\"form-select\" required>\n")
            .append("        <option value=\"\">-- Select Issued Book --</option>\n");
        for (Issue i : activeIssues) {
            String ov = i.getOverdueDays() > 0 ? " [OVERDUE: " + i.getOverdueDays() + " days - Fine: INR " + (i.getOverdueDays() * 5) + "]" : " [Due: " + i.getDueDate() + "]";
            content.append("        <option value=\"").append(i.getIssueId()).append("\">#").append(i.getIssueId()).append(" - ").append(HtmlRenderer.escape(i.getBookTitle())).append(" (").append(i.getRegisterNumber()).append(" - ").append(HtmlRenderer.escape(i.getStudentName())).append(")").append(ov).append("</option>\n");
        }
        content.append("      </select>\n    </div>\n")
            .append("    <div class=\"form-actions\">\n")
            .append("      <a href=\"/librarian/dashboard\" class=\"btn btn-secondary\">Cancel</a>\n")
            .append("      <button type=\"submit\" class=\"btn btn-success\">Process Return &amp; Inventory Restock</button>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        return HtmlRenderer.renderAppPage("Return Book", "/librarian/return", user, flashSuccess, flashError, content.toString());
    }

    public String renderFines(User user, String statusFilter, String flashSuccess, String flashError) {
        List<Fine> fines = fineService.getAllFines(statusFilter);

        StringBuilder content = new StringBuilder();
        content.append("<div style=\"display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;\">\n")
            .append("  <div class=\"quick-actions\" style=\"margin-bottom: 0;\">\n")
            .append("    <a href=\"/librarian/fines\" class=\"btn ").append(statusFilter == null || statusFilter.isEmpty() ? "btn-primary" : "btn-secondary").append("\">All Fines</a>\n")
            .append("    <a href=\"/librarian/fines?status=UNPAID\" class=\"btn ").append("UNPAID".equalsIgnoreCase(statusFilter) ? "btn-primary" : "btn-secondary").append("\">Unpaid Only</a>\n")
            .append("    <a href=\"/librarian/fines?status=PAID\" class=\"btn ").append("PAID".equalsIgnoreCase(statusFilter) ? "btn-primary" : "btn-secondary").append("\">Paid Receipts</a>\n")
            .append("  </div>\n")
            .append("</div>\n");

        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Fines &amp; Dues Register (").append(fines.size()).append(" records)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Fine ID</th><th>Student Name</th><th>Reg No</th><th>Book Title</th><th>Due Date</th><th>Return Date</th><th>Fine Amount</th><th>Status</th><th>Actions</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (Fine f : fines) {
            boolean unpaid = "UNPAID".equalsIgnoreCase(f.getPaymentStatus());
            content.append("        <tr>\n")
                .append("          <td>#").append(f.getFineId()).append("</td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(f.getStudentName())).append("</strong></td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(f.getRegisterNumber())).append("</code></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(f.getBookTitle())).append("</td>\n")
                .append("          <td>").append(f.getDueDate()).append("</td>\n")
                .append("          <td>").append(f.getReturnDate()).append("</td>\n")
                .append("          <td><strong>INR ").append(String.format("%.2f", f.getAmount())).append("</strong></td>\n")
                .append("          <td><span class=\"badge ").append(unpaid ? "badge-danger" : "badge-success").append("\">").append(f.getPaymentStatus()).append("</span></td>\n")
                .append("          <td>\n");

            if (unpaid) {
                content.append("            <form method=\"POST\" action=\"/librarian/pay-fine\" style=\"display: inline;\">\n")
                    .append("              <input type=\"hidden\" name=\"fineId\" value=\"").append(f.getFineId()).append("\">\n")
                    .append("              <button type=\"submit\" class=\"btn btn-sm btn-success\">Mark as Paid</button>\n")
                    .append("            </form>\n");
            } else {
                content.append("            <span style=\"font-size: 0.8rem; color: var(--text-muted);\">Paid on ").append(f.getPaymentDate()).append("</span>\n");
            }

            content.append("          </td>\n        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Fine Management", "/librarian/fines", user, flashSuccess, flashError, content.toString());
    }

    public String renderOverduePage(User user, String flashSuccess, String flashError) {
        List<Issue> overdueList = issueService.getOverdueIssues();

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Overdue Books Register (").append(overdueList.size()).append(" overdue items)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Issue ID</th><th>Student Name</th><th>Reg No</th><th>Book Title</th><th>Accession</th><th>Due Date</th><th>Days Overdue</th><th>Estimated Fine</th><th>Actions</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (overdueList.isEmpty()) {
            content.append("        <tr><td colspan=\"9\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">No overdue books at this time!</td></tr>\n");
        } else {
            for (Issue i : overdueList) {
                content.append("        <tr>\n")
                    .append("          <td>#").append(i.getIssueId()).append("</td>\n")
                    .append("          <td><strong>").append(HtmlRenderer.escape(i.getStudentName())).append("</strong></td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(i.getRegisterNumber())).append("</code></td>\n")
                    .append("          <td>").append(HtmlRenderer.escape(i.getBookTitle())).append("</td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(i.getAccessionNumber())).append("</code></td>\n")
                    .append("          <td>").append(i.getDueDate()).append("</td>\n")
                    .append("          <td><span class=\"badge badge-danger\">").append(i.getOverdueDays()).append(" days</span></td>\n")
                    .append("          <td><strong>INR ").append(String.format("%.2f", i.getFineAmount())).append("</strong></td>\n")
                    .append("          <td>\n")
                    .append("            <form method=\"POST\" action=\"/librarian/return\" style=\"display: inline;\">\n")
                    .append("              <input type=\"hidden\" name=\"issueId\" value=\"").append(i.getIssueId()).append("\">\n")
                    .append("              <button type=\"submit\" class=\"btn btn-sm btn-success\">Process Return</button>\n")
                    .append("            </form>\n")
                    .append("          </td>\n        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Overdue Books", "/librarian/overdue", user, flashSuccess, flashError, content.toString());
    }

    public String renderReservations(User user, String flashSuccess, String flashError) {
        List<Reservation> reservations = reservationService.getAllReservations();

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Book Reservation Waiting Queues (").append(reservations.size()).append(" total)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Res ID</th><th>Book Title</th><th>Student Name</th><th>Reg No</th><th>Queue Position</th><th>Reservation Date</th><th>Status</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (Reservation r : reservations) {
            content.append("        <tr>\n")
                .append("          <td>#").append(r.getReservationId()).append("</td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(r.getBookTitle())).append("</strong></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(r.getStudentName())).append("</td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(r.getRegisterNumber())).append("</code></td>\n")
                .append("          <td><strong>#").append(r.getQueueNumber()).append("</strong></td>\n")
                .append("          <td>").append(r.getReservationDate()).append("</td>\n")
                .append("          <td><span class=\"badge ").append("PENDING".equals(r.getStatus()) ? "badge-warning" : "badge-neutral").append("\">").append(r.getStatus()).append("</span></td>\n")
                .append("        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Reservations Queue", "/librarian/reservations", user, flashSuccess, flashError, content.toString());
    }

    public String renderStudents(User user, String flashSuccess, String flashError) {
        List<Student> students = studentDAO.getAllStudents();

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Enrolled Student Directory (").append(students.size()).append(" students)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Student ID</th><th>Register No</th><th>Student Name</th><th>Department</th><th>Year</th><th>Section</th><th>Email</th><th>Phone</th><th>Status</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (Student s : students) {
            content.append("        <tr>\n")
                .append("          <td>#").append(s.getStudentId()).append("</td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(s.getRegisterNumber())).append("</code></td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(s.getName())).append("</strong></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(s.getDepartment())).append("</td>\n")
                .append("          <td>Year ").append(s.getYear()).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(s.getSection())).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(s.getEmail())).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(s.getPhone())).append("</td>\n")
                .append("          <td><span class=\"badge badge-success\">").append(s.getStatus()).append("</span></td>\n")
                .append("        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Manage Students", "/librarian/students", user, flashSuccess, flashError, content.toString());
    }
}
