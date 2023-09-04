package ru.workwear.server.web.configurations.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.workwear.server.web.models.PageData;
import ru.workwear.server.web.services.AuthService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final PageData pageData;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {

        String refreshToken = null;
        try{
            refreshToken = exchange.getRequest().getCookies().getFirst("refresh").getValue();
            authService.deleteRefreshToken(refreshToken,pageData.getUser().getAccessToken()).subscribe();
            exchange.getResponse().addCookie(createNullResponseCookie());
        }catch (NullPointerException exception){
            log.error("Null: " + exception.getMessage());
        }
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        MultiValueMap<String, HttpCookie> requestCookies = exchange.getRequest().getCookies();
        String refreshToken = null;

        for(String key : requestCookies.keySet()){
            if(key.equals("refresh")){
                refreshToken = requestCookies.getFirst("refresh").getValue();
                break;
            }
        }

        if(refreshToken != null){
            log.info("Found refresh token");
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(refreshToken,exchange.getRequest().getHeaders().toSingleValueMap().get("User-Agent"));
            return authenticationManager
                    .authenticate(auth)
                    .map(SecurityContextImpl::new);
        }else{
            log.info("Logout user on null refresh");
            return authenticationManager
                    .authenticate(null)
                    .map(SecurityContextImpl::new);
        }
    }

    private ResponseCookie createNullResponseCookie(){
        return ResponseCookie.from("refresh","null")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }
}
