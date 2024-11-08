package no.vicx.backend.config;

import no.vicx.backend.calculator.repository.CalculatorEntity;
import no.vicx.database.user.VicxUser;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = {CalculatorEntity.class, VicxUser.class})
@EntityScan(basePackageClasses = {CalculatorEntity.class, VicxUser.class})
public class RepositoryConfiguration {
}
