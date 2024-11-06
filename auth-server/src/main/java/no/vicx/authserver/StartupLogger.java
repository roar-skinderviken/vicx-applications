package no.vicx.authserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${app-user.name}")
    private String username;

    @Override
    public void run(String... args) {
        logger.info("Username: {}", username);
    }
}