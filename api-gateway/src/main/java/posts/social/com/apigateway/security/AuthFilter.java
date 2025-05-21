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
import posts.social.com.apigateway.auth.service.AuthService;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class AuthFilter implements WebFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    private List<String> unauthorizedUrls = List.of("/api/v1/users/login",
            "/api/v1/users/register",
            "/actuator/prometheus");
    private AuthService authService;

    public AuthFilter(AuthService authService ) {
        this.authService = authService;
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (unauthorizedUrls.contains(request.getURI().getPath())) {
            return chain.filter(exchange);
        }


        HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
        if (token != null) {
            authService.validateToken(token.getValue());
        }
        return chain.filter(exchange);

    }
}