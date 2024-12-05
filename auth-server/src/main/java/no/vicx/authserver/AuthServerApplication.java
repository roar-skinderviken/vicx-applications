package no.vicx.authserver;

import no.vicx.authserver.config.DefaultUserProperties;
import no.vicx.authserver.config.OAuthProperties;
import no.vicx.database.user.VicxUser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties({OAuthProperties.class, DefaultUserProperties.class})
@EnableJpaRepositories(basePackageClasses = VicxUser.class)
@EntityScan(basePackageClasses = VicxUser.class)
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
