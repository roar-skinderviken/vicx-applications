package no.vicx.ktor.cache

import com.github.benmanes.caffeine.cache.AsyncCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.util.concurrent.TimeUnit

class AsyncCacheWrapper<in K : Any, out V : Any>(
    expireAfterWrite: Long,
    expireAfterWriteUnit: TimeUnit,
    val computeFunc: suspend (K) -> V,
) {
    private val cache: AsyncCache<K, V> =
        Caffeine
            .newBuilder()
            .expireAfterWrite(expireAfterWrite, expireAfterWriteUnit)
            .buildAsync()

    suspend fun getOrCompute(key: K): V =
        coroutineScope {
            cache.get(key) { _, _ -> future { computeFunc(key) } }.await()
        }

    fun invalidate(key: K) = cache.synchronous().invalidate(key)
}
