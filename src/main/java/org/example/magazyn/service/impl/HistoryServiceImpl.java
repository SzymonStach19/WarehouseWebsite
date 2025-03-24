package org.example.magazyn.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.entity.History;
import org.example.magazyn.entity.User;
import org.example.magazyn.repository.HistoryRepository;
import org.example.magazyn.service.HistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    public History addHistoryEntry(Long userId, String operation, String details) {
        History history = new History();
        history.setUserId(userId);
        history.setOperation(operation);
        history.setTimestamp(LocalDateTime.now());
        history.setDetails(details);

        return historyRepository.save(history);
    }

    @Override
    public List<History> getAllHistory() {
        return historyRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    public List<History> getUserHistory(Long userId) {
        return historyRepository.findByUserId(userId);
    }

    // Product reservation history
    @Override
    public History logProductReservation(Long userId, Long productId, String productName, int quantity) {
        return addHistoryEntry(
                userId,
                "RESERVE_PRODUCT",
                "Zarezerwowano produkt: " + productName + ", ID: " + productId + ", Ilość: " + quantity
        );
    }

    @Override
    public History logReservationCancellation(Long userId, Long reservationId, String productName, int quantity) {
        return addHistoryEntry(
                userId,
                "CANCEL_RESERVATION",
                "Anulowano rezerwację ID: " + reservationId + ", Produkt: " + productName + ", Ilość: " + quantity
        );
    }

    @Override
    public History logReservationStatusChange(Long userId, Long reservationId, String oldStatus, String newStatus, String productName) {
        return addHistoryEntry(
                userId,
                "UPDATE_RESERVATION_STATUS",
                "Zmieniono status rezerwacji ID: " + reservationId + " z \"" + oldStatus + "\" na \"" + newStatus +
                        "\", Produkt: " + productName
        );
    }

    // Zone management history
    @Override
    public History logZoneCreation(Long userId, Long zoneId, String zoneName, double maxCapacity) {
        return addHistoryEntry(
                userId,
                "CREATE_ZONE",
                "Utworzono strefę: " + zoneName + ", ID: " + zoneId + ", Maksymalna pojemność: " + maxCapacity + " kg"
        );
    }

    @Override
    public History logZoneUpdate(Long userId, Long zoneId, String oldName, String newName,
                                 double oldCapacity, double newCapacity) {
        return addHistoryEntry(
                userId,
                "UPDATE_ZONE",
                "Zaktualizowano strefę ID: " + zoneId +
                        (oldName.equals(newName) ? "" : ", Nazwa zmieniona z \"" + oldName + "\" na \"" + newName + "\"") +
                        (oldCapacity == newCapacity ? "" : ", Pojemność zmieniona z " + oldCapacity + " kg na " + newCapacity + " kg")
        );
    }

    @Override
    public History logZoneDeletion(Long userId, Long zoneId, String zoneName) {
        return addHistoryEntry(
                userId,
                "DELETE_ZONE",
                "Usunięto strefę: " + zoneName + ", ID: " + zoneId
        );
    }

    // Product zone assignment history
    @Override
    public History logProductZoneAssignment(Long userId, Long productId, String productName,
                                            Long zoneId, String zoneName, int quantity) {
        return addHistoryEntry(
                userId,
                "ASSIGN_PRODUCT_TO_ZONE",
                "Przypisano produkt do strefy: Produkt \"" + productName + "\" (ID: " + productId +
                        ", Ilość: " + quantity + ") do strefy \"" + zoneName + "\" (ID: " + zoneId + ")"
        );
    }

    @Override
    public History logProductZoneRemoval(Long userId, Long productId, String productName,
                                         Long zoneId, String zoneName) {
        return addHistoryEntry(
                userId,
                "REMOVE_PRODUCT_FROM_ZONE",
                "Usunięto produkt ze strefy: Produkt \"" + productName + "\" (ID: " + productId +
                        ") ze strefy \"" + zoneName + "\" (ID: " + zoneId + ")"
        );
    }

    // User role management history
    @Override
    public History logUserRoleChange(Long adminUserId, Long targetUserId, String targetUserEmail,
                                     String oldRole, String newRole) {
        return addHistoryEntry(
                adminUserId,
                "CHANGE_USER_ROLE",
                "Zmieniono rolę użytkownika: Email: " + targetUserEmail +
                        ", ID: " + targetUserId + ", z \"" + oldRole + "\" na \"" + newRole + "\""
        );
    }
}