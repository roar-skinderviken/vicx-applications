/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.vicx.authserver;

import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Consent screen integration tests for the sample Authorization Server.
 *
 * @author Dmitriy Dubson
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DefaultAuthorizationServerConsentTests {

    @Autowired
    WebClient webClient;

    @MockitoBean
    OAuth2AuthorizationConsentService authorizationConsentService;

    private final String redirectUri = "http://localhost:3000/api/auth/callback/next-app-client";

    private final String authorizationRequestUri = UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "next-app-client")
            .queryParam("scope", "openid profile email")
            .queryParam("state", "state")
            .queryParam("redirect_uri", this.redirectUri)
            .toUriString();

    @BeforeEach
    public void setUp() {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getCookieManager().clearCookies();
        when(this.authorizationConsentService.findById(any(), any())).thenReturn(null);
    }

    @Test
    @WithMockUser("user1")
    public void whenUserConsentsToAllScopesThenReturnAuthorizationCode() throws IOException {
        final HtmlPage consentPage = this.webClient.getPage(this.authorizationRequestUri);
        assertThat(consentPage.getTitleText()).isEqualTo("Consent required");

        var scopeCheckBoxes = new ArrayList<HtmlCheckBoxInput>();
        consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
                scopeCheckBoxes.add((HtmlCheckBoxInput) scope));

        for (var scopeCheckBox : scopeCheckBoxes) {
            scopeCheckBox.click();
        }

        var scopeIds = new ArrayList<>();
        scopeCheckBoxes.forEach(scope -> {
            assertTrue(scope.isChecked());
            scopeIds.add(scope.getId());
        });

        assertThat(scopeIds).containsExactlyInAnyOrder("profile", "email");

        DomElement submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        this.webClient.getOptions().setRedirectEnabled(false);

        var approveConsentResponse = submitConsentButton.click().getWebResponse();
        assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), approveConsentResponse.getStatusCode());

        var location = approveConsentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(this.redirectUri);
        assertThat(location).contains("code=");
    }

    @Test
    @WithMockUser("user1")
    public void whenUserCancelsConsentThenReturnAccessDeniedError() throws IOException {
        final HtmlPage consentPage = this.webClient.getPage(this.authorizationRequestUri);
        assertEquals("Consent required", consentPage.getTitleText());

        DomElement cancelConsentButton = consentPage.querySelector("button[id='cancel-consent']");
        this.webClient.getOptions().setRedirectEnabled(false);

        var cancelConsentResponse = cancelConsentButton.click().getWebResponse();
        assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), cancelConsentResponse.getStatusCode());

        var location = cancelConsentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(this.redirectUri);
        assertThat(location).contains("error=access_denied");
    }
}
