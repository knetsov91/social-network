package social.com.authservice.token.service;

import org.springframework.stereotype.Service;
import social.com.authservice.token.model.Token;
import social.com.authservice.token.repository.TokenRepository;
import social.com.authservice.web.dto.InvalidateTokenRequest;
import java.util.UUID;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void invalidateToken(InvalidateTokenRequest token) {
        Token t = new Token(UUID.randomUUID(), token.token());
        tokenRepository.save(t);
    }
}