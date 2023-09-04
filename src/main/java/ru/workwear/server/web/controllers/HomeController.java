package ru.workwear.server.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.models.PageData;
import ru.workwear.server.web.models.User;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PageData pageData;

    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(
                Rendering.view("home")
                        .build()
        );
    }

    @ModelAttribute(name = "pageData")
    public PageData setPageData(Authentication authentication){
        pageData.setTitle("Home Page");
        if(authentication != null){
            pageData.setAuthenticated(authentication.isAuthenticated());
            pageData.setUser((User) authentication.getCredentials());
        }
        return pageData;
    }
}
