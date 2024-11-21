package no.vicx.authserver.config;

import no.vicx.database.user.VicxUser;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = VicxUser.class)
@EntityScan(basePackageClasses = VicxUser.class)
public class RepositoryConfiguration {
}
