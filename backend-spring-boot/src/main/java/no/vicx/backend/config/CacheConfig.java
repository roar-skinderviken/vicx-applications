package no.vicx.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        var cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("GITHUB_TOKENS",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .build());

        cacheManager.registerCustomCache("RECAPTCHA_TOKENS",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .build());

        cacheManager.registerCustomCache("ESPORT",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .buildAsync());

        return cacheManager;
    }
}