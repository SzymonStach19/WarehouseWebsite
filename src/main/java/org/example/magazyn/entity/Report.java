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
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime reportGenerationDate;

    @Column(nullable = false)
    private String generatedByUser;

    @PrePersist
    public void prePersist() {
        this.reportGenerationDate = LocalDateTime.now();
    }
}