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

import social.com.authservice.web.dto.TokenValidateRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void test_isValid_whenTokenIsBlacklisted_thenThrowsException() {
        TokenValidateRequest request = new TokenValidateRequest("jwt-token");
        Token blacklisted = new Token(UUID.randomUUID(), "jwt-token");

        when(tokenRepository.findByToken("jwt-token")).thenReturn(Optional.of(blacklisted));

        assertThrows(RuntimeException.class, () -> tokenService.isValid(request));
    }

    @Test
    void test_isValid_whenTokenNotBlacklisted_thenDoesNotThrow() {
        TokenValidateRequest request = new TokenValidateRequest("jwt-token");

        when(tokenRepository.findByToken("jwt-token")).thenReturn(Optional.empty());

        tokenService.isValid(request);
    }

    @Test
    void test_isInvalidated_whenTokenIsBlacklisted_thenReturnsTrue() {
        TokenValidateRequest request = new TokenValidateRequest("jwt-token");
        Token blacklisted = new Token(UUID.randomUUID(), "jwt-token");

        when(tokenRepository.findByToken("jwt-token")).thenReturn(Optional.of(blacklisted));

        assertTrue(tokenService.isInvalidated(request));
    }

    @Test
    void test_isInvalidated_whenTokenNotBlacklisted_thenReturnsFalse() {
        TokenValidateRequest request = new TokenValidateRequest("jwt-token");

        when(tokenRepository.findByToken("jwt-token")).thenReturn(Optional.empty());

        assertFalse(tokenService.isInvalidated(request));
    }
}
