package service;

import dao.ReservationDAO;
import model.Reservation;

import java.util.List;

/**
 * Service managing Book Reservations and Waiting Queues.
 * Member 4: Reservations Module.
 */
public class ReservationService {

    private final ReservationDAO reservationDAO = new ReservationDAO();

    public ReservationDAO.ReservationResult reserveBook(int studentId, int bookId) {
        return reservationDAO.reserveBook(studentId, bookId);
    }

    public List<Reservation> getStudentReservations(int studentId) {
        return reservationDAO.getReservationsForStudent(studentId);
    }

    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    public boolean cancelReservation(int reservationId, int studentId) {
        return reservationDAO.cancelReservation(reservationId, studentId);
    }

    public boolean updateStatus(int reservationId, String newStatus) {
        return reservationDAO.updateStatus(reservationId, newStatus);
    }
}
