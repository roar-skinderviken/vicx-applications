package no.vicx.backend.jwt;

import no.vicx.backend.jwt.github.GitHubJwtFromOpaqueProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import static no.vicx.backend.testconfiguration.TestSecurityConfig.VALID_JWT_STRING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CompositeJwtDecoderTest {

    @Mock
    JwtDecoder nimbusJwtDecoder;

    @Mock
    GitHubJwtFromOpaqueProducer gitHubJwtFromOpaqueProducer;

    @InjectMocks
    CompositeJwtDecoder sut;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void decode_givenValidJwt_expectJwt() {
        Jwt decodedJwt = mock(Jwt.class);
        when(nimbusJwtDecoder.decode(VALID_JWT_STRING)).thenReturn(decodedJwt);

        Jwt result = sut.decode(VALID_JWT_STRING);

        assertNotNull(result);
        verify(nimbusJwtDecoder).decode(VALID_JWT_STRING);
    }

    @Test
    void decode_givenOpaqueToken_expectJwt() {
        String token = "opaque-token";

        Jwt decodedJwt = mock(Jwt.class);
        when(gitHubJwtFromOpaqueProducer.createJwt(token)).thenReturn(decodedJwt);

        Jwt result = sut.decode(token);

        assertNotNull(result);
        verify(gitHubJwtFromOpaqueProducer).createJwt(token);

    }

    @Test
    void decode_givenNullToken_expectException() {
        assertThrows(JwtException.class, () ->
                sut.decode(null), "Expected JwtException when token is null");
    }

    @Test
    void decode_givenEmptyToken_expectException() {
        assertThrows(JwtException.class, () ->
                sut.decode(""), "Expected JwtException when token is empty");
    }

    @Test
    void isJwt_givenValidJwt_expectTrue() {
        assertTrue(CompositeJwtDecoder.isJwt(VALID_JWT_STRING));
    }

    @Test
    void isJwt_givenInvalidJwt_expectFalse() {
        String invalidJwtToken = "invalid.token";
        assertFalse(CompositeJwtDecoder.isJwt(invalidJwtToken));
    }

    @Test
    void isJwt_givenNonBase64EncodedParts() {
        String invalidJwtToken = "header.payload.signature";
        assertFalse(CompositeJwtDecoder.isJwt(invalidJwtToken));
    }
}