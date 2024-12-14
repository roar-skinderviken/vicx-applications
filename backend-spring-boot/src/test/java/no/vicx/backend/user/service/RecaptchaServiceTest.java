package no.vicx.backend.user.service;

import no.vicx.backend.config.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(RecaptchaService.class)
@Import(RestClientConfig.class)
class RecaptchaServiceTest {

    @Autowired
    private RecaptchaService sut;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void verifyToken_givenValidToken_expectTrue() {
        mockServer.expect(requestTo(EXPECTED_URL))
                .andRespond(withSuccess(validResponseBody, MediaType.APPLICATION_JSON));

        assertTrue(sut.verifyToken("some-token"));
    }

    @Test
    void verifyToken_givenErrorResponse_expectFalse() {
        mockServer.expect(requestTo(EXPECTED_URL))
                .andRespond(withSuccess(errorResponseBody, MediaType.APPLICATION_JSON));

        assertFalse(sut.verifyToken("some-token"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{}"})
    void verifyToken_givenEmptyResponseBody_expectFalse(String body) {
        mockServer.expect(requestTo(EXPECTED_URL))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        assertFalse(sut.verifyToken("some-token"));
    }

    private static final String EXPECTED_URL =
            "https://www.google.com/recaptcha/api/siteverify?secret=6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe&response=some-token";

    private static final String validResponseBody = """
            {
                "success": true,
                "challenge_ts": "~challenge_ts~",
                "hostname": "~hostname~",
                "error-codes": []
            }
            """;

    private static final String errorResponseBody = """
            {
                "success": false,
                "challenge_ts": "~challenge_ts~",
                "hostname": "~hostname~",
                "error-codes": ["Some error"]
            }
            """;
}
