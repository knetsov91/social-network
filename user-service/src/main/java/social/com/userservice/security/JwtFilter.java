package social.com.userservice.security;

import feign.FeignException;
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
import social.com.userservice.auth.client.AuthClient;
import social.com.userservice.auth.client.dto.TokenValidationRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    @Lazy
    private HandlerExceptionResolver handlerExceptionResolver;
    private AuthClient authClient;
    private UserDetailsService userDetailsService;
    public JwtFilter(  AuthClient authClient, UserDetailsService userDetailsService) {
        this.authClient = authClient;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        
        Optional<Cookie> token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("token"))
                .findFirst();


        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String jwtToken = token.get().getValue();
            ResponseEntity responseEntity = authClient.validateToken(new TokenValidationRequest(jwtToken));
            if (responseEntity.getStatusCode().value() == 200) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetails u1 = userDetailsService.loadUserByUsername("u1");
                if (authentication == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(u1, null, u1.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (FeignException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
