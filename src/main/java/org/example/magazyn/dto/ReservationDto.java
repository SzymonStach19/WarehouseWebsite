package org.example.magazyn.dto;

import lombok.Data;
import org.example.magazyn.entity.Reservation;

import java.time.LocalDateTime;

@Data
public class ReservationDto {
    private Long id;
    private Long productId;
    private String productName;
    private String userName;
    private int quantity;
    private LocalDateTime reservationDate;
    private Reservation.ReservationStatus status;
    private String statusChangedByUser;
}