package social.com.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import social.com.userservice.security.JwtFilter;
import social.com.userservice.user.repository.UserRepository;

import java.util.List;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    private final UserDetailsService userDetailsService;
    private UserRepository userRepository;
    @Value("${frontend.url}") String frontendUrl;

    private JwtFilter jwtFilter;

    public SecurityConfig(UserDetailsService userDetailsService, UserRepository userRepository, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {

       return http
               .cors(c -> c.configurationSource(e -> corsConfiguration()))
            .authorizeHttpRequests(req ->
                req.requestMatchers("/api/v1/users/register",
                                "/api/v1/users/login",
                                "/api/v1/users/auth/issue"
                        )
                        .permitAll()
                        .anyRequest().authenticated()
            ).csrf(c -> c.disable())
           .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
           .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
           .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("Content-Type", "Authorization", "Accept"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        return corsConfiguration;
    }
}