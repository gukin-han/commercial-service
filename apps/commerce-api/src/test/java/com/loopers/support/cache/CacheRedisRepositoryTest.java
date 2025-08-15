package com.loopers.support.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CacheRedisRepositoryTest {

    @Autowired
    private CacheRedisRepository sut;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }


    @Nested
    @DisplayName("cacheAside 메소드는")
    class Describe_cacheAside {

        private final String key = "testKey";
        private final String namespacedKey = "v1:" + key;
        private final TypeReference<String> typeRef = new TypeReference<>() {
        };

        @Test
        @DisplayName("캐시에 값이 있으면 DB를 호출하지 않고 캐시 값을 반환한다")
        void whenCacheHit_thenReturnFromCacheAndNotCallDB() {
            // given
            String cachedValue = "cachedValue";
            redisTemplate.opsForValue().set(namespacedKey, "\"" + cachedValue + "\"");

            Supplier<String> dbFetcher = mock(Supplier.class);

            // when
            String result = sut.cacheAside(key, dbFetcher, typeRef);

            // then
            assertThat(result).isEqualTo(cachedValue);
            verify(dbFetcher, never()).get();
        }

        @Test
        @DisplayName("캐시에 값이 없으면 DB에서 값을 가져와 캐시에 저장하고 반환한다")
        void whenCacheMiss_thenLoadFromDBAndSetToCache() {
            // given
            String dbValue = "dbValue";
            Supplier<String> dbFetcher = () -> dbValue;

            // when
            String result = sut.cacheAside(key, dbFetcher, typeRef);

            // then
            assertThat(result).isEqualTo(dbValue);
            String cachedValue = redisTemplate.opsForValue().get(namespacedKey);
            assertThat(cachedValue).isEqualTo("\"" + dbValue + "\"");
        }

        @Test
        @DisplayName("DB 결과가 null일 때, NULL_VALUE를 캐시한다")
        void whenDbResultIsNull_thenCacheNullValue() {
            // given
            Supplier<String> nullDbFetcher = () -> null;

            // when
            String result = sut.cacheAside(key, nullDbFetcher, typeRef);

            // then
            assertThat(result).isNull();
            String cachedValue = redisTemplate.opsForValue().get(namespacedKey);
            assertThat(cachedValue).isEqualTo("__NULL__");
        }
    }
}
