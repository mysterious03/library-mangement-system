package controller;

import model.AuditLog;
import model.Book;
import model.Librarian;
import model.Student;
import model.User;
import service.BookService;
import service.ReportService;
import service.UserService;

import java.util.List;
import java.util.Map;

/**
 * Controller for Administrator Dashboard, User Management, Reports & Audit Logs.
 * Member 1 & 3: Administration & Security Auditing.
 */
public class AdminController {

    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();
    private final ReportService reportService = new ReportService();

    public String renderDashboard(User user, String flashSuccess, String flashError) {
        Map<String, Object> stats = reportService.getSystemStatistics();
        List<AuditLog> recentLogs = userService.getRecentAuditLogs(6);

        StringBuilder content = new StringBuilder();

        // 1. Stat Cards
        content.append("<div class=\"stats-grid\">\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Catalog Titles</div><div class=\"stat-value\">").append(stats.getOrDefault("totalBooks", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-blue\">&#128218;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Enrolled Students</div><div class=\"stat-value\">").append(stats.getOrDefault("totalStudents", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-green\">&#127891;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Total Active Loans</div><div class=\"stat-value\">").append(stats.getOrDefault("issuedBooks", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-purple\">&#128214;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Overdue Violations</div><div class=\"stat-value\" style=\"color: var(--danger);\">").append(stats.getOrDefault("overdueBooks", 0)).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-red\">&#9888;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Fines Collected</div><div class=\"stat-value\" style=\"color: var(--success);\">INR ").append(String.format("%.2f", (Double) stats.getOrDefault("collectedFines", 0.0))).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-green\">&#128176;</div>\n")
            .append("  </div>\n")
            .append("  <div class=\"stat-card\">\n")
            .append("    <div class=\"stat-info\"><div class=\"stat-label\">Pending Dues</div><div class=\"stat-value\" style=\"color: var(--danger);\">INR ").append(String.format("%.2f", (Double) stats.getOrDefault("pendingFines", 0.0))).append("</div></div>\n")
            .append("    <div class=\"stat-icon icon-amber\">&#9878;</div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        // 2. Quick Admin Actions
        content.append("<div class=\"quick-actions\">\n")
            .append("  <a href=\"/admin/users\" class=\"btn btn-primary\">&#128101; Manage Users</a>\n")
            .append("  <a href=\"/admin/librarians\" class=\"btn btn-secondary\">&#128188; Manage Librarians</a>\n")
            .append("  <a href=\"/admin/reports\" class=\"btn btn-secondary\">&#128202; SQL Analytical Reports</a>\n")
            .append("  <a href=\"/admin/audit-logs\" class=\"btn btn-secondary\">&#128220; Security Audit Logs</a>\n")
            .append("</div>\n");

        // 3. Recent Audit Trail Preview
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\">\n")
            .append("    <h2>Recent System Activity &amp; Audit Trail</h2>\n")
            .append("    <a href=\"/admin/audit-logs\" class=\"btn btn-sm btn-secondary\">View Full Audit Trail</a>\n")
            .append("  </div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Log ID</th><th>User</th><th>Role</th><th>System Action</th><th>Timestamp</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (AuditLog log : recentLogs) {
            content.append("        <tr>\n")
                .append("          <td>#").append(log.getLogId()).append("</td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(log.getUserName() != null ? log.getUserName() : "System")).append("</strong></td>\n")
                .append("          <td><span class=\"badge badge-neutral\">").append(log.getUserRole() != null ? log.getUserRole() : "SYSTEM").append("</span></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(log.getAction())).append("</td>\n")
                .append("          <td>").append(log.getActionDate()).append("</td>\n")
                .append("        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Admin Dashboard", "/admin/dashboard", user, flashSuccess, flashError, content.toString());
    }

    public String renderUsers(User user, String flashSuccess, String flashError) {
        List<User> users = userService.getAllUsers();

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Master User Directory (").append(users.size()).append(" accounts)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>User ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Registration Date</th><th>Action</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (User u : users) {
            boolean active = "ACTIVE".equalsIgnoreCase(u.getStatus());
            content.append("        <tr>\n")
                .append("          <td>#").append(u.getUserId()).append("</td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(u.getName())).append("</strong></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(u.getEmail())).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(u.getPhone())).append("</td>\n")
                .append("          <td><span class=\"badge role-").append(u.getRole().toLowerCase()).append("\">").append(u.getRole()).append("</span></td>\n")
                .append("          <td><span class=\"badge ").append(active ? "badge-success" : "badge-danger").append("\">").append(u.getStatus()).append("</span></td>\n")
                .append("          <td>").append(u.getCreatedAt()).append("</td>\n")
                .append("          <td>\n");

            if (u.getUserId() != user.getUserId()) {
                content.append("            <form method=\"POST\" action=\"/admin/toggle-status\" style=\"display: inline;\">\n")
                    .append("              <input type=\"hidden\" name=\"userId\" value=\"").append(u.getUserId()).append("\">\n")
                    .append("              <input type=\"hidden\" name=\"newStatus\" value=\"").append(active ? "SUSPENDED" : "ACTIVE").append("\">\n")
                    .append("              <button type=\"submit\" class=\"btn btn-sm ").append(active ? "btn-danger" : "btn-success").append("\">").append(active ? "Suspend" : "Activate").append("</button>\n")
                    .append("            </form>\n");
            } else {
                content.append("            <span style=\"color: var(--text-muted); font-size: 0.8rem;\">Current Admin</span>\n");
            }

            content.append("          </td>\n        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Manage Users", "/admin/users", user, flashSuccess, flashError, content.toString());
    }

    public String renderLibrarians(User user, String flashSuccess, String flashError) {
        List<Librarian> librarians = userService.getAllLibrarians();

        StringBuilder content = new StringBuilder();

        // Add Librarian Form
        content.append("<div class=\"form-card\" style=\"max-width: 100%; margin-bottom: 2rem; padding: 1.5rem;\">\n")
            .append("  <h3 style=\"margin-bottom: 1rem; color: var(--primary); font-size: 1.1rem;\">+ Register Staff Librarian</h3>\n")
            .append("  <form method=\"POST\" action=\"/admin/add-librarian\" style=\"display: flex; gap: 1rem; flex-wrap: wrap; align-items: flex-end;\">\n")
            .append("    <div style=\"flex: 1 1 200px;\">\n")
            .append("      <label class=\"form-label\">Full Name *</label>\n")
            .append("      <input type=\"text\" name=\"name\" class=\"form-input\" required placeholder=\"Staff Name\">\n")
            .append("    </div>\n")
            .append("    <div style=\"flex: 1 1 200px;\">\n")
            .append("      <label class=\"form-label\">Staff Email *</label>\n")
            .append("      <input type=\"email\" name=\"email\" class=\"form-input\" required placeholder=\"staff@library.edu\">\n")
            .append("    </div>\n")
            .append("    <div style=\"flex: 1 1 150px;\">\n")
            .append("      <label class=\"form-label\">Employee ID *</label>\n")
            .append("      <input type=\"text\" name=\"employeeId\" class=\"form-input\" required placeholder=\"LIB-2026-09\">\n")
            .append("    </div>\n")
            .append("    <div style=\"flex: 1 1 150px;\">\n")
            .append("      <label class=\"form-label\">Phone</label>\n")
            .append("      <input type=\"text\" name=\"phone\" class=\"form-input\" placeholder=\"9876543210\">\n")
            .append("    </div>\n")
            .append("    <div style=\"flex: 1 1 150px;\">\n")
            .append("      <label class=\"form-label\">Password *</label>\n")
            .append("      <input type=\"password\" name=\"password\" class=\"form-input\" required placeholder=\"Min 6 chars\">\n")
            .append("    </div>\n")
            .append("    <div>\n")
            .append("      <button type=\"submit\" class=\"btn btn-primary\">Create Librarian</button>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        // Librarians Table
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Library Staff Members (").append(librarians.size()).append(" librarians)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Librarian ID</th><th>Employee ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Status</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (Librarian l : librarians) {
            content.append("        <tr>\n")
                .append("          <td>#").append(l.getLibrarianId()).append("</td>\n")
                .append("          <td><code>").append(HtmlRenderer.escape(l.getEmployeeId())).append("</code></td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(l.getName())).append("</strong></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(l.getEmail())).append("</td>\n")
                .append("          <td>").append(HtmlRenderer.escape(l.getPhone())).append("</td>\n")
                .append("          <td><span class=\"badge badge-success\">").append(l.getStatus()).append("</span></td>\n")
                .append("        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Manage Librarians", "/admin/librarians", user, flashSuccess, flashError, content.toString());
    }

    public String renderAuditLogs(User user, String flashSuccess, String flashError) {
        List<AuditLog> logs = userService.getRecentAuditLogs(100);

        StringBuilder content = new StringBuilder();
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\"><h2>Database Audit Trail (Last ").append(logs.size()).append(" recorded actions)</h2></div>\n")
            .append("  <div class=\"data-table-container\">\n")
            .append("    <table class=\"data-table\">\n")
            .append("      <thead><tr><th>Log ID</th><th>User</th><th>Role</th><th>Action Executed</th><th>Timestamp</th></tr></thead>\n")
            .append("      <tbody>\n");

        for (AuditLog l : logs) {
            content.append("        <tr>\n")
                .append("          <td>#").append(l.getLogId()).append("</td>\n")
                .append("          <td><strong>").append(HtmlRenderer.escape(l.getUserName() != null ? l.getUserName() : "System Routine")).append("</strong></td>\n")
                .append("          <td><span class=\"badge badge-neutral\">").append(l.getUserRole() != null ? l.getUserRole() : "SYSTEM").append("</span></td>\n")
                .append("          <td>").append(HtmlRenderer.escape(l.getAction())).append("</td>\n")
                .append("          <td>").append(l.getActionDate()).append("</td>\n")
                .append("        </tr>\n");
        }

        content.append("      </tbody>\n    </table>\n  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage("Security Audit Logs", "/admin/audit-logs", user, flashSuccess, flashError, content.toString());
    }
}
