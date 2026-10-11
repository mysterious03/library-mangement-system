package service;

import dao.ReportDAO;

import java.util.List;
import java.util.Map;

/**
 * Service managing Analytical Reports and Statistics.
 * Member 4: Reports & Analytics Module.
 */
public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public Map<String, Object> getSystemStatistics() {
        return reportDAO.getSystemStatistics();
    }

    public List<Map<String, String>> getReport(int reportId) {
        switch (reportId) {
            case 1: return reportDAO.getMostBorrowedBooks();
            case 2: return reportDAO.getMostActiveStudents();
            case 3: return reportDAO.getOverdueBooksReport();
            case 4: return reportDAO.getUnpaidFinesReport();
            case 5: return reportDAO.getBooksByCategory();
            case 6: return reportDAO.getAvailableBooksReport();
            case 7: return reportDAO.getIssuedBooksReport();
            case 8: return reportDAO.getReservationStatsReport();
            case 9: return reportDAO.getMonthlyBorrowingReport();
            case 10: return reportDAO.getFineCollectionReport();
            default: return reportDAO.getMostBorrowedBooks();
        }
    }

    public String getReportTitle(int reportId) {
        switch (reportId) {
            case 1: return "1. Most Borrowed Books";
            case 2: return "2. Most Active Students";
            case 3: return "3. Overdue Books with Calculated Fines";
            case 4: return "4. Students with Unpaid Fines";
            case 5: return "5. Books Distribution by Category";
            case 6: return "6. Available Books (Physical Shelf Audit)";
            case 7: return "7. Currently Issued Books Status";
            case 8: return "8. Book Reservation Queues & Statistics";
            case 9: return "9. Monthly Borrowing Trends";
            case 10: return "10. Fine Collection & Recovery Summary";
            default: return "Library Analytical Report";
        }
    }
}
