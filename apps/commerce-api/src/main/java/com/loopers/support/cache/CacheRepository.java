package com.loopers.support.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Cache-Aside 패턴을 구현한 캐시 저장소 인터페이스.
 */
public interface CacheRepository {

    /**
     * 캐시를 조회하고, 없으면 DB에서 조회하여 캐시에 저장 후 반환합니다. (기본 TTL 사용)
     *
     * @param key       캐시 키
     * @param dbFetcher 캐시 미스 시 데이터를 조회할 함수
     * @param typeRef   반환 타입 정보
     * @param <T>       반환 타입
     * @return 캐시되거나 DB에서 조회된 데이터
     */
    <T> T cacheAside(String key, Supplier<T> dbFetcher, TypeReference<T> typeRef);

    /**
     * cacheAside의 편의 메소드. TypeReference 대신 Class를 사용합니다.
     */
    <T> T cacheAside(String key, Supplier<T> dbFetcher, Class<T> clazz);

    /**
     * 캐시를 조회하고, 없으면 DB에서 조회하여 캐시에 저장 후 반환합니다. (사용자 정의 TTL 사용)
     *
     * @param ttl 캐시 만료 시간
     */
    <T> T cacheAside(String key, Supplier<T> dbFetcher, TypeReference<T> typeRef, Duration ttl);

    /**
     * 여러 키를 한 번에 조회하는 벌크(bulk) 연산입니다.
     *
     * @param keys          조회할 키 목록
     * @param dbBulkFetcher 캐시 미스된 키들로 DB를 조회할 함수
     * @param keyFn         키 객체에서 캐시 키(String)를 생성하는 함수
     * @param valueType     값의 타입
     * @param <K>           키의 타입
     * @param <V>           값의 타입
     * @return 키-값 Map 형태로 된 데이터
     */
    <K, V> Map<K, V> cacheAsideBulk(Collection<K> keys, Function<Collection<K>, Map<K, V>> dbBulkFetcher, Function<K, String> keyFn, Class<V> valueType);

    /**
     * 특정 키의 캐시를 무효화합니다.
     *
     * @param key 무효화할 캐시 키
     */
    void evict(String key);

    /**
     * 특정 접두사(prefix)로 시작하는 모든 캐시를 무효화합니다.
     * 주의: 이 연산은 SCAN을 사용하므로 운영 환경에서는 주의해서 사용해야 합니다.
     *
     * @param prefix 무효화할 캐시 키의 접두사
     */
    void evictByPrefix(String prefix);
}
