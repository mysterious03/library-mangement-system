package service;

import dao.FineDAO;
import model.Fine;

import java.util.List;

/**
 * Service managing Fine calculations and payments.
 * Member 4: Fines Module.
 */
public class FineService {

    private final FineDAO fineDAO = new FineDAO();

    public List<Fine> getStudentFines(int studentId) {
        return fineDAO.getFinesForStudent(studentId);
    }

    public List<Fine> getAllFines(String statusFilter) {
        return fineDAO.getAllFines(statusFilter);
    }

    public boolean markFineAsPaid(int fineId, int librarianUserId) {
        return fineDAO.markFineAsPaid(fineId, librarianUserId);
    }

    public double getTotalUnpaidFineForStudent(int studentId) {
        return fineDAO.getTotalUnpaidFineForStudent(studentId);
    }
}
