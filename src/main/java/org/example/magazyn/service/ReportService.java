package org.example.magazyn.service;

import org.example.magazyn.dto.ReportDto;
import org.example.magazyn.entity.Reservation;
import org.example.magazyn.entity.Report;

import java.io.IOException;
import java.security.Principal;

public interface ReportService {
    Report generateReportForReservation(Reservation reservation, Principal principal) throws IOException;
    ReportDto getReportByReservationId(Long reservationId);
}