package com.loopers.support.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CacheRedisRepository implements CacheRepository {

    private static final String KEY_PREFIX = "v1:";
    private static final String LOCK_PREFIX = "lock:";
    private static final String NULL_VALUE = "__NULL__";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);
    private static final Duration LOCK_TTL = Duration.ofSeconds(5);
    private static final String CACHE_METRIC_NAME = "cache.requests";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    public CacheRedisRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public <T> T cacheAside(String key, Supplier<T> dbFetcher, TypeReference<T> typeRef) {
        return cacheAside(key, dbFetcher, typeRef, DEFAULT_TTL);
    }

    @Override
    public <T> T cacheAside(String key, Supplier<T> dbFetcher, Class<T> clazz) {
        return cacheAside(key, dbFetcher, new TypeReference<>() {}, DEFAULT_TTL);
    }

    @Override
    public <T> T cacheAside(String key, Supplier<T> dbFetcher, TypeReference<T> typeRef, Duration ttl) {
        String namespacedKey = KEY_PREFIX + key;
        try {
            String jsonValue = redisTemplate.opsForValue().get(namespacedKey);
            if (jsonValue != null) {
                recordHit(key);
                if (NULL_VALUE.equals(jsonValue)) {
                    return null;
                }
                return objectMapper.readValue(jsonValue, typeRef);
            }

            recordMiss(key);
            return loadFromDbAndCache(namespacedKey, dbFetcher, typeRef, ttl);

        } catch (Exception e) {
            log.warn("Cannot operate cache for key '{}'. Falling back to DB.", key, e);
            return dbFetcher.get();
        }
    }

    private <T> T loadFromDbAndCache(String namespacedKey, Supplier<T> dbFetcher, TypeReference<T> typeRef, Duration ttl) throws InterruptedException, JsonProcessingException {
        String lockKey = LOCK_PREFIX + namespacedKey;
        boolean locked = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TTL));

        if (locked) {
            try {
                T dbValue = dbFetcher.get();
                String jsonValueToCache = (dbValue == null) ? NULL_VALUE : objectMapper.writeValueAsString(dbValue);
                long jitterTtl = getJitterTtl(ttl);
                redisTemplate.opsForValue().set(namespacedKey, jsonValueToCache, jitterTtl, TimeUnit.SECONDS);
                return dbValue;
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            TimeUnit.MILLISECONDS.sleep(50); // Spin-wait
            String jsonValue = redisTemplate.opsForValue().get(namespacedKey);
            if (jsonValue != null) {
                return NULL_VALUE.equals(jsonValue) ? null : objectMapper.readValue(jsonValue, typeRef);
            }
            // Fallback for safety, though it shouldn't be reached if lock holder sets the key.
            return dbFetcher.get();
        }
    }

    @Override
    public <K, V> Map<K, V> cacheAsideBulk(Collection<K> keys, Function<Collection<K>, Map<K, V>> dbBulkFetcher, Function<K, String> keyFn, Class<V> valueType) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> redisKeys = keys.stream().map(keyFn).map(k -> KEY_PREFIX + k).toList();
        List<String> cachedValues = redisTemplate.opsForValue().multiGet(redisKeys);

        Map<K, V> result = new java.util.HashMap<>();
        List<K> missedKeys = new ArrayList<>();
        List<K> keyList = new ArrayList<>(keys);

        for (int i = 0; i < keyList.size(); i++) {
            String value = (cachedValues != null) ? cachedValues.get(i) : null;
            K key = keyList.get(i);
            if (value != null) {
                if (!NULL_VALUE.equals(value)) {
                    try {
                        result.put(key, objectMapper.readValue(value, valueType));
                    } catch (JsonProcessingException e) {
                        log.warn("Failed to deserialize value for key '{}'. Will fetch from DB.", key, e);
                        missedKeys.add(key);
                    }
                }
            } else {
                missedKeys.add(key);
            }
        }

        if (!missedKeys.isEmpty()) {
            Map<K, V> dbValues = dbBulkFetcher.apply(missedKeys);
            try {
                redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                    dbValues.forEach((key, value) -> {
                        try {
                            String redisKey = KEY_PREFIX + keyFn.apply(key);
                            String jsonValue = objectMapper.writeValueAsString(value);
                            connection.stringCommands().set(redisKey.getBytes(StandardCharsets.UTF_8), jsonValue.getBytes(StandardCharsets.UTF_8));
                        } catch (JsonProcessingException e) {
                            log.error("Failed to serialize value for bulk caching, key: {}", key, e);
                        }
                    });
                    return null;
                });
            } catch (DataAccessException e) {
                log.warn("Failed to cache bulk results due to Redis error.", e);
            }
            result.putAll(dbValues);
        }

        return result;
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(KEY_PREFIX + key);
        log.info("Cache evicted for key: {}", key);
    }

    @Override
    public void evictByPrefix(String prefix) {
        String scanPattern = KEY_PREFIX + prefix + "*";
        log.info("Start evicting caches with prefix: {}", prefix);
        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(scanPattern).count(1000).build())) {
                    cursor.forEachRemaining(key -> connection.del(key));
                }
                return null;
            });
        } catch (DataAccessException e) {
            log.warn("Failed to evict caches with prefix '{}' due to Redis error.", prefix, e);
        }
    }

    private long getJitterTtl(Duration baseTtl) {
        long baseSeconds = baseTtl.toSeconds();
        if (baseSeconds <= 0) return 0;
        long jitter = (long) (baseSeconds * 0.2);
        return baseSeconds + ThreadLocalRandom.current().nextLong(-jitter, jitter + 1);
    }

    private void recordHit(String key) {
        meterRegistry.counter(CACHE_METRIC_NAME, List.of(Tag.of("type", "hit"), Tag.of("key", key))).increment();
    }

    private void recordMiss(String key) {
        meterRegistry.counter(CACHE_METRIC_NAME, List.of(Tag.of("type", "miss"), Tag.of("key", key))).increment();
    }
}
