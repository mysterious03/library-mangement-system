package service;

import dao.IssueDAO;
import model.Issue;

import java.util.List;

/**
 * Service managing Issue, Loan and Return Circulation workflows.
 * Member 2: Issue & Return Module.
 */
public class IssueService {

    private final IssueDAO issueDAO = new IssueDAO();

    public IssueDAO.IssueResult issueBook(int studentId, int bookId, int librarianUserId) {
        return issueDAO.issueBook(studentId, bookId, librarianUserId);
    }

    public IssueDAO.ReturnResult returnBook(int issueId, int librarianUserId) {
        return issueDAO.returnBook(issueId, librarianUserId);
    }

    public List<Issue> getActiveIssuesForStudent(int studentId) {
        return issueDAO.getActiveIssuesForStudent(studentId);
    }

    public List<Issue> getBorrowingHistoryForStudent(int studentId) {
        return issueDAO.getBorrowingHistoryForStudent(studentId);
    }

    public List<Issue> getAllActiveIssues() {
        return issueDAO.getAllActiveIssues();
    }

    public List<Issue> getOverdueIssues() {
        return issueDAO.getOverdueIssues();
    }

    public List<Issue> getRecentIssues(int limit) {
        return issueDAO.getRecentIssues(limit);
    }
}
