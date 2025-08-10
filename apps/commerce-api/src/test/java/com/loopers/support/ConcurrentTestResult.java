package com.loopers.support;

import lombok.Data;

import java.util.List;

public class ConcurrentTestResult<T>  {
    private final List<T> successes;
    private final List<Throwable> errors;

    public ConcurrentTestResult(List<T> successes, List<Throwable> errors) {
        this.successes = successes;
        this.errors = errors;
    }

    public int successCount() {
        return successes.size();
    }

    public int failureCount() {
        return errors.size();
    }

    public long failureCountOf(Class<? extends Throwable> type) {
        return errors.stream().filter(type::isInstance).count();
    }

    public List<T> getSuccesses() {
        return successes;
    }

    public List<Throwable> getErrors() {
        return errors;
    }
}
