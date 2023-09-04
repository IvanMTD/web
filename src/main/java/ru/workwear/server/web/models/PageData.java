package ru.workwear.server.web.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PageData {
    private String title;
    private boolean authenticated;
    private User user;
    private boolean loginError;
}
