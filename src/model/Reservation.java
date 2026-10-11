package model;

import java.sql.Timestamp;

public class Reservation {
    private int reservationId;
    private int studentId;
    private int bookId;
    private Timestamp reservationDate;
    private String status; // 'PENDING', 'APPROVED', 'COMPLETED', 'CANCELLED'
    private int queueNumber;

    // Display joins
    private String studentName;
    private String registerNumber;
    private String bookTitle;
    private String isbn;

    public Reservation() {}

    public Reservation(int reservationId, int studentId, int bookId, Timestamp reservationDate, String status, int queueNumber) {
        this.reservationId = reservationId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
        this.status = status;
        this.queueNumber = queueNumber;
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public Timestamp getReservationDate() { return reservationDate; }
    public void setReservationDate(Timestamp reservationDate) { this.reservationDate = reservationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getQueueNumber() { return queueNumber; }
    public void setQueueNumber(int queueNumber) { this.queueNumber = queueNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegisterNumber() { return registerNumber; }
    public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
