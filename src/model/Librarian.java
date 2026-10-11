package model;

import java.sql.Timestamp;

/**
 * Librarian model subclassing User.
 * Member 2: Librarian & Catalog Management Module.
 */
public class Librarian extends User {
    private int librarianId;
    private String employeeId;

    public Librarian() {
        super();
        setRole("LIBRARIAN");
    }

    public Librarian(int userId, String name, String email, String password, String phone, String status, Timestamp createdAt,
                     int librarianId, String employeeId) {
        super(userId, name, email, password, phone, "LIBRARIAN", status, createdAt);
        this.librarianId = librarianId;
        this.employeeId = employeeId;
    }

    public int getLibrarianId() { return librarianId; }
    public void setLibrarianId(int librarianId) { this.librarianId = librarianId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
