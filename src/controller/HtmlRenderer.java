package controller;

import model.User;

/**
 * Pure Java HTML5 Template Renderer.
 * Generates modern, clean, semantic HTML5 markup styled with CSS3.
 * Zero JavaScript frameworks - 100% compliant with project rules.
 */
public class HtmlRenderer {

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String getHeadStyles() {
        String inlineCss = "";
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("web", "css", "style.css");
            if (java.nio.file.Files.exists(p)) {
                inlineCss = java.nio.file.Files.readString(p);
            }
        } catch (Exception ignored) {}
        return "<link rel=\"stylesheet\" href=\"/css/style.css?v=" + System.currentTimeMillis() + "\">\n" +
               "<style id=\"slms-core-stylesheet\">\n" + inlineCss + "\n</style>\n";
    }

    public static String renderPublicPage(String title, String activeNav, String content) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
            .append("<meta charset=\"UTF-8\">\n")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            .append("<title>").append(escape(title)).append(" - Smart Library</title>\n")
            .append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n")
            .append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n")
            .append("<link href=\"https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=JetBrains+Mono:wght@400;500;600&family=Playfair+Display:ital,wght@0,500;0,600;0,700;0,800;1,400;1,600;1,700&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap\" rel=\"stylesheet\">\n")
            .append(getHeadStyles())
            .append("</head>\n<body class=\"public-body\">\n")
            .append("<div class=\"public-nav-wrapper\">\n")
            .append("  <header class=\"public-nav\">\n")
            .append("    <a href=\"/\" class=\"brand-logo\">\n")
            .append("      <span class=\"logo-badge\">SLMS</span>\n")
            .append("      <span class=\"brand-title-text\">Campus Central Library</span>\n")
            .append("    </a>\n")
            .append("    <nav class=\"nav-links\">\n")
            .append("      <a href=\"/\" class=\"nav-link ").append("home".equals(activeNav) ? "active" : "").append("\">Home</a>\n")
            .append("      <a href=\"/about\" class=\"nav-link ").append("about".equals(activeNav) ? "active" : "").append("\">About &amp; Architecture</a>\n")
            .append("      <a href=\"/login\" class=\"btn btn-pill btn-secondary ").append("login".equals(activeNav) ? "active" : "").append("\">Sign In</a>\n")
            .append("      <a href=\"/register\" class=\"btn btn-pill btn-primary ").append("register".equals(activeNav) ? "active" : "").append("\">Student Register</a>\n")
            .append("    </nav>\n")
            .append("  </header>\n")
            .append("</div>\n")
            .append("<main>\n").append(content).append("\n</main>\n")
            .append("</body>\n</html>");
        return html.toString();
    }

    public static String renderAuthPage(String title, String content) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
            .append("<meta charset=\"UTF-8\">\n")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            .append("<title>").append(escape(title)).append(" - Smart Library</title>\n")
            .append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n")
            .append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n")
            .append("<link href=\"https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=JetBrains+Mono:wght@400;500;600&family=Playfair+Display:ital,wght@0,500;0,600;0,700;0,800;1,400;1,600;1,700&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap\" rel=\"stylesheet\">\n")
            .append(getHeadStyles())
            .append("</head>\n<body class=\"auth-body\">\n")
            .append(content)
            .append("\n</body>\n</html>");
        return html.toString();
    }

    public static String renderAppPage(String title, String activeRoute, User user, String flashSuccess, String flashError, String bodyContent) {
        StringBuilder html = new StringBuilder();
        String role = user != null ? user.getRole().toUpperCase() : "STUDENT";

        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
            .append("<meta charset=\"UTF-8\">\n")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
            .append("<title>").append(escape(title)).append(" - Smart Library</title>\n")
            .append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n")
            .append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n")
            .append("<link href=\"https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=JetBrains+Mono:wght@400;500;600&family=Playfair+Display:ital,wght@0,500;0,600;0,700;0,800;1,400;1,600;1,700&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap\" rel=\"stylesheet\">\n")
            .append(getHeadStyles())
            .append("</head>\n<body>\n")
            .append("<div class=\"app-container\">\n")
            .append("  <!-- Sidebar Navigation -->\n")
            .append("  <aside class=\"sidebar\">\n")
            .append("    <div class=\"sidebar-header\">\n")
            .append("      <span class=\"logo-badge\">SLMS</span>\n")
            .append("      <span class=\"sidebar-title\">Smart Library</span>\n")
            .append("    </div>\n")
            .append("    <div class=\"user-badge\">\n")
            .append("      <div class=\"user-name\">").append(escape(user != null ? user.getName() : "Guest")).append("</div>\n")
            .append("      <span class=\"user-role role-").append(role.toLowerCase()).append("\">").append(role).append("</span>\n")
            .append("    </div>\n")
            .append("    <nav class=\"sidebar-nav\">\n");

        if ("STUDENT".equals(role)) {
            html.append("      <div class=\"nav-group-title\">Student Menu</div>\n")
                .append(link("/student/dashboard", "Dashboard", activeRoute.equals("/student/dashboard")))
                .append(link("/student/books", "Search Books", activeRoute.equals("/student/books")))
                .append(link("/student/borrowed", "My Borrowed Books", activeRoute.equals("/student/borrowed")))
                .append(link("/student/reservations", "My Reservations", activeRoute.equals("/student/reservations")))
                .append(link("/student/fines", "My Fines", activeRoute.equals("/student/fines")))
                .append(link("/student/history", "Borrowing History", activeRoute.equals("/student/history")))
                .append(link("/student/profile", "My Profile", activeRoute.equals("/student/profile")));
        } else if ("LIBRARIAN".equals(role)) {
            html.append("      <div class=\"nav-group-title\">Circulation Desk</div>\n")
                .append(link("/librarian/dashboard", "Dashboard", activeRoute.equals("/librarian/dashboard")))
                .append(link("/librarian/issue", "Issue Book", activeRoute.equals("/librarian/issue")))
                .append(link("/librarian/return", "Return Book", activeRoute.equals("/librarian/return")))
                .append(link("/librarian/overdue", "Overdue Books", activeRoute.equals("/librarian/overdue")))
                .append(link("/librarian/fines", "Fine Management", activeRoute.equals("/librarian/fines")))
                .append(link("/librarian/reservations", "Reservations Queue", activeRoute.equals("/librarian/reservations")))
                .append("      <div class=\"nav-group-title\">Catalog & Students</div>\n")
                .append(link("/librarian/books", "Manage Books", activeRoute.equals("/librarian/books")))
                .append(link("/librarian/add-book", "Add New Book", activeRoute.equals("/librarian/add-book")))
                .append(link("/librarian/copies", "Physical Book Copies", activeRoute.equals("/librarian/copies")))
                .append(link("/librarian/students", "Manage Students", activeRoute.equals("/librarian/students")))
                .append(link("/librarian/reports", "Analytical Reports", activeRoute.equals("/librarian/reports")));
        } else if ("ADMIN".equals(role)) {
            html.append("      <div class=\"nav-group-title\">Administration</div>\n")
                .append(link("/admin/dashboard", "Admin Dashboard", activeRoute.equals("/admin/dashboard")))
                .append(link("/admin/users", "Manage Users", activeRoute.equals("/admin/users")))
                .append(link("/admin/librarians", "Manage Librarians", activeRoute.equals("/admin/librarians")))
                .append(link("/admin/students", "Manage Students", activeRoute.equals("/admin/students")))
                .append(link("/admin/books", "Book Catalog Overview", activeRoute.equals("/admin/books")))
                .append(link("/admin/reports", "System SQL Reports", activeRoute.equals("/admin/reports")))
                .append(link("/admin/audit-logs", "Security Audit Logs", activeRoute.equals("/admin/audit-logs")));
        }

        html.append("    </nav>\n")
            .append("    <div class=\"sidebar-footer\">\n")
            .append("      <a href=\"/logout\" class=\"btn-logout\">Sign Out</a>\n")
            .append("    </div>\n")
            .append("  </aside>\n")
            .append("  <!-- Main Content Wrapper -->\n")
            .append("  <div class=\"main-wrapper\">\n")
            .append("    <header class=\"top-bar\">\n")
            .append("      <h1 class=\"page-title\">").append(escape(title)).append("</h1>\n")
            .append("      <div class=\"top-actions\">\n")
            .append("        <span style=\"font-size: 0.85rem; color: var(--text-muted);\">Logged in as <strong>").append(escape(user != null ? user.getEmail() : "")).append("</strong></span>\n")
            .append("      </div>\n")
            .append("    </header>\n")
            .append("    <main class=\"content-body\">\n");

        if (flashSuccess != null && !flashSuccess.isEmpty()) {
            html.append("      <div class=\"alert alert-success\">\n")
                .append("        <span>&#10003; ").append(escape(flashSuccess)).append("</span>\n")
                .append("      </div>\n");
        }
        if (flashError != null && !flashError.isEmpty()) {
            html.append("      <div class=\"alert alert-danger\">\n")
                .append("        <span>&#9888; ").append(escape(flashError)).append("</span>\n")
                .append("      </div>\n");
        }

        html.append(bodyContent)
            .append("    </main>\n")
            .append("  </div>\n")
            .append("</div>\n")
            .append("</body>\n</html>");

        return html.toString();
    }

    private static String link(String href, String text, boolean active) {
        return "      <a href=\"" + href + "\" class=\"sidebar-link " + (active ? "active" : "") + "\">" + escape(text) + "</a>\n";
    }
}
