package social.com.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import social.com.userservice.auth.client.dto.TokenValidationRequest;
import social.com.userservice.auth.service.AuthService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    @Lazy
    private HandlerExceptionResolver handlerExceptionResolver;
    private AuthService authService;
    private UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(AuthService authService, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Optional<Cookie> token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst();

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String jwtToken = token.get().getValue();
            authService.validateToken(new TokenValidationRequest(jwtToken));
            ResponseEntity tokenInvalidatedCheck = authService.checkIfInvalidated(jwtToken);
            if (tokenInvalidatedCheck.getStatusCode().value() == 200) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null) {
                    String username = extractUsername(jwtToken);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private String extractUsername(String token) throws Exception {
        String payload = token.split("\\.")[1];
        byte[] decoded = Base64.getUrlDecoder().decode(payload);
        Map<String, Object> claims = objectMapper.readValue(decoded, Map.class);
        return (String) claims.get("sub");
    }
}
