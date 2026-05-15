package posts.social.com.apigateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    @Value("${frontend.origin:http://localhost:5173}")
    private String frontendOrigin;

    private final List<PathPattern> publicPaths = List.of(
            "/api/v1/users/login",
            "/api/v1/users/register",
            "/api/v1/users",
            "/api/v1/users/*",
            "/api/v1/tokens/**",
            "/actuator/**",
            "/ws-chat/**",
            "/ws-notifications/**"
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
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        boolean isPublic = publicPaths.stream()
                .anyMatch(p -> p.matches(request.getPath().pathWithinApplication()));
        if (isPublic) {
            return chain.filter(exchange);
        }


        HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
        if (token == null) {
            return unauthorized(exchange);
        }

        return authService.validateToken(token.getValue())
                .then(chain.filter(exchange))
                .onErrorResume(e -> {
                    log.error("Token validation failed: {}", e.getMessage());
                    return unauthorized(exchange);
                });

    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders()
                .add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, frontendOrigin);
        exchange.getResponse().getHeaders()
                .add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}