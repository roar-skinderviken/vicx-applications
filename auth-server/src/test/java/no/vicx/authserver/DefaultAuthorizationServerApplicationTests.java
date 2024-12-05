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

import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for the sample Authorization Server.
 *
 * @author Daniel Garnier-Moiroux
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DefaultAuthorizationServerApplicationTests {
    private static final String REDIRECT_URI = "http://localhost:3000/api/auth/callback/next-app-client";

    private static final String AUTHORIZATION_REQUEST = UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "next-app-client")
            .queryParam("scope", "openid")
            .queryParam("state", "some-state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString();

    @Autowired
    private WebClient webClient;

    @BeforeEach
    public void setUp() {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getCookieManager().clearCookies();    // log out
    }

    @Test
    public void whenLoginSuccessfulThenDisplayNotFoundError() throws IOException {
        HtmlPage page = this.webClient.getPage("/");

        assertLoginPage(page);

        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        var signInResponse = signIn(page, "password").getWebResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), signInResponse.getStatusCode());
    }

    @Test
    public void whenLoginFailsThenDisplayBadCredentials() throws IOException {
        HtmlPage page = this.webClient.getPage("/");

        HtmlPage loginErrorPage = signIn(page, "wrong-password");

        var alert = loginErrorPage.querySelector("div[role=\"alert\"]");
        assertNotNull(alert);
        assertEquals("Bad credentials", alert.getTextContent());
    }

    @Test
    public void whenNotLoggedInAndRequestingTokenThenRedirectsToLogin() throws IOException {
        HtmlPage page = this.webClient.getPage(AUTHORIZATION_REQUEST);

        assertLoginPage(page);
    }

    @Test
    public void whenLoggingInAndRequestingTokenThenRedirectsToClientApplication() throws IOException {
        // Log in
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        signIn(this.webClient.getPage("/login"), "password");

        // Request token
        var response = this.webClient.getPage(AUTHORIZATION_REQUEST).getWebResponse();

        assertEquals(HttpStatus.MOVED_PERMANENTLY.value(), response.getStatusCode());

        var location = response.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");
    }

    private static <P extends Page> P signIn(HtmlPage page, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        usernameInput.type("user1");
        passwordInput.type(password);

        return signInButton.click();
    }

    private static void assertLoginPage(HtmlPage page) {
        assertThat(page.getUrl().toString()).endsWith("/login");

        assertNotNull(page.querySelector("input[name=\"username\"]"));
        assertNotNull(page.querySelector("input[name=\"password\"]"));

        var signInButton = page.querySelector("button");
        assertEquals("Sign in", signInButton.getTextContent());
    }
}
