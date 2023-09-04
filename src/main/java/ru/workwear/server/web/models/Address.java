package ru.workwear.server.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
    private long id;
    private int index;
    private String country;
    private String locality;
    private String street;
    private String house;
    private String apartment;
}
