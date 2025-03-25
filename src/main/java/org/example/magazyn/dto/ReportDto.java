package org.example.magazyn.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReportDto {
    private Long id;
    private Long reservationId;
    private String filePath;
    private LocalDateTime reportGenerationDate;
    private String generatedByUser;
}