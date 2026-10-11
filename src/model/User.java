package model;

import java.sql.Timestamp;

/**
 * Base User class demonstrating OOP Encapsulation and Inheritance.
 * Member 1: Authentication & User Management.
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;     // 'STUDENT', 'LIBRARIAN', 'ADMIN'
    private String status;   // 'ACTIVE', 'INACTIVE', 'SUSPENDED'
    private Timestamp createdAt;

    public User() {}

    public User(int userId, String name, String email, String password, String phone, String role, String status, Timestamp createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isStudent() { return "STUDENT".equalsIgnoreCase(this.role); }
    public boolean isLibrarian() { return "LIBRARIAN".equalsIgnoreCase(this.role); }
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(this.role); }
}
