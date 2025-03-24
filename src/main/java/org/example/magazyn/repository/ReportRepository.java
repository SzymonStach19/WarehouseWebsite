package org.example.magazyn.repository;

import org.example.magazyn.entity.Report;
import org.example.magazyn.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReservation(Reservation reservation);
}