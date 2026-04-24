package posts.social.com.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import posts.social.com.apigateway.auth.service.AuthService;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class AuthFilter implements WebFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    private final List<PathPattern> publicPaths = List.of(
            "/api/v1/users/login",
            "/api/v1/users/register",
            "/api/v1/tokens/**",
            "/actuator/**"
    ).stream().map(new PathPatternParser()::parse).toList();
    private AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        boolean isPublic = publicPaths.stream()
                .anyMatch(p -> p.matches(request.getPath().pathWithinApplication()));
        if (isPublic) {
            return chain.filter(exchange);
        }


        HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
        if (token == null) {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authService.validateToken(token.getValue())
                .then(chain.filter(exchange))
                .onErrorResume(e -> {
                    log.error("Token validation failed: {}", e.getMessage());
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });

    }
}