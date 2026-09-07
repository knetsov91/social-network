package posts.social.com.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitConfigUTest {

    private final KeyResolver keyResolver = new RateLimitConfig().userKeyResolver();

    @Test
    void whenTokenCookieHasValidJwt_thenResolvesToSubjectClaim() {
        String payload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("{\"sub\":\"user-123\"}".getBytes(StandardCharsets.UTF_8));
        String token = "header." + payload + ".signature";

        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/v1/posts").cookie(new org.springframework.http.HttpCookie("token", token))
        );

        String key = keyResolver.resolve(exchange).block();

        assertThat(key).isEqualTo("user-123");
    }
}
