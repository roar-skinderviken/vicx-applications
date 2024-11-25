package no.vicx.backend.jwt;

import no.vicx.backend.jwt.github.GitHubJwtFromOpaqueProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder compositeJwtDecoder(
            @Value("${auth-server.issuer-uri}") String issuerUri,
            GitHubJwtFromOpaqueProducer gitHubJwtFromOpaqueProducer) {
        return new CompositeJwtDecoder(
                NimbusJwtDecoder.withIssuerLocation(issuerUri).build(),
                gitHubJwtFromOpaqueProducer
        );
    }
}