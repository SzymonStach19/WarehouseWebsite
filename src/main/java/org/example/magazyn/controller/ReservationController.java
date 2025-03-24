package org.example.magazyn.controller;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.Reservation;
import org.example.magazyn.entity.User;
import org.example.magazyn.repository.ProductRepository;
import org.example.magazyn.repository.UserRepository;
import org.example.magazyn.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/products/reserve/{id}")
    public String showReservationForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu"));
        model.addAttribute("product", product);
        return "reserve";  // New reservation form page
    }

    @PostMapping("/products/reserve/{id}")
    public String reserveProduct(
            @PathVariable Long id,
            @RequestParam int quantity,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User currentUser = userRepository.findByEmail(principal.getName());
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono produktu"));

            reservationService.createReservation(product, currentUser, quantity);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Zarezerwowano " + quantity + " szt. produktu: " + product.getName());

            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Błąd rezerwacji: " + e.getMessage());

            return "redirect:/products/reserve/" + id;
        }
    }

    @GetMapping("/reservations")
    public String showAllReservations(Model model, Principal principal) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "reservations";
    }

    @PostMapping("/reservations/update-status/{id}")
    public ResponseEntity<Map<String, Object>> updateReservationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Principal principal
    ) {
        try {
            Reservation.ReservationStatus status =
                    Reservation.ReservationStatus.valueOf(payload.get("status"));

            // Pass principal to the service method
            reservationService.updateReservationStatus(id, status, principal);

            return ResponseEntity.ok(Map.of("message", "Status zaktualizowany"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Błąd aktualizacji: " + e.getMessage()));
        }
    }

    @PostMapping("/reservations/cancel/{id}")
    public String cancelReservation(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User currentUser = userRepository.findByEmail(principal.getName());
            reservationService.cancelReservation(id, currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Rezerwacja została anulowana");
            return "redirect:/reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Błąd anulowania rezerwacji: " + e.getMessage());
            return "redirect:/reservations";
        }
    }
}