package model;

import java.sql.Timestamp;

/**
 * Student model subclassing User, demonstrating Inheritance and Polymorphism.
 * Member 1: Authentication & Student Profile Module.
 */
public class Student extends User {
    private int studentId;
    private String registerNumber;
    private String department;
    private int year;
    private String section;

    public Student() {
        super();
        setRole("STUDENT");
    }

    public Student(int userId, String name, String email, String password, String phone, String status, Timestamp createdAt,
                   int studentId, String registerNumber, String department, int year, String section) {
        super(userId, name, email, password, phone, "STUDENT", status, createdAt);
        this.studentId = studentId;
        this.registerNumber = registerNumber;
        this.department = department;
        this.year = year;
        this.section = section;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
