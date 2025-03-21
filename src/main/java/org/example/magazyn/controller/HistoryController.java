package org.example.magazyn.controller;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.dto.HistoryDto;
import org.example.magazyn.entity.History;
import org.example.magazyn.entity.User;
import org.example.magazyn.service.HistoryService;
import org.example.magazyn.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final UserService userService;

    @GetMapping
    public String viewHistory(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.findUserByEmail(currentUser.getUsername());

        // Sprawdzanie, czy użytkownik ma rolę ADMIN
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        List<History> historyList;
        if (isAdmin) {
            // Admin widzi całą historię
            historyList = historyService.getAllHistory();
        } else {
            // Zwykły użytkownik widzi tylko swoją historię
            historyList = historyService.getUserHistory(user.getId());
        }

        // Konwersja na DTO z nazwami użytkowników
        List<HistoryDto> historyDtoList = historyList.stream()
                .map(history -> {
                    HistoryDto dto = new HistoryDto();
                    dto.setId(history.getId());

                    // Pobierz nazwę użytkownika na podstawie ID
                    User historyUser = userService.findUserByEmail(
                            userService.findAllUsers().stream()
                                    .filter(u -> u.getId().equals(history.getUserId()))
                                    .findFirst()
                                    .orElse(new org.example.magazyn.dto.UserDto())
                                    .getEmail());

                    dto.setUserName(historyUser != null ? historyUser.getName() : "Nieznany");
                    dto.setOperation(history.getOperation());
                    dto.setTimestamp(history.getTimestamp());
                    dto.setDetails(history.getDetails());
                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("historyList", historyDtoList);
        return "list";
    }
}