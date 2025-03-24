package org.example.magazyn.controller;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.dto.ZoneDto;
import org.example.magazyn.entity.Product;
import org.example.magazyn.entity.Zone;
import org.example.magazyn.service.ZoneService;
import org.example.magazyn.service.ProductService;
import org.example.magazyn.service.HistoryService;
import org.example.magazyn.service.UserService; // Add this import if you have a UserService
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/zones")
public class ZoneController {

    private final ZoneService zoneService;
    private final ProductService productService;
    private final HistoryService historyService;
    // Add UserService if you have one
    // private final UserService userService;

    // Helper method to get current user ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return 1L; // Default ID or system ID, adjust as needed
        }

        // Get the username/email from authentication
        String username = authentication.getName();

        // Option 1: If you store the user ID in the authentication principal
        // Try to get it from authentication principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.example.magazyn.entity.User) {
            return ((org.example.magazyn.entity.User) principal).getId();
        }

        // Option 2: If you have a UserService to look up user by username/email
        // return userService.findByUsername(username).getId();

        // Fallback to default/system user ID
        return 1L; // Replace with an appropriate default or system user ID
    }

    @GetMapping
    public String getZones(Model model) {
        model.addAttribute("zones", zoneService.getAllZones());
        return "zones";
    }

    @GetMapping("/add")
    public String showAddZoneForm(Model model) {
        model.addAttribute("zoneDto", new ZoneDto());
        return "addZone";
    }

    @PostMapping("/add")
    public String addZone(@ModelAttribute ZoneDto zoneDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "addZone";
        }

        try {
            Zone createdZone = zoneService.createZone(zoneDto);

            // Use our helper method to get current user ID
            Long userId = getCurrentUserId();

            // Log zone creation in history
            historyService.logZoneCreation(
                    userId,
                    createdZone.getId(),
                    createdZone.getName(),
                    createdZone.getMaxCapacity()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Strefa " + createdZone.getName() + " została utworzona.");
            return "redirect:/zones";
        } catch (Exception e) {
            bindingResult.rejectValue("name", "error.zoneDto", e.getMessage());
            return "addZone";
        }
    }

    @GetMapping("/details/{id}")
    public String getZoneDetails(@PathVariable Long id, Model model) {
        Zone zone = zoneService.getZoneById(id);
        model.addAttribute("zone", zone);
        model.addAttribute("products", zoneService.getProductsInZone(id));
        return "zoneDetails";
    }

    @GetMapping("/edit/{id}")
    public String showEditZoneForm(@PathVariable Long id, Model model) {
        Zone zone = zoneService.getZoneById(id);
        ZoneDto zoneDto = new ZoneDto(zone.getName(), zone.getMaxCapacity());
        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("zoneId", id);
        return "editZone";
    }

    @PostMapping("/edit/{id}")
    public String editZone(@PathVariable Long id,
                           @ModelAttribute ZoneDto zoneDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "editZone";
        }

        try {
            // Get the zone before updating to compare values
            Zone existingZone = zoneService.getZoneById(id);
            String oldName = existingZone.getName();
            double oldCapacity = existingZone.getMaxCapacity();

            Zone updatedZone = zoneService.updateZone(id, zoneDto);

            // Use our helper method to get current user ID
            Long userId = getCurrentUserId();

            // Log zone update in history
            historyService.logZoneUpdate(
                    userId,
                    updatedZone.getId(),
                    oldName,
                    updatedZone.getName(),
                    oldCapacity,
                    updatedZone.getMaxCapacity()
            );

            redirectAttributes.addFlashAttribute("successMessage",
                    "Strefa " + updatedZone.getName() + " została zaktualizowana.");
            return "redirect:/zones";
        } catch (Exception e) {
            bindingResult.rejectValue("name", "error.zoneDto", e.getMessage());
            return "editZone";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteZone(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            Zone zone = zoneService.getZoneById(id);

            // Use our helper method to get current user ID
            Long userId = getCurrentUserId();

            // Log zone deletion in history before the zone is deleted
            historyService.logZoneDeletion(
                    userId,
                    zone.getId(),
                    zone.getName()
            );

            zoneService.deleteZone(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Strefa " + zone.getName() + " została usunięta.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie można usunąć strefy: " + e.getMessage());
        }
        return "redirect:/zones";
    }

    @GetMapping("/assignProduct/{zoneId}")
    public String showAssignProductForm(@PathVariable Long zoneId, Model model) {
        Zone zone = zoneService.getZoneById(zoneId);
        List<Product> availableProducts = productService.findByZoneIsNull();

        model.addAttribute("zone", zone);
        model.addAttribute("products", availableProducts);

        return "assignProduct";
    }

    @PostMapping("/assignProduct")
    public String assignProduct(@RequestParam Long productId,
                                @RequestParam Long zoneId,
                                RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId);
            Zone zone = zoneService.getZoneById(zoneId);

            zoneService.assignProductToZone(productId, zoneId);

            // Use our helper method to get current user ID
            Long userId = getCurrentUserId();

            // Log product zone assignment in history
            historyService.logProductZoneAssignment(
                    userId,
                    productId,
                    product.getName(),
                    zoneId,
                    zone.getName(),
                    product.getQuantity()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Produkt został przypisany do strefy");
            return "redirect:/zones/details/" + zoneId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/zones/assignProduct/" + zoneId;
        }
    }

    @GetMapping("/removeProduct/{zoneId}")
    public String showRemoveProductForm(@PathVariable Long zoneId, Model model) {
        try {
            Zone zone = zoneService.getZoneById(zoneId);
            model.addAttribute("zone", zone);
            return "removeProduct";
        } catch (Exception e) {
            return "redirect:/zones";
        }
    }

    @GetMapping("/removeProduct/{zoneId}/{productId}")
    public String removeProductFromZone(@PathVariable Long zoneId,
                                        @PathVariable Long productId,
                                        RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(productId);
            Zone zone = zoneService.getZoneById(zoneId);

            // Use our helper method to get current user ID
            Long userId = getCurrentUserId();

            // Log product zone removal in history before removing
            historyService.logProductZoneRemoval(
                    userId,
                    productId,
                    product.getName(),
                    zoneId,
                    zone.getName()
            );

            zoneService.removeProductFromZone(productId, zoneId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Produkt został usunięty ze strefy");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie można usunąć produktu ze strefy: " + e.getMessage());
        }
        return "redirect:/zones/removeProduct/" + zoneId;
    }
}