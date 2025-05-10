package social.com.userservice.security;

import feign.FeignException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import social.com.userservice.auth.client.AuthClient;
import social.com.userservice.auth.client.dto.TokenValidationRequest;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    @Lazy
    private HandlerExceptionResolver handlerExceptionResolver;
    private AuthClient authClient;

    public JwtFilter(  AuthClient authClient) {

        this.authClient = authClient;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//
//            String json = "{\"message\":\"Unauthorized\",\"status\":401, \"data\": {\"url\": \"/api/v1/users/login\"}}";
//            response.getWriter().write(json);
//            filterChain.doFilter(request, response);

            filterChain.doFilter(request, response);

        }
        try {
            String jwtToken = authorization.split(" ")[1];
            ResponseEntity responseEntity = authClient.validateToken(new TokenValidationRequest(jwtToken));
            filterChain.doFilter(request, response);
        } catch (FeignException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
