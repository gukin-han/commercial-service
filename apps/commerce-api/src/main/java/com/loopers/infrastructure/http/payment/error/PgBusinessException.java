package com.loopers.infrastructure.http.payment.error;

public class PgBusinessException extends RuntimeException { // 재시도 불가
    public PgBusinessException(String code, String message) {
        super("PG business error. code=" + code + ", msg=" + message);
    }
}
