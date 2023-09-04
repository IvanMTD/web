package ru.workwear.server.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.dto.AddressDTO;
import ru.workwear.server.web.dto.UserDTO;
import ru.workwear.server.web.models.Gender;
import ru.workwear.server.web.models.PageData;
import ru.workwear.server.web.services.AuthService;
import ru.workwear.server.web.validations.AddressValidation;
import ru.workwear.server.web.validations.UserValidation;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PageData pageData;
    private final AddressValidation addressValidation;
    private final UserValidation userValidation;
    private final AuthService authService;

    @GetMapping("/reg")
    public Mono<Rendering> regPage(){
        return Mono.just(
                Rendering.view("auth/reg")
                        .modelAttribute("user", new UserDTO())
                        .modelAttribute("gender", Gender.values())
                        .modelAttribute("address", new AddressDTO())
                        .build()
        ).map(rendering -> {
            if(pageData.isAuthenticated()){
                return Rendering.redirectTo("/").build();
            }else{
                return rendering;
            }
        });
    }

    @PostMapping("/reg")
    public Mono<Rendering> saveUser(
            @ModelAttribute(name = "user") @Valid UserDTO userDTO, BindingResult userError,
            @ModelAttribute(name = "address") @Valid AddressDTO addressDTO, BindingResult addressErrors,
            ServerWebExchange exchange
    ){

        return userValidation.checkUsername(userDTO,userError)
                        .flatMap(userErrors -> Mono.just(Rendering.redirectTo("/").build())
                                .flatMap(rendering -> {
                                    addressValidation.validate(addressDTO,addressErrors);
                                    userValidation.validate(userDTO,userErrors);
                                    if(userErrors.hasErrors() || addressErrors.hasErrors()){
                                        return Mono.just(Rendering.view("auth/reg")
                                                .modelAttribute("user", userDTO)
                                                .modelAttribute("gender", Gender.values())
                                                .modelAttribute("address", addressDTO)
                                                .build()
                                        );
                                    }else{
                                        userDTO.setAddressDTO(addressDTO);
                                        userDTO.setDigitalSignature(exchange.getRequest().getHeaders().toSingleValueMap().get("User-Agent"));
                                        return authService.saveUser(userDTO)
                                                .map(refreshToken -> {
                                                    exchange.getResponse().addCookie(createResponseCookie(refreshToken));
                                                    return rendering;
                                                });
                                    }
                                }));
    }

    @GetMapping("/login")
    public Mono<Rendering> loginPage(){
        return Mono.just(
                Rendering.view("auth/login").build()
        ).map(rendering -> {
            if(pageData.isAuthenticated()){
                return Rendering.redirectTo("/").build();
            }else{
                return rendering;
            }
        });
    }

    @PostMapping("/login")
    public Mono<Rendering> login(ServerWebExchange exchange, @ModelAttribute(name = "pageData") @Valid PageData pageData, Errors errors){
        return exchange.getFormData()
                .flatMap(form -> {
                    String username = form.getFirst("username");
                    String password = form.getFirst("password");
                    log.info(username + " | " + password);
                    String digitalSignature = exchange.getRequest().getHeaders().toSingleValueMap().get("User-Agent");
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUsername(username);
                    userDTO.setPassword(password);
                    userDTO.setDigitalSignature(digitalSignature);
                    return authService.login(userDTO)
                            .flatMap(refreshToken -> Mono.justOrEmpty(Rendering.redirectTo("/").build())
                                    .flatMap(rendering -> {
                                        exchange.getResponse().addCookie(createResponseCookie(refreshToken));
                                        return Mono.just(rendering);
                                    })
                            ).switchIfEmpty(
                                    Mono.just(Rendering.view("auth/login").build())
                                            .map(rendering -> {
                                                errors.rejectValue("loginError","","Имя пользователя или пароль неверны");
                                                return rendering;
                                            })
                            );
                });
    }

    @ModelAttribute(name = "pageData")
    public PageData setPageData(){
        pageData.setTitle("Auth Page");
        return pageData;
    }

    private ResponseCookie createResponseCookie(String refreshToken){
        return ResponseCookie.from("refresh",refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2592000)
                .sameSite("Strict")
                .build();
    }
}
