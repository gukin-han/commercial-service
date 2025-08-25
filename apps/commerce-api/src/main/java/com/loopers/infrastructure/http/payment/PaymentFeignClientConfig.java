package com.loopers.infrastructure.http.payment;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class PaymentFeignClientConfig {
    @Bean
    public feign.Retryer feignRetryer() {
        return feign.Retryer.NEVER_RETRY;
    }

    @Bean
    public Request.Options pgClientOptions() {
        Duration connectTimeout = Duration.of(3000, ChronoUnit.MILLIS);
        Duration readTimeout = Duration.of(5000, ChronoUnit.MILLIS);
        return new Request.Options(connectTimeout, readTimeout, false);
    }
}
