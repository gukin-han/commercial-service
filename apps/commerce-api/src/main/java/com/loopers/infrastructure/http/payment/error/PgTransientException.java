package com.loopers.infrastructure.http.payment.error;

public class PgTransientException extends RuntimeException {
    public PgTransientException(String code, String message) {
        super("PG transient error. code=" + code + ", msg=" + message);
    }
}
