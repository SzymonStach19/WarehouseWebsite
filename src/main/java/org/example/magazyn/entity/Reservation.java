package org.example.magazyn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "reserved_by_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "status_changed_by_user_id")
    private User statusChangedByUser;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public enum ReservationStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    @PrePersist
    public void prePersist() {
        this.reservationDate = LocalDateTime.now();

        if (this.status == null) {
            this.status = ReservationStatus.ACTIVE;
        }
    }
}