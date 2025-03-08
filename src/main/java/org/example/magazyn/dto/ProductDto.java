package org.example.magazyn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    @NotEmpty(message = "Nazwa produktu jest wymagana")
    private String name;

    @NotEmpty(message = "Kategoria jest wymagana")
    private String category;

    @NotEmpty(message = "Opis jest wymagany")
    private String description;

    @NotNull(message = "Cena jest wymagana")
    @Min(value = 0, message = "Cena nie może być ujemna")
    private Double price;

    @NotNull(message = "Waga jest wymagana")
    @Min(value = 0, message = "Waga nie może być ujemna")
    private Double weight;

    @NotEmpty(message = "Marka jest wymagana")
    private String brand;

    @Min(value = 0, message = "Ilość nie może być ujemna")
    private int quantity;

    private MultipartFile image;
}