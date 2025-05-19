package posts.social.com.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        return  http.authorizeExchange(req ->
                req.pathMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll()
                        .anyExchange().authenticated())
                .csrf(c -> c.disable())
                .build();
    }
}
