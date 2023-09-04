package ru.workwear.server.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.workwear.server.web.models.Gender;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 4, max=16, message = "4-16 знаков")
    private String username;
    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 8, max=30, message = "8-30 знаков")
    private String password;
    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 8, max=30, message = "8-30 знаков")
    private String confirmPassword;
    @Size(min = 2, max=16, message = "2-16 знаков")
    private String lastName;
    @NotBlank(message = "Поле обязательно к заполнению")
    @Size(min = 2, max=16, message = "2-16 знаков")
    private String firstName;
    @Size(min = 2, max=16, message = "2-16 знаков")
    private String middleName;
    @NotBlank(message = "Поле обязательно к заполнению")
    @Email(message = "Не валидный E-Mail")
    private String eMail;
    @Pattern(regexp = "\\+\\d{11}", message = "Укажите номер телефона в формате +79998887766")
    private String phoneNumber;
    private Gender gender;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Past(message = "Введите правильную дату")
    @NotNull(message = "Не может быть Null")
    private LocalDate birthdate;
    private AddressDTO addressDTO;
    private String digitalSignature;
}
