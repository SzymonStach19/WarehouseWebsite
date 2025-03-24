package org.example.magazyn.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportDto {
    private Long id;
    private Long reservationId;
    private String filePath;
    private LocalDateTime reportGenerationDate;
    private String generatedByUser;
    private ReservationDto reservation;
}