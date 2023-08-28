package ru.workwear.server.auth.configurations.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getPrincipal().toString();
        String username;

        if(jwtProvider.validateAccessToken(authToken)) {
            try {
                username = jwtProvider.getAccessClaims(authToken).getSubject();
                log.info("Found user with username " + username);
            } catch (UsernameNotFoundException e) {
                username = null;
                log.error("user not found | " + e);
            }

            if (username != null && jwtProvider.validateAccessToken(authToken)) {
                Claims claims = jwtProvider.getAccessClaims(authToken);
                List<Object> authClaims = claims.get("authorities", List.class);
                List<SimpleGrantedAuthority> authorities = authClaims.stream().map(o -> new SimpleGrantedAuthority(o.toString())).toList();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );
                return Mono.just(authenticationToken);
            } else {
                return Mono.empty();
            }
        }else{
            log.error("Token is not valid!");
            return Mono.empty();
        }
    }
}
