package org.example.magazyn.controller;

import jakarta.validation.Valid;
import org.example.magazyn.dto.UserDto;
import org.example.magazyn.entity.User;
import org.example.magazyn.service.HistoryService;
import org.example.magazyn.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
public class AuthController {

    private final UserService userService;
    private final HistoryService historyService;

    public AuthController(UserService userService, HistoryService historyService) {
        this.userService = userService;
        this.historyService = historyService;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("isLoggedIn", principal != null);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email", "duplicate.email",
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/login?registrationSuccess";
    }
    @GetMapping("/users")
    public String users(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/updateRoles")
    public String updateRoles(
            @RequestParam Long userId,
            @RequestParam(required = false) List<String> roles,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        try {
            if (roles == null) {
                roles = new ArrayList<>();
            }

            // Get the target user
            UserDto targetUserDto = userService.findAllUsers().stream()
                    .filter(user -> user.getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get the old role
            String oldRole = targetUserDto.getRole();

            // Get the new role (assuming one role per user for simplicity)
            String newRole = roles.isEmpty() ? "ROLE_USER" : roles.get(0);

            // Update roles
            userService.updateUserRoles(userId, roles);

            // Get admin user ID
            User adminUser = userService.findUserByEmail(currentUser.getUsername());

            // Log the role change in history
            historyService.logUserRoleChange(
                    adminUser.getId(),
                    userId,
                    targetUserDto.getEmail(),
                    oldRole,
                    newRole
            );

            redirectAttributes.addFlashAttribute("successMessage", "Roles updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating roles: " + e.getMessage());
        }
        return "redirect:/users";
    }
}