package ru.workwear.server.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class User {
    private long id;
    private String username;
    private String password;
    private List<Authority> authorities;
    private String lastName;
    private String firstName;
    private String middleName;
    private String eMail;
    private String phoneNumber;
    private Gender gender;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthdate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate registrationDate;
    private Address address;
    private String accessToken;

    public String getFullName(){
        return lastName + " " + firstName + " " + middleName;
    }

    public enum Authority {
        AUTHORITY_Create,
        AUTHORITY_Read,
        AUTHORITY_Update,
        AUTHORITY_Delete
    }
}
