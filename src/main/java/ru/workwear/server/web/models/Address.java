package ru.workwear.server.auth.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "Addresses")
public class Address {
    @Id
    private long id;

    private String country;
    private String locality;
    private String street;
    private String house;
    private String apartment;
}
