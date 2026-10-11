package service;

import dao.AuditDAO;
import dao.StudentDAO;
import dao.UserDAO;
import model.Student;
import model.User;
import util.PasswordUtils;

/**
 * Service handling Authentication, RBAC, and Student Registration.
 * Member 1: Authentication & User Management.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final AuditDAO auditDAO = new AuditDAO();

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        User user = userDAO.authenticate(email.trim(), password);
        if (user != null && "ACTIVE".equalsIgnoreCase(user.getStatus())) {
            auditDAO.logAction(user.getUserId(), "User logged in successfully (" + user.getRole() + ")");
            return user;
        }
        return null;
    }

    public String registerStudent(String name, String email, String password, String phone,
                                  String registerNumber, String department, int year, String section) {
        // Validation
        if (name == null || name.trim().isEmpty()) return "Full name is required.";
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "Valid email address is required.";
        if (password == null || password.length() < 6) return "Password must be at least 6 characters long.";
        if (registerNumber == null || registerNumber.trim().isEmpty()) return "Register number is required.";
        if (department == null || department.trim().isEmpty()) return "Department is required.";
        if (year < 1 || year > 5) return "Year of study must be between 1 and 5.";
        if (section == null || section.trim().isEmpty()) return "Section is required.";

        // Duplicate checks
        if (userDAO.findByEmail(email) != null) {
            return "Email is already registered. Please login or use a different email.";
        }
        if (studentDAO.findByRegisterNumber(registerNumber) != null) {
            return "Register Number (" + registerNumber + ") already exists in system records.";
        }

        Student student = new Student();
        student.setName(name.trim());
        student.setEmail(email.trim().toLowerCase());
        student.setPassword(PasswordUtils.hashPassword(password));
        student.setPhone(phone != null ? phone.trim() : "");
        student.setRole("STUDENT");
        student.setStatus("ACTIVE");
        student.setRegisterNumber(registerNumber.trim().toUpperCase());
        student.setDepartment(department.trim());
        student.setYear(year);
        student.setSection(section.trim().toUpperCase());

        boolean ok = studentDAO.registerStudent(student);
        return ok ? "SUCCESS" : "Registration failed due to a database transaction error.";
    }

    public Student getStudentByUserId(int userId) {
        return studentDAO.findByUserId(userId);
    }
}
