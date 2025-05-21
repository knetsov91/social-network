package posts.social.com.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Value("${auth.key}")
    private String key;
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        return  http.authorizeExchange(req ->
                req.pathMatchers("/api/v1/users/login", "/api/v1/users/register", "/api/v1/tokens/**","/actuator/**").permitAll()
                        .anyExchange().authenticated())
                .csrf(c -> c.disable())
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecode() {
        SecretKeySpec k = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"HS256");
        return NimbusReactiveJwtDecoder.withSecretKey(k).build();
    }
}