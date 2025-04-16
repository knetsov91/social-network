package social.com.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class SecurityConfig implements WebMvcConfigurer {


    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {

       return http
            .authorizeHttpRequests(req ->
                req.requestMatchers("/auth/register", "/auth/login")
                        .permitAll()
                        .anyRequest().permitAll()
            ).csrf(c -> c.disable())
               .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.NEVER))
           .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}