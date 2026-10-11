package controller;

import model.Book;
import model.Fine;
import model.Issue;
import model.Reservation;
import model.Student;
import model.User;
import service.AuthService;
import service.BookService;
import service.FineService;
import service.IssueService;
import service.ReservationService;

import java.util.List;

/**
 * Controller for Student Portal Pages.
 * Member 1 & 4: Student features, Borrowed, Reservations & Fines.
 */
public class StudentController {

    private final AuthService authService = new AuthService();
    private final BookService bookService = new BookService();
    private final IssueService issueService = new IssueService();
    private final ReservationService reservationService = new ReservationService();
    private final FineService fineService = new FineService();

    public String renderDashboard(User user, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());
        int studentId = student != null ? student.getStudentId() : -1;

        List<Issue> activeIssues = issueService.getActiveIssuesForStudent(studentId);
        List<Reservation> reservations = reservationService.getStudentReservations(studentId);
        double unpaidFine = fineService.getTotalUnpaidFineForStudent(studentId);

        int activeLoans = activeIssues.size();
        int overdueCount = 0;
        int dueSoonCount = 0;
        for (Issue issue : activeIssues) {
            if (issue.getOverdueDays() > 0) {
                overdueCount++;
            } else if (issue.getDueDate() != null) {
                dueSoonCount++;
            }
        }

        StringBuilder content = new StringBuilder();

        // 1. Stat Cards
        content.append("<div class=\"stats-grid\">\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Borrowed Books</div><div class=\"stat-value\">").append(activeLoans).append(" / 3</div></div>\n")
            .append("    <div class=\"stat-icon icon-blue\">&#128218;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Overdue Books</div><div class=\"stat-value\" style=\"color: ").append(overdueCount > 0 ? "var(--danger)" : "var(--primary-dark)").append(";\">").append(overdueCount).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-red\">&#9888;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Unpaid Fines</div><div class=\"stat-value\" style=\"color: ").append(unpaidFine > 0 ? "var(--danger)" : "var(--success)").append(";\">INR ").append(String.format("%.2f", unpaidFine)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-amber\">&#9878;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Active Reservations</div><div class=\"stat-value\">").append(reservations.size()).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-purple\">&#128339;</div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        // 2. Quick Action Buttons
        content.append("<div class=\"quick-actions\">\n")
            .append("  <a href=\"/student/books\" class=\"btn btn-primary\">&#128269; Search Catalog</a>\n")
            .append("  <a href=\"/student/borrowed\" class=\"btn btn-secondary\">&#128214; My Borrowed Books</a>\n")
            .append("  <a href=\"/student/reservations\" class=\"btn btn-secondary\">&#128337; My Reservations</a>\n")
            .append("  <a href=\"/student/fines\" class=\"btn btn-secondary\">&#9878; My Fines</a>\n")
            .append("  <a href=\"/student/history\" class=\"btn btn-secondary\">&#128220; Borrowing History</a>\n")
            .append("</div>\n");

        // 3. Currently Borrowed Books Table
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\">\n")
            .append("    <h2>Currently Borrowed Books</h2>\n")
            .append("    <a href=\"/student/borrowed\" class=\"btn btn-sm btn-secondary\">View All</a>\n")
            .append("  </div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Book Title</th><th>Accession No</th><th>Shelf Location</th><th>Issue Date</th><th>Due Date</th><th>Status</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (activeIssues.isEmpty()) {
            content.append("        <tr><td colspan=\"6\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">You have no currently borrowed books. Explore the catalog to borrow books!</td></tr>\n");
        } else {
            for (Issue issue : activeIssues) {
                boolean isOverdue = issue.getOverdueDays() > 0;
                content.append("        <tr>\n")
                    .append("          <td><strong>").append(HtmlRenderer.escape(issue.getBookTitle())).append("</strong></td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(issue.getAccessionNumber())).append("</code></td>\n")
                    .append("          <td>").append(HtmlRenderer.escape(issue.getShelfLocation())).append("</td>\n")
                    .append("          <td>").append(issue.getIssueDate()).append("</td>\n")
                    .append("          <td>").append(issue.getDueDate()).append("</td>\n")
                    .append("          <td><span class=\"badge ").append(isOverdue ? "badge-danger" : "badge-success").append("\">").append(isOverdue ? "OVERDUE (" + issue.getOverdueDays() + " days)" : "ACTIVE").append("</span></td>\n")
                    .append("        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Student Dashboard", "/student/dashboard", user, flashSuccess, flashError, content.toString());
    }

    public String renderSearchBooks(User user, String query, Integer categoryId, Integer authorId, String availability, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());
        int studentId = student != null ? student.getStudentId() : -1;

        List<Book> books = bookService.searchBooks(query, categoryId, authorId, availability);

        StringBuilder content = new StringBuilder();

        // Filter Bar
        content.append("<div class=\"filter-bar\">\n")
            .append("  <form method=\"GET\" action=\"/student/books\" class=\"filter-form\">\n")
            .append("    <div class=\"filter-field\" style=\"flex: 2 1 250px;\">\n")
            .append("      <label class=\"form-label\">Search Catalog</label>\n")
            .append("      <input type=\"text\" name=\"q\" class=\"form-input\" value=\"").append(HtmlRenderer.escape(query)).append("\" placeholder=\"Search title, author, ISBN...\">\n")
            .append("    </div>\n")
            .append("    <div class=\"filter-field\">\n")
            .append("      <label class=\"form-label\">Category</label>\n")
            .append("      <select name=\"category\" class=\"form-select\">\n")
            .append("        <option value=\"\">All Categories</option>\n");
        for (var c : bookService.getAllCategories()) {
            boolean sel = categoryId != null && categoryId == c.getCategoryId();
            content.append("        <option value=\"").append(c.getCategoryId()).append("\" ").append(sel ? "selected" : "").append(">").append(HtmlRenderer.escape(c.getCategoryName())).append("</option>\n");
        }
        content.append("      </select>\n    </div>\n")
            .append("    <div class=\"filter-field\">\n")
            .append("      <label class=\"form-label\">Availability</label>\n")
            .append("      <select name=\"avail\" class=\"form-select\">\n")
            .append("        <option value=\"\">Any</option>\n")
            .append("        <option value=\"AVAILABLE\" ").append("AVAILABLE".equalsIgnoreCase(availability) ? "selected" : "").append(">Available Now</option>\n")
            .append("        <option value=\"UNAVAILABLE\" ").append("UNAVAILABLE".equalsIgnoreCase(availability) ? "selected" : "").append(">Out of Stock</option>\n")
            .append("      </select>\n    </div>\n")
            .append("    <div>\n")
            .append("      <button type=\"submit\" class=\"btn btn-primary\">Filter</button>\n")
            .append("      <a href=\"/student/books\" class=\"btn btn-secondary\">Reset</a>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        // Books Table
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\">\n")
            .append("    <h2>Catalog Results (").append(books.size()).append(" titles found)</h2>\n")
            .append("  </div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Book Title</th><th>Author</th><th>Category</th><th>ISBN</th><th>Shelf</th><th>Availability</th><th>Actions</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (books.isEmpty()) {
            content.append("        <tr><td colspan=\"7\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">No books matched your search query.</td></tr>\n");
        } else {
            for (Book b : books) {
                boolean avail = b.getAvailableCopies() > 0;
                content.append("        <tr>\n")
                    .append("          <td><strong><a href=\"/student/book-details?id=").append(b.getBookId()).append("\">").append(HtmlRenderer.escape(b.getTitle())).append("</a></strong></td>\n")
                    .append("          <td>").append(HtmlRenderer.escape(b.getAuthorName())).append("</td>\n")
                    .append("          <td><span class=\"badge badge-neutral\">").append(HtmlRenderer.escape(b.getCategoryName())).append("</span></td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(b.getIsbn())).append("</code></td>\n")
                    .append("          <td>").append(HtmlRenderer.escape(b.getShelfLocation() != null ? b.getShelfLocation() : "-")).append("</td>\n")
                    .append("          <td><span class=\"badge ").append(avail ? "badge-success" : "badge-danger").append("\">").append(avail ? b.getAvailableCopies() + " / " + b.getTotalCopies() + " Available" : "Out of Stock").append("</span></td>\n")
                    .append("          <td>\n");

                if (!avail) {
                    content.append("            <form method=\"POST\" action=\"/student/reserve\" style=\"display: inline;\">\n")
                        .append("              <input type=\"hidden\" name=\"bookId\" value=\"").append(b.getBookId()).append("\">\n")
                        .append("              <button type=\"submit\" class=\"btn btn-sm btn-secondary\">Reserve Book</button>\n")
                        .append("            </form>\n");
                } else {
                    content.append("            <form method=\"POST\" action=\"/student/borrow\" style=\"display: inline;\">\n")
                        .append("              <input type=\"hidden\" name=\"bookId\" value=\"").append(b.getBookId()).append("\">\n")
                        .append("              <button type=\"submit\" class=\"btn btn-sm btn-primary\">Borrow Book</button>\n")
                        .append("            </form>\n");
                }

                content.append("          </td>\n        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Search Books", "/student/books", user, flashSuccess, flashError, content.toString());
    }

    public String renderBookDetails(User user, int bookId, String flashSuccess, String flashError) {
        Book b = bookService.getBookById(bookId);
        if (b == null) {
            return HtmlRenderer.renderAppPage("Book Details", "/student/books", user, null, "Book not found", "<p>The requested book could not be found.</p>");
        }

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"form-card\" style=\"max-width: 850px; margin: 0 auto;\">\n")
            .append("  <div style=\"display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem;\">\n")
            .append("    <div>\n")
            .append("      <h2 style=\"color: var(--primary-dark); font-size: 1.6rem;\">").append(HtmlRenderer.escape(b.getTitle())).append("</h2>\n")
            .append("      <p style=\"color: var(--text-muted); font-size: 1rem;\">By <strong>").append(HtmlRenderer.escape(b.getAuthorName())).append("</strong> &bull; Published by ").append(HtmlRenderer.escape(b.getPublisherName())).append(" (").append(b.getPublicationYear()).append(")</p>\n")
            .append("    </div>\n")
            .append("    <span class=\"badge ").append(b.isAvailable() ? "badge-success" : "badge-danger").append("\" style=\"font-size: 0.9rem; padding: 0.4rem 0.8rem;\">").append(b.isAvailable() ? b.getAvailableCopies() + " Available" : "Out of Stock").append("</span>\n")
            .append("  </div>\n")
            .append("  <div class=\"form-grid\" style=\"margin-bottom: 1.5rem;\">\n")
            .append("    <div><strong>ISBN:</strong> <code>").append(HtmlRenderer.escape(b.getIsbn())).append("</code></div>\n")
            .append("    <div><strong>Category:</strong> ").append(HtmlRenderer.escape(b.getCategoryName())).append("</div>\n")
            .append("    <div><strong>Edition:</strong> ").append(HtmlRenderer.escape(b.getEdition())).append("</div>\n")
            .append("    <div><strong>Language:</strong> ").append(HtmlRenderer.escape(b.getLanguage())).append("</div>\n")
            .append("    <div><strong>Primary Shelf:</strong> ").append(HtmlRenderer.escape(b.getShelfLocation() != null ? b.getShelfLocation() : "General")).append("</div>\n")
            .append("    <div><strong>Total Copies:</strong> ").append(b.getTotalCopies()).append("</div>\n")
            .append("  </div>\n")
            .append("  <div style=\"background: #f8fafc; padding: 1.25rem; border-radius: var(--radius-sm); margin-bottom: 1.5rem;\">\n")
            .append("    <h4 style=\"margin-bottom: 0.5rem; color: var(--text-muted); text-transform: uppercase; font-size: 0.8rem;\">Book Summary &amp; Description</h4>\n")
            .append("    <p style=\"font-size: 0.95rem; line-height: 1.6;\">").append(HtmlRenderer.escape(b.getDescription())).append("</p>\n")
            .append("  </div>\n")
            .append("  <div style=\"display: flex; gap: 1rem;\">\n")
            .append("    <a href=\"/student/books\" class=\"btn btn-secondary\">&larr; Back to Catalog</a>\n");

        if (b.isAvailable()) {
            content.append("    <form method=\"POST\" action=\"/student/borrow\" style=\"display: inline;\">\n")
                .append("      <input type=\"hidden\" name=\"bookId\" value=\"").append(b.getBookId()).append("\">\n")
                .append("      <button type=\"submit\" class=\"btn btn-primary\" onclick=\"return confirm('Confirm borrowing this book for 14 days?');\">&plus; Borrow This Book Now</button>\n")
                .append("    </form>\n");
        } else {
            content.append("    <form method=\"POST\" action=\"/student/reserve\" style=\"display: inline;\">\n")
                .append("      <input type=\"hidden\" name=\"bookId\" value=\"").append(b.getBookId()).append("\">\n")
                .append("      <button type=\"submit\" class=\"btn btn-warning\">Place In Reservation Queue</button>\n")
                .append("    </form>\n");
        }

        content.append("  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Book Details: " + b.getTitle(), "/student/books", user, flashSuccess, flashError, content.toString());
    }

    public String renderBorrowedBooks(User user, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());
        int studentId = student != null ? student.getStudentId() : -1;
        List<Issue> activeIssues = issueService.getActiveIssuesForStudent(studentId);

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>My Currently Borrowed Books (").append(activeIssues.size()).append(" / 3 Quota)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Book Title</th><th>Accession No</th><th>Shelf Location</th><th>Issue Date</th><th>Due Date</th><th>Status</th><th>Overdue Days</th><th>Est. Fine</th><th>Action</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (activeIssues.isEmpty()) {
            content.append("        <tr><td colspan=\"9\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">No active book loans.</td></tr>\n");
        } else {
            for (Issue i : activeIssues) {
                boolean isOverdue = i.getOverdueDays() > 0;
                content.append("        <tr>\n")
                    .append("          <td><strong>").append(HtmlRenderer.escape(i.getBookTitle())).append("</strong></td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(i.getAccessionNumber())).append("</code></td>\n")
                    .append("          <td>").append(HtmlRenderer.escape(i.getShelfLocation())).append("</td>\n")
                    .append("          <td>").append(i.getIssueDate()).append("</td>\n")
                    .append("          <td>").append(i.getDueDate()).append("</td>\n")
                    .append("          <td><span class=\"badge ").append(isOverdue ? "badge-danger" : "badge-success").append("\">").append(isOverdue ? "OVERDUE" : "ACTIVE").append("</span></td>\n")
                    .append("          <td>").append(isOverdue ? i.getOverdueDays() + " days" : "-").append("</td>\n")
                    .append("          <td>").append(isOverdue ? "INR " + String.format("%.2f", i.getFineAmount()) : "INR 0.00").append("</td>\n")
                    .append("          <td>\n")
                    .append("            <form method=\"POST\" action=\"/student/return\" style=\"display: inline;\">\n")
                    .append("              <input type=\"hidden\" name=\"issueId\" value=\"").append(i.getIssueId()).append("\">\n")
                    .append("              <button type=\"submit\" class=\"btn btn-sm btn-secondary\" onclick=\"return confirm('Confirm returning this book?');\">&larr; Return Book</button>\n")
                    .append("            </form>\n")
                    .append("          </td>\n")
                    .append("        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("My Borrowed Books", "/student/borrowed", user, flashSuccess, flashError, content.toString());
    }

    public String renderReservations(User user, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());
        int studentId = student != null ? student.getStudentId() : -1;
        List<Reservation> list = reservationService.getStudentReservations(studentId);

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>My Book Reservations</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Book Title</th><th>ISBN</th><th>Reservation Date</th><th>Status</th><th>Queue Position</th><th>Actions</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (list.isEmpty()) {
            content.append("        <tr><td colspan=\"6\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">No active reservations. If a book is out of stock in the catalog, you can reserve it!</td></tr>\n");
        } else {
            for (Reservation r : list) {
                content.append("        <tr>\n")
                    .append("          <td><strong>").append(HtmlRenderer.escape(r.getBookTitle())).append("</strong></td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(r.getIsbn())).append("</code></td>\n")
                    .append("          <td>").append(r.getReservationDate()).append("</td>\n")
                    .append("          <td><span class=\"badge ").append("PENDING".equals(r.getStatus()) ? "badge-warning" : "badge-neutral").append("\">").append(r.getStatus()).append("</span></td>\n")
                    .append("          <td><strong>#").append(r.getQueueNumber()).append("</strong></td>\n")
                    .append("          <td>\n");
                if ("PENDING".equals(r.getStatus())) {
                    content.append("            <form method=\"POST\" action=\"/student/cancel-reservation\" style=\"display: inline;\">\n")
                        .append("              <input type=\"hidden\" name=\"resId\" value=\"").append(r.getReservationId()).append("\">\n")
                        .append("              <button type=\"submit\" class=\"btn btn-sm btn-danger\">Cancel</button>\n")
                        .append("            </form>\n");
                } else {
                    content.append("            <span style=\"color: var(--text-muted); font-size: 0.8rem;\">-</span>\n");
                }
                content.append("          </td>\n        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("My Reservations", "/student/reservations", user, flashSuccess, flashError, content.toString());
    }

    public String renderFines(User user, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());
        int studentId = student != null ? student.getStudentId() : -1;
        List<Fine> fines = fineService.getStudentFines(studentId);
        double totalUnpaid = fineService.getTotalUnpaidFineForStudent(studentId);

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"stats-grid\" style=\"grid-template-columns: repeat(2, 1fr);\">\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Total Unpaid Dues</div><div class=\"stat-value\" style=\"color: ").append(totalUnpaid > 0 ? "var(--danger)" : "var(--success)").append(";\">INR ").append(String.format("%.2f", totalUnpaid)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-red\">&#9878;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Total Fine Records</div><div class=\"stat-value\">").append(fines.size()).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-blue\">&#128176;</div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Fine Statement &amp; Receipts</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Fine ID</th><th>Book Title</th><th>Due Date</th><th>Return Date</th><th>Fine Amount</th><th>Status</th><th>Payment Date</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (fines.isEmpty()) {
            content.append("        <tr><td colspan=\"7\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">No fines recorded! You have an outstanding clean record.</td></tr>\n");
        } else {
            for (Fine f : fines) {
                boolean unpaid = "UNPAID".equalsIgnoreCase(f.getPaymentStatus());
                content.append("        <tr>\n")
                    .append("          <td>#").append(f.getFineId()).append("</td>\n")
                    .append("          <td><strong>").append(HtmlRenderer.escape(f.getBookTitle())).append("</strong></td>\n")
                    .append("          <td>").append(f.getDueDate()).append("</td>\n")
                    .append("          <td>").append(f.getReturnDate()).append("</td>\n")
                    .append("          <td><strong>INR ").append(String.format("%.2f", f.getAmount())).append("</strong></td>\n")
                    .append("          <td><span class=\"badge ").append(unpaid ? "badge-danger" : "badge-success").append("\">").append(f.getPaymentStatus()).append("</span></td>\n")
                    .append("          <td>").append(f.getPaymentDate() != null ? f.getPaymentDate() : "Pending Counter Clearance").append("</td>\n")
                    .append("        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("My Fines", "/student/fines", user, flashSuccess, flashError, content.toString());
    }

    public String renderHistory(User user, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());
        int studentId = student != null ? student.getStudentId() : -1;
        List<Issue> history = issueService.getBorrowingHistoryForStudent(studentId);

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Complete Borrowing History</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Book Title</th><th>Accession No</th><th>Issue Date</th><th>Due Date</th><th>Return Date</th><th>Status</th></tr></thead>\n")
            .append("      <tbody>\n");

        if (history.isEmpty()) {
            content.append("        <tr><td colspan=\"6\" style=\"text-align: center; color: var(--text-muted); padding: 2rem;\">No past circulation history.</td></tr>\n");
        } else {
            for (Issue i : history) {
                content.append("        <tr>\n")
                    .append("          <td><strong>").append(HtmlRenderer.escape(i.getBookTitle())).append("</strong></td>\n")
                    .append("          <td><code>").append(HtmlRenderer.escape(i.getAccessionNumber())).append("</code></td>\n")
                    .append("          <td>").append(i.getIssueDate()).append("</td>\n")
                    .append("          <td>").append(i.getDueDate()).append("</td>\n")
                    .append("          <td>").append(i.getReturnDate() != null ? i.getReturnDate() : "Still with Student").append("</td>\n")
                    .append("          <td><span class=\"badge ").append("RETURNED".equals(i.getStatus()) ? "badge-success" : "badge-neutral").append("\">").append(i.getStatus()).append("</span></td>\n")
                    .append("        </tr>\n");
            }
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Borrowing History", "/student/history", user, flashSuccess, flashError, content.toString());
    }

    public String renderProfile(User user, String flashSuccess, String flashError) {
        Student student = authService.getStudentByUserId(user.getUserId());

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"form-card\">\n")
            .append("  <h2 style=\"margin-bottom: 1.5rem; color: var(--primary-dark);\">Student Profile Details</h2>\n")
            .append("  <div class=\"form-grid\">\n")
            .append("    <div><strong>Full Name:</strong> ").append(HtmlRenderer.escape(user.getName())).append("</div>\n")
            .append("    <div><strong>College Email:</strong> ").append(HtmlRenderer.escape(user.getEmail())).append("</div>\n")
            .append("    <div><strong>Register Number:</strong> <code>").append(HtmlRenderer.escape(student != null ? student.getRegisterNumber() : "-")).append("</code></div>\n")
            .append("    <div><strong>Department:</strong> ").append(HtmlRenderer.escape(student != null ? student.getDepartment() : "-")).append("</div>\n")
            .append("    <div><strong>Year of Study:</strong> Year ").append(student != null ? student.getYear() : 1).append("</div>\n")
            .append("    <div><strong>Section:</strong> ").append(HtmlRenderer.escape(student != null ? student.getSection() : "A")).append("</div>\n")
            .append("    <div><strong>Phone:</strong> ").append(HtmlRenderer.escape(user.getPhone())).append("</div>\n")
            .append("    <div><strong>Membership Status:</strong> <span class=\"badge badge-success\">").append(user.getStatus()).append("</span></div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        return HtmlRenderer.renderAppPage("My Profile", "/student/profile", user, flashSuccess, flashError, content.toString());
    }
}
