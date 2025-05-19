 package posts.social.com.apigateway.security;

import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthFilter implements WebFilter, Ordered {

    private List<String> unauthorizedUrls = List.of("/api/v1/users/login", "/api/v1/users/register");

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

        return chain.filter(exchange);

    }
}