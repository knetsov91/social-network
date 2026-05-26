package social.com.authservice.token.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.com.authservice.token.model.Token;
import social.com.authservice.token.repository.TokenRepository;
import social.com.authservice.web.dto.InvalidateTokenRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenServiceUTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @Test
    void test_invalidateToken_whenTokenProvided_thenSavesTokenToBlacklist() {
        InvalidateTokenRequest request = new InvalidateTokenRequest("jwt-token");

        tokenService.invalidateToken(request);

        ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
        verify(tokenRepository).save(captor.capture());

        assertEquals("jwt-token", captor.getValue().getToken());
    }
}
