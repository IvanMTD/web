package ru.workwear.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthRequest {
    private String refreshToken;
    private String digitalSignature;
}
