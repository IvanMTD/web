package ru.workwear.server.web.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.dto.UserDTO;
import ru.workwear.server.web.models.User;

import java.util.List;

@Slf4j
@Service
public class AuthService {

    private final WebClient webClient;

    public AuthService(){
        webClient = WebClient.create();
    }
    /*
    * after saving the user, the method returns the refresh token
    */
    public Mono<String> saveUser(UserDTO userDTO){
        return webClient
                .post()
                .uri("http://localhost:9000/api/auth/user/save")
                .bodyValue(userDTO)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> login(UserDTO userDTO){
        return webClient
                .post()
                .uri("http://localhost:9000/api/auth/login")
                .bodyValue(userDTO)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> {
                    log.error("Some error " + error.getMessage());
                })
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<Boolean> deleteRefreshToken(String refreshToken, String accessToken) {
        return webClient
                .delete()
                .uri("http://localhost:9000/api/auth/refresh/delete/" + refreshToken)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
