package no.vicx.backend.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration(proxyBeanMethods = false)
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager =
        CaffeineCacheManager().apply {
            registerCustomCache(
                "ESPORT",
                Caffeine
                    .newBuilder()
                    .expireAfterWrite(30, TimeUnit.SECONDS)
                    .buildAsync(),
            )

            registerCustomCache(
                "GITHUB_OPAQUE_PRINCIPALS",
                Caffeine
                    .newBuilder()
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .build(),
            )

            registerCustomCache(
                "RECAPTCHA_TOKENS",
                Caffeine
                    .newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build(),
            )
        }
}
