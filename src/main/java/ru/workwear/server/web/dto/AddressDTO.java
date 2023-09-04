package ru.workwear.server.web.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDTO {
    @Digits(message="Индекс должен содержать 6 цифр", fraction = 0, integer = 6)
    private int index;
    @NotBlank(message = "Поле не может быть пустым")
    private String country;
    @NotBlank(message = "Поле не может быть пустым")
    private String locality;
    @NotBlank(message = "Поле не может быть пустым")
    private String street;
    @NotBlank(message = "Поле не может быть пустым")
    private String house;
    @NotBlank(message = "Поле не может быть пустым")
    private String apartment;
}
