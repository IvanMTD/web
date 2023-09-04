package ru.workwear.server.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.models.PageData;

@Slf4j
@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final PageData pageData;

    // CATALOG
    @GetMapping("/catalog")
    public Mono<Rendering> showCatalog(){
        return Mono.just(
                Rendering
                        .view("store/catalog")
                        .build()
        );
    }

    // CART
    @GetMapping("/cart")
    public Mono<Rendering> showCart(){
        return Mono.just(
                Rendering
                        .view("store/cart")
                        .build()
        );
    }

    @ModelAttribute(name = "pageData")
    public PageData pageData(){
        pageData.setTitle("Store Page");
        return pageData;
    }
}
