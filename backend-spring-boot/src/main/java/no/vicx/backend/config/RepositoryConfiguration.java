package no.vicx.backend.config;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.user.VicxUser;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {CalcEntry.class, VicxUser.class})
@EntityScan(basePackageClasses = {CalcEntry.class, VicxUser.class})
public class RepositoryConfiguration {
}
