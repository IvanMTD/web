package ru.workwear.server.web.configurations.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.dto.AuthRequest;
import ru.workwear.server.web.models.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private WebClient webClient;

    public AuthenticationManager(){
        webClient = WebClient.create();
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if(authentication != null) {
            String refreshToken = authentication.getPrincipal().toString();
            String digitalSignature = authentication.getCredentials().toString();
            Mono<User> userMono = null;
            try {
                userMono = webClient.post()
                        .uri("http://localhost:9000/api/auth/refresh/login")
                        .bodyValue(new AuthRequest(refreshToken, digitalSignature))
                        .retrieve()
                        .bodyToMono(User.class)
                        .doOnError(error -> {
                            log.error("Some error " + error.getMessage());
                        })
                        .onErrorResume(error -> Mono.empty());
            } catch (WebClientResponseException exception) {
                log.info(exception.getMessage());
            }

            if (userMono != null) {
                return userMono
                        .flatMap(user -> {
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    user,
                                    user.getAuthorities().stream()
                                            .map(authority -> new SimpleGrantedAuthority(authority.toString()))
                                            .collect(Collectors.toSet())
                            );

                            return Mono.just(authenticationToken).cast(Authentication.class);
                        })
                        .switchIfEmpty(Mono.just(emptyAuthentication()));
            } else {
                return Mono.empty();
            }
        }else{
            return Mono.just(emptyAuthentication());
        }
    }

    private Authentication emptyAuthentication(){
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };
    }
}
