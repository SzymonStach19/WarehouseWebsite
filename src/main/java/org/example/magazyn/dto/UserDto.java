package org.example.magazyn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(message = "Imie nie powinno być puste")
    private String firstName;
    @NotEmpty(message = "Naswisko nie powinno być puste")
    private String lastName;
    @NotEmpty(message = "Email nie powinien być pusty")
    @Email
    private String email;
    @NotEmpty(message = "Hasło nie powinno być puste")
    private String password;
    private String role;
}
