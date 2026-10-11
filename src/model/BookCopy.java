package model;

public class BookCopy {
    private int copyId;
    private int bookId;
    private String accessionNumber;
    private String shelfLocation;
    private String status; // 'AVAILABLE', 'ISSUED', 'RESERVED', 'LOST', 'MAINTENANCE'
    private String bookTitle;

    public BookCopy() {}

    public BookCopy(int copyId, int bookId, String accessionNumber, String shelfLocation, String status) {
        this.copyId = copyId;
        this.bookId = bookId;
        this.accessionNumber = accessionNumber;
        this.shelfLocation = shelfLocation;
        this.status = status;
    }

    public int getCopyId() { return copyId; }
    public void setCopyId(int copyId) { this.copyId = copyId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getAccessionNumber() { return accessionNumber; }
    public void setAccessionNumber(String accessionNumber) { this.accessionNumber = accessionNumber; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
}
