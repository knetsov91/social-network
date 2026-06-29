package posts.social.com.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Objects;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
            if (token != null) {
                try {
                    String[] parts = token.getValue().split("\\.");
                    String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                    String sub = payload.replaceAll(".*\"sub\":\"([^\"]+)\".*", "$1");
                    return Mono.just(sub);
                } catch (Exception e) {
                    // fall through to IP
                }
            }
            return Mono.just(
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress()
            );
        };
    }
}
