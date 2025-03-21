package org.example.magazyn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {
    private Long id;
    private String userName;
    private String operation;
    private LocalDateTime timestamp;
    private String details;
}