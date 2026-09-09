package posts.social.com.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
            MockServerHttpRequest.get("/api/v1/posts").cookie(new HttpCookie("token", token))
        );

        String key = keyResolver.resolve(exchange).block();

        assertThat(key).isEqualTo("user-123");
    }

    @Test
    void whenNoTokenCookie_thenFallsBackToRemoteAddress() throws UnknownHostException {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/v1/posts").remoteAddress(remoteAddress())
        );

        String key = keyResolver.resolve(exchange).block();

        assertThat(key).isEqualTo("192.168.1.10");
    }

    @Test
    void whenTokenCookieIsMalformed_thenFallsBackToRemoteAddress() throws UnknownHostException {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/v1/posts")
                .cookie(new HttpCookie("token", "not-a-valid-jwt"))
                .remoteAddress(remoteAddress())
        );

        String key = keyResolver.resolve(exchange).block();

        assertThat(key).isEqualTo("192.168.1.10");
    }

    private static InetSocketAddress remoteAddress() throws UnknownHostException {
        return new InetSocketAddress(InetAddress.getByName("192.168.1.10"), 12345);
    }
}
