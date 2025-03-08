package org.example.magazyn.service;

import org.example.magazyn.dto.ReservationDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.Reservation;
import org.example.magazyn.entity.User;

import java.util.List;

public interface ReservationService {
    Reservation createReservation(Product product, User user, int quantity);
    List<ReservationDto> getUserReservations(User user);
    List<ReservationDto> getAllReservations();
    void cancelReservation(Long reservationId, User currentUser);
    ReservationDto updateReservationStatus(Long reservationId, Reservation.ReservationStatus status);

}
