package org.example.magazyn.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.dto.ReservationDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.Reservation;
import org.example.magazyn.entity.User;
import org.example.magazyn.repository.ProductRepository;
import org.example.magazyn.repository.ReservationRepository;
import org.example.magazyn.service.HistoryService;
import org.example.magazyn.service.ReportService;
import org.example.magazyn.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final HistoryService historyService;
    private final ReportService reportService;

    @Transactional
    public Reservation createReservation(Product product, User user, int quantity) {
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Niewystarczająca ilość produktu");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        Reservation reservation = new Reservation();
        reservation.setProduct(product);
        reservation.setUser(user);
        reservation.setQuantity(quantity);
        reservation.setStatus(Reservation.ReservationStatus.ACTIVE);
        reservation.setReservationDate(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        // Log the reservation to history
        historyService.logProductReservation(
                user.getId(),
                product.getId(),
                product.getName(),
                quantity
        );

        return savedReservation;
    }

    public List<ReservationDto> getUserReservations(User user) {
        List<Reservation> reservations = reservationRepository.findByUser(user);

        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void cancelReservation(Long reservationId, User currentUser) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono rezerwacji"));

        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Brak uprawnień do anulowania tej rezerwacji");
        }

        Product product = reservation.getProduct();
        product.setQuantity(product.getQuantity() + reservation.getQuantity());
        productRepository.save(product);

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        // Log cancellation to history
        historyService.logReservationCancellation(
                currentUser.getId(),
                reservationId,
                product.getName(),
                reservation.getQuantity()
        );
    }

    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setProductId(reservation.getProduct().getId());
        dto.setProductName(reservation.getProduct().getName());
        dto.setUserName(reservation.getUser().getEmail());
        dto.setQuantity(reservation.getQuantity());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());
        dto.setStatusChangedByUser(reservation.getStatusChangedByUser());
        return dto;
    }

    @Override
    public List<ReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationDto updateReservationStatus(Long reservationId, Reservation.ReservationStatus status, Principal principal) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono rezerwacji"));

        // Get the old status before updating
        Reservation.ReservationStatus oldStatus = reservation.getStatus();

        // Update status
        reservation.setStatus(status);

        // Dodaj informację, kto zmienił status
        reservation.setStatusChangedByUser(principal.getName());

        Reservation updatedReservation = reservationRepository.save(reservation);

        // If status is COMPLETED, generate report
        if (status == Reservation.ReservationStatus.COMPLETED) {
            try {
                reportService.generateReportForReservation(updatedReservation, principal);
            } catch (Exception e) {
                throw new RuntimeException("Błąd generowania raportu: " + e.getMessage(), e);
            }
        }

        // Log status change to history
        historyService.logReservationStatusChange(
                updatedReservation.getUser().getId(),
                reservationId,
                oldStatus.toString(),
                status.toString(),
                updatedReservation.getProduct().getName()
        );

        return convertToDto(updatedReservation);
    }
}