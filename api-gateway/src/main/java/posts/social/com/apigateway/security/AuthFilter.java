package posts.social.com.apigateway.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Base64;
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
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    private String extractUserId(String token) {
        try {
            String payload = token.split("\\.")[1];
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            JsonNode claims = objectMapper.readTree(decoded);
            return claims.path("userId").asText(null);
        } catch (Exception e) {
            log.warn("Failed to extract userId from token: {}", e.getMessage());
            return null;
        }
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

        String userId = extractUserId(token.getValue());
        ServerWebExchange mutatedExchange = userId != null
                ? exchange.mutate().request(r -> r.header("X-User-Id", userId)).build()
                : exchange;

        return authService.validateToken(token.getValue())
                .then(chain.filter(mutatedExchange))
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