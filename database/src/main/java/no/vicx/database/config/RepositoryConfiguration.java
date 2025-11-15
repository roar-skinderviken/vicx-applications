package no.vicx.database.config;

import no.vicx.database.calculator.CalcEntry;
import no.vicx.database.user.VicxUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EnableJpaRepositories(basePackageClasses = {CalcEntry.class, VicxUser.class})
@EntityScan(basePackageClasses = {CalcEntry.class, VicxUser.class})
public class RepositoryConfiguration {
}
