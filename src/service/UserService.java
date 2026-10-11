package service;

import dao.AuditDAO;
import dao.LibrarianDAO;
import dao.StudentDAO;
import dao.UserDAO;
import model.AuditLog;
import model.Librarian;
import model.Student;
import model.User;
import util.PasswordUtils;

import java.util.List;

/**
 * Service managing User administration, Librarians, Students, and Audit logs.
 * Member 1 & 3: User Management and Administration.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final LibrarianDAO librarianDAO = new LibrarianDAO();
    private final AuditDAO auditDAO = new AuditDAO();

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public List<Librarian> getAllLibrarians() {
        return librarianDAO.getAllLibrarians();
    }

    public String createLibrarian(String name, String email, String password, String phone, String employeeId, int adminUserId) {
        if (name == null || name.trim().isEmpty()) return "Name is required.";
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "Valid email is required.";
        if (password == null || password.length() < 6) return "Password must be at least 6 characters.";
        if (employeeId == null || employeeId.trim().isEmpty()) return "Employee ID is required.";

        if (userDAO.findByEmail(email) != null) {
            return "Email is already registered.";
        }

        Librarian lib = new Librarian();
        lib.setName(name.trim());
        lib.setEmail(email.trim().toLowerCase());
        lib.setPassword(PasswordUtils.hashPassword(password));
        lib.setPhone(phone != null ? phone.trim() : "");
        lib.setEmployeeId(employeeId.trim().toUpperCase());
        lib.setStatus("ACTIVE");

        boolean ok = librarianDAO.createLibrarian(lib);
        if (ok) {
            auditDAO.logAction(adminUserId, "Created new librarian: " + lib.getName() + " (" + lib.getEmployeeId() + ")");
            return "SUCCESS";
        }
        return "Failed to create librarian.";
    }

    public boolean updateUserStatus(int userId, String status, int adminUserId) {
        boolean ok = userDAO.updateStatus(userId, status);
        if (ok) {
            auditDAO.logAction(adminUserId, "Updated status of user #" + userId + " to " + status);
        }
        return ok;
    }

    public List<AuditLog> getRecentAuditLogs(int limit) {
        return auditDAO.getRecentLogs(limit);
    }
}
