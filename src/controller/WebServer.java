package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.IssueDAO;
import dao.ReservationDAO;
import dao.StudentDAO;
import model.Student;
import model.User;
import service.AuthService;
import service.BookService;
import service.FineService;
import service.IssueService;
import service.ReservationService;
import service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Pure Java SE Embedded Web Server using com.sun.net.httpserver.HttpServer.
 * Zero external frameworks, zero third-party web libraries.
 * Handles HTTP requests, Sessions, RBAC protection, and JDBC Controllers.
 */
public class WebServer {

    private final int port;
    private HttpServer server;

    private final PublicController publicCtrl = new PublicController();
    private final StudentController studentCtrl = new StudentController();
    private final LibrarianController librarianCtrl = new LibrarianController();
    private final AdminController adminCtrl = new AdminController();
    private final ReportController reportCtrl = new ReportController();

    private final AuthService authService = new AuthService();
    private final BookService bookService = new BookService();
    private final IssueService issueService = new IssueService();
    private final ReservationService reservationService = new ReservationService();
    private final FineService fineService = new FineService();
    private final UserService userService = new UserService();
    private final StudentDAO studentDAO = new StudentDAO();

    public WebServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Static CSS & Assets handler
        server.createContext("/css/", new StaticFileHandler());
        server.createContext("/style.css", new StaticFileHandler());
        server.createContext("/images/", new StaticFileHandler());

        // Master Request Dispatcher
        server.createContext("/", new MasterRouter());

        server.setExecutor(null); // default single/multi-thread executor
        server.start();
        System.out.println("==================================================================");
        System.out.println(" Smart Library Management System is running live!");
        System.out.println(" Access URL: http://localhost:" + port);
        System.out.println("==================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class MasterRouter implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();
            String sessionToken = getSessionToken(exchange);
            User user = SessionManager.getUser(sessionToken);

            String flashSuccess = SessionManager.getAndClearFlashSuccess(sessionToken);
            String flashError = SessionManager.getAndClearFlashError(sessionToken);

            try {
                // Public Routes
                if ("/".equals(path)) {
                    sendHtml(exchange, publicCtrl.renderHome());
                    return;
                }
                if ("/about".equals(path)) {
                    sendHtml(exchange, publicCtrl.renderAbout());
                    return;
                }
                if ("/login".equals(path)) {
                    if ("POST".equals(method)) {
                        Map<String, String> form = parseFormData(exchange);
                        String email = form.get("email");
                        String password = form.get("password");
                        User loggedIn = authService.login(email, password);
                        if (loggedIn != null) {
                            String newToken = SessionManager.createSession(loggedIn);
                            setCookie(exchange, "session_id", newToken);
                            if (loggedIn.isAdmin()) {
                                redirect(exchange, "/admin/dashboard");
                            } else if (loggedIn.isLibrarian()) {
                                redirect(exchange, "/librarian/dashboard");
                            } else {
                                redirect(exchange, "/student/dashboard");
                            }
                        } else {
                            sendHtml(exchange, publicCtrl.renderLogin(null, "Invalid email or password. Please try again."));
                        }
                    } else {
                        if (user != null) {
                            redirectBasedOnRole(exchange, user);
                            return;
                        }
                        sendHtml(exchange, publicCtrl.renderLogin(flashSuccess, flashError));
                    }
                    return;
                }
                if ("/register".equals(path)) {
                    if ("POST".equals(method)) {
                        Map<String, String> form = parseFormData(exchange);
                        String name = form.get("name");
                        String email = form.get("email");
                        String phone = form.get("phone");
                        String password = form.get("password");
                        String regNo = form.get("registerNumber");
                        String dept = form.get("department");
                        int year = 1;
                        try { year = Integer.parseInt(form.get("year")); } catch (Exception ignored) {}
                        String section = form.get("section");

                        String result = authService.registerStudent(name, email, password, phone, regNo, dept, year, section);
                        if ("SUCCESS".equals(result)) {
                            SessionManager.setFlashSuccess(sessionToken, "Registration successful! You may now sign in.");
                            redirect(exchange, "/login");
                        } else {
                            sendHtml(exchange, publicCtrl.renderRegister(result));
                        }
                    } else {
                        sendHtml(exchange, publicCtrl.renderRegister(flashError));
                    }
                    return;
                }
                if ("/logout".equals(path)) {
                    SessionManager.destroySession(sessionToken);
                    clearCookie(exchange, "session_id");
                    redirect(exchange, "/login");
                    return;
                }

                // Authentication Check for Protected Routes
                if (user == null) {
                    redirect(exchange, "/login");
                    return;
                }

                // Student Routes
                if (path.startsWith("/student/")) {
                    if (!user.isStudent()) {
                        redirectBasedOnRole(exchange, user);
                        return;
                    }
                    handleStudentRoutes(exchange, path, method, user, sessionToken, flashSuccess, flashError);
                    return;
                }

                // Librarian Routes
                if (path.startsWith("/librarian/")) {
                    if (!user.isLibrarian()) {
                        redirectBasedOnRole(exchange, user);
                        return;
                    }
                    handleLibrarianRoutes(exchange, path, method, user, sessionToken, flashSuccess, flashError);
                    return;
                }

                // Admin Routes
                if (path.startsWith("/admin/")) {
                    if (!user.isAdmin()) {
                        redirectBasedOnRole(exchange, user);
                        return;
                    }
                    handleAdminRoutes(exchange, path, method, user, sessionToken, flashSuccess, flashError);
                    return;
                }

                // 404 Fallback
                redirectBasedOnRole(exchange, user);

            } catch (Exception e) {
                System.err.println("Request handling error on " + path + ": " + e.getMessage());
                e.printStackTrace();
                sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        }
    }

    private void handleStudentRoutes(HttpExchange exchange, String path, String method, User user,
                                    String sessionToken, String flashSuccess, String flashError) throws IOException {
        Map<String, String> query = parseQueryParams(exchange);

        if ("/student/dashboard".equals(path)) {
            sendHtml(exchange, studentCtrl.renderDashboard(user, flashSuccess, flashError));
        } else if ("/student/books".equals(path)) {
            String q = query.get("q");
            Integer catId = parseInt(query.get("category"));
            Integer authorId = parseInt(query.get("author"));
            String avail = query.get("avail");
            sendHtml(exchange, studentCtrl.renderSearchBooks(user, q, catId, authorId, avail, flashSuccess, flashError));
        } else if ("/student/book-details".equals(path)) {
            int bookId = parseInt(query.get("id")) != null ? parseInt(query.get("id")) : 1;
            sendHtml(exchange, studentCtrl.renderBookDetails(user, bookId, flashSuccess, flashError));
        } else if ("/student/borrowed".equals(path)) {
            sendHtml(exchange, studentCtrl.renderBorrowedBooks(user, flashSuccess, flashError));
        } else if ("/student/reservations".equals(path)) {
            sendHtml(exchange, studentCtrl.renderReservations(user, flashSuccess, flashError));
        } else if ("/student/fines".equals(path)) {
            sendHtml(exchange, studentCtrl.renderFines(user, flashSuccess, flashError));
        } else if ("/student/history".equals(path)) {
            sendHtml(exchange, studentCtrl.renderHistory(user, flashSuccess, flashError));
        } else if ("/student/profile".equals(path)) {
            sendHtml(exchange, studentCtrl.renderProfile(user, flashSuccess, flashError));
        } else if ("/student/borrow".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int bookId = parseInt(form.get("bookId")) != null ? parseInt(form.get("bookId")) : -1;
            Student student = authService.getStudentByUserId(user.getUserId());
            int studentId = student != null ? student.getStudentId() : -1;

            IssueDAO.IssueResult res = issueService.issueBook(studentId, bookId, user.getUserId());
            if (res.success) {
                SessionManager.setFlashSuccess(sessionToken, res.message);
                redirect(exchange, "/student/borrowed");
            } else {
                SessionManager.setFlashError(sessionToken, res.message);
                redirect(exchange, "/student/books");
            }
        } else if ("/student/return".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int issueId = parseInt(form.get("issueId")) != null ? parseInt(form.get("issueId")) : -1;
            IssueDAO.ReturnResult res = issueService.returnBook(issueId, user.getUserId());
            if (res.success) {
                String msg = res.message;
                if (res.overdueDays > 0) {
                    msg += " (Overdue: " + res.overdueDays + " days. Fine of INR " + String.format("%.2f", res.fineAmount) + " charged)";
                }
                SessionManager.setFlashSuccess(sessionToken, msg);
            } else {
                SessionManager.setFlashError(sessionToken, res.message);
            }
            redirect(exchange, "/student/borrowed");
        } else if ("/student/reserve".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int bookId = parseInt(form.get("bookId")) != null ? parseInt(form.get("bookId")) : -1;
            Student student = authService.getStudentByUserId(user.getUserId());
            int studentId = student != null ? student.getStudentId() : -1;

            ReservationDAO.ReservationResult res = reservationService.reserveBook(studentId, bookId);
            if (res.success) {
                SessionManager.setFlashSuccess(sessionToken, res.message);
                redirect(exchange, "/student/reservations");
            } else {
                SessionManager.setFlashError(sessionToken, res.message);
                redirect(exchange, "/student/books");
            }
        } else if ("/student/cancel-reservation".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int resId = parseInt(form.get("resId")) != null ? parseInt(form.get("resId")) : -1;
            Student student = authService.getStudentByUserId(user.getUserId());
            int studentId = student != null ? student.getStudentId() : -1;

            boolean ok = reservationService.cancelReservation(resId, studentId);
            if (ok) {
                SessionManager.setFlashSuccess(sessionToken, "Reservation cancelled successfully.");
            } else {
                SessionManager.setFlashError(sessionToken, "Failed to cancel reservation.");
            }
            redirect(exchange, "/student/reservations");
        } else {
            redirect(exchange, "/student/dashboard");
        }
    }

    private void handleLibrarianRoutes(HttpExchange exchange, String path, String method, User user,
                                      String sessionToken, String flashSuccess, String flashError) throws IOException {
        Map<String, String> query = parseQueryParams(exchange);

        if ("/librarian/dashboard".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderDashboard(user, flashSuccess, flashError));
        } else if ("/librarian/books".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderManageBooks(user, query.get("q"), flashSuccess, flashError));
        } else if ("/librarian/add-book".equals(path)) {
            if ("POST".equals(method)) {
                Map<String, String> form = parseFormData(exchange);
                String title = form.get("title");
                String isbn = form.get("isbn");
                int authorId = parseInt(form.get("authorId")) != null ? parseInt(form.get("authorId")) : 0;
                int categoryId = parseInt(form.get("categoryId")) != null ? parseInt(form.get("categoryId")) : 0;
                int publisherId = parseInt(form.get("publisherId")) != null ? parseInt(form.get("publisherId")) : 0;
                int year = parseInt(form.get("publicationYear")) != null ? parseInt(form.get("publicationYear")) : 2022;
                String edition = form.get("edition");
                String language = form.get("language");
                String desc = form.get("description");
                int initialCopies = parseInt(form.get("initialCopies")) != null ? parseInt(form.get("initialCopies")) : 1;
                String shelf = form.get("initialShelf");

                String res = bookService.addBook(isbn, title, authorId, categoryId, publisherId, year, edition, language, desc, initialCopies, shelf, user.getUserId());
                if ("SUCCESS".equals(res)) {
                    SessionManager.setFlashSuccess(sessionToken, "Book added with " + initialCopies + " barcode copies successfully!");
                    redirect(exchange, "/librarian/books");
                } else {
                    sendHtml(exchange, librarianCtrl.renderAddBookForm(user, null, res));
                }
            } else {
                sendHtml(exchange, librarianCtrl.renderAddBookForm(user, flashSuccess, flashError));
            }
        } else if ("/librarian/copies".equals(path)) {
            Integer bookId = parseInt(query.get("bookId"));
            sendHtml(exchange, librarianCtrl.renderManageCopies(user, bookId, flashSuccess, flashError));
        } else if ("/librarian/add-copy".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int bookId = parseInt(form.get("bookId")) != null ? parseInt(form.get("bookId")) : -1;
            String accNo = form.get("accessionNumber");
            String shelf = form.get("shelfLocation");

            boolean ok = bookService.addCopy(bookId, accNo, shelf);
            if (ok) {
                SessionManager.setFlashSuccess(sessionToken, "New copy " + accNo + " registered successfully!");
            } else {
                SessionManager.setFlashError(sessionToken, "Failed to register copy (duplicate accession number?).");
            }
            redirect(exchange, "/librarian/copies?bookId=" + bookId);
        } else if ("/librarian/students".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderStudents(user, flashSuccess, flashError));
        } else if ("/librarian/issue".equals(path)) {
            if ("POST".equals(method)) {
                Map<String, String> form = parseFormData(exchange);
                int studentId = parseInt(form.get("studentId")) != null ? parseInt(form.get("studentId")) : -1;
                int bookId = parseInt(form.get("bookId")) != null ? parseInt(form.get("bookId")) : -1;

                IssueDAO.IssueResult res = issueService.issueBook(studentId, bookId, user.getUserId());
                if (res.success) {
                    SessionManager.setFlashSuccess(sessionToken, res.message);
                    redirect(exchange, "/librarian/dashboard");
                } else {
                    SessionManager.setFlashError(sessionToken, res.message);
                    sendHtml(exchange, librarianCtrl.renderIssuePage(user, null, res.message));
                }
            } else {
                sendHtml(exchange, librarianCtrl.renderIssuePage(user, flashSuccess, flashError));
            }
        } else if ("/librarian/return".equals(path)) {
            if ("POST".equals(method)) {
                Map<String, String> form = parseFormData(exchange);
                int issueId = parseInt(form.get("issueId")) != null ? parseInt(form.get("issueId")) : -1;

                IssueDAO.ReturnResult res = issueService.returnBook(issueId, user.getUserId());
                if (res.success) {
                    SessionManager.setFlashSuccess(sessionToken, res.message);
                } else {
                    SessionManager.setFlashError(sessionToken, res.message);
                }
                redirect(exchange, "/librarian/dashboard");
            } else {
                sendHtml(exchange, librarianCtrl.renderReturnPage(user, flashSuccess, flashError));
            }
        } else if ("/librarian/fines".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderFines(user, query.get("status"), flashSuccess, flashError));
        } else if ("/librarian/pay-fine".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int fineId = parseInt(form.get("fineId")) != null ? parseInt(form.get("fineId")) : -1;
            boolean ok = fineService.markFineAsPaid(fineId, user.getUserId());
            if (ok) {
                SessionManager.setFlashSuccess(sessionToken, "Fine ID #" + fineId + " cleared and marked as PAID.");
            } else {
                SessionManager.setFlashError(sessionToken, "Failed to clear fine.");
            }
            redirect(exchange, "/librarian/fines");
        } else if ("/librarian/overdue".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderOverduePage(user, flashSuccess, flashError));
        } else if ("/librarian/reservations".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderReservations(user, flashSuccess, flashError));
        } else if ("/librarian/reports".equals(path)) {
            int reportId = parseInt(query.get("id")) != null ? parseInt(query.get("id")) : 1;
            sendHtml(exchange, reportCtrl.renderReports(user, reportId, "/librarian/reports"));
        } else {
            redirect(exchange, "/librarian/dashboard");
        }
    }

    private void handleAdminRoutes(HttpExchange exchange, String path, String method, User user,
                                  String sessionToken, String flashSuccess, String flashError) throws IOException {
        Map<String, String> query = parseQueryParams(exchange);

        if ("/admin/dashboard".equals(path)) {
            sendHtml(exchange, adminCtrl.renderDashboard(user, flashSuccess, flashError));
        } else if ("/admin/users".equals(path)) {
            sendHtml(exchange, adminCtrl.renderUsers(user, flashSuccess, flashError));
        } else if ("/admin/toggle-status".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            int targetUserId = parseInt(form.get("userId")) != null ? parseInt(form.get("userId")) : -1;
            String newStatus = form.get("newStatus");

            boolean ok = userService.updateUserStatus(targetUserId, newStatus, user.getUserId());
            if (ok) {
                SessionManager.setFlashSuccess(sessionToken, "User #" + targetUserId + " updated to " + newStatus);
            } else {
                SessionManager.setFlashError(sessionToken, "Failed to update user status.");
            }
            redirect(exchange, "/admin/users");
        } else if ("/admin/librarians".equals(path)) {
            sendHtml(exchange, adminCtrl.renderLibrarians(user, flashSuccess, flashError));
        } else if ("/admin/add-librarian".equals(path) && "POST".equals(method)) {
            Map<String, String> form = parseFormData(exchange);
            String name = form.get("name");
            String email = form.get("email");
            String password = form.get("password");
            String phone = form.get("phone");
            String empId = form.get("employeeId");

            String res = userService.createLibrarian(name, email, password, phone, empId, user.getUserId());
            if ("SUCCESS".equals(res)) {
                SessionManager.setFlashSuccess(sessionToken, "New staff librarian registered successfully!");
            } else {
                SessionManager.setFlashError(sessionToken, res);
            }
            redirect(exchange, "/admin/librarians");
        } else if ("/admin/students".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderStudents(user, flashSuccess, flashError));
        } else if ("/admin/books".equals(path)) {
            sendHtml(exchange, librarianCtrl.renderManageBooks(user, query.get("q"), flashSuccess, flashError));
        } else if ("/admin/reports".equals(path)) {
            int reportId = parseInt(query.get("id")) != null ? parseInt(query.get("id")) : 1;
            sendHtml(exchange, reportCtrl.renderReports(user, reportId, "/admin/reports"));
        } else if ("/admin/audit-logs".equals(path)) {
            sendHtml(exchange, adminCtrl.renderAuditLogs(user, flashSuccess, flashError));
        } else {
            redirect(exchange, "/admin/dashboard");
        }
    }

    private void redirectBasedOnRole(HttpExchange exchange, User user) throws IOException {
        if (user.isAdmin()) {
            redirect(exchange, "/admin/dashboard");
        } else if (user.isLibrarian()) {
            redirect(exchange, "/librarian/dashboard");
        } else {
            redirect(exchange, "/student/dashboard");
        }
    }

    // Static Asset Handler for CSS, Images, and static web resources
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/style.css".equals(path)) {
                path = "/css/style.css";
            }
            File file = new File("web" + path);
            if (file.exists() && !file.isDirectory()) {
                String contentType = "application/octet-stream";
                if (path.endsWith(".css")) {
                    contentType = "text/css; charset=UTF-8";
                } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (path.endsWith(".png")) {
                    contentType = "image/png";
                } else if (path.endsWith(".webp")) {
                    contentType = "image/webp";
                } else if (path.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                }
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
                exchange.getResponseHeaders().set("Pragma", "no-cache");
                exchange.getResponseHeaders().set("Expires", "0");
                exchange.sendResponseHeaders(200, file.length());
                try (InputStream is = new FileInputStream(file);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                }
            } else {
                sendError(exchange, 404, "Asset Not Found: " + path);
            }
        }
    }

    // Helper utilities for HTTP Protocol handling
    private static void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendError(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] bytes = ("<h1>" + code + "</h1><p>" + msg + "</p>").getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void redirect(HttpExchange exchange, String target) throws IOException {
        exchange.getResponseHeaders().set("Location", target);
        exchange.sendResponseHeaders(302, -1);
    }

    private static String getSessionToken(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String c : cookies) {
                String[] pair = c.trim().split("=");
                if (pair.length == 2 && "session_id".equals(pair[0].trim())) {
                    return pair[1].trim();
                }
            }
        }
        return null;
    }

    private static void setCookie(HttpExchange exchange, String name, String value) {
        exchange.getResponseHeaders().add("Set-Cookie", name + "=" + value + "; Path=/; HttpOnly; SameSite=Lax");
    }

    private static void clearCookie(HttpExchange exchange, String name) {
        exchange.getResponseHeaders().add("Set-Cookie", name + "=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly");
    }

    private static Map<String, String> parseQueryParams(HttpExchange exchange) {
        Map<String, String> map = new HashMap<>();
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery != null) {
            String[] pairs = rawQuery.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                } else if (kv.length == 1) {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
                }
            }
        }
        return map;
    }

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (!body.isEmpty()) {
                String[] pairs = body.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                    } else if (kv.length == 1) {
                        map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
                    }
                }
            }
        }
        return map;
    }

    private static Integer parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
