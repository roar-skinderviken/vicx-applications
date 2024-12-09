package no.vicx.backend.user.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecaptchaResponseVmTest {

    @Test
    void mappingFromJsonToRecaptchaResponseVm_expectPopulatedInstance() throws JsonProcessingException {
        var json = """
                {
                    "success": true,
                    "challenge_ts": "2024-12-09T14:30:00+00:00",
                    "hostname": "~hostname~",
                    "error-codes": ["~error-code1~","~error-code2~"]
                }""";

        var sut = objectMapper.readValue(json, RecaptchaResponseVm.class);

        assertTrue(sut.success());
        assertEquals("2024-12-09T14:30:00+00:00", sut.challengeTimestamp());
        assertEquals("~hostname~", sut.hostname());
        assertThat(Arrays.asList(sut.errorCodes()), contains("~error-code1~", "~error-code2~"));
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();
}