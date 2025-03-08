package org.example.magazyn.repository;

import org.example.magazyn.entity.Reservation;
import org.example.magazyn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByProductIdAndStatus(Long productId, Reservation.ReservationStatus status);
}