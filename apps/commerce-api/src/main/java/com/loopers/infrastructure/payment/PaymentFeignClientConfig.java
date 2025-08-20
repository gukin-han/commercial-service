package com.loopers.infrastructure.payment;

import feign.Request;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class PaymentFeignClientConfig {
    @Bean
    public Request.Options pgClientOptions() {
        Duration connectTimeout = Duration.of(3000, ChronoUnit.MILLIS);
        Duration readTimeout = Duration.of(5000, ChronoUnit.MILLIS);
        return new Request.Options(connectTimeout, readTimeout, false);
    }
}
