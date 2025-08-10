package com.loopers.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentTestRunner {

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    public static <T> ConcurrentTestResult<T> run(int threads, ThrowingSupplier<T> task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier start = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);

        List<T> successes = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();       // 동시 시작
                    T result = task.get();
                    successes.add(result);
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        boolean finished = done.await(30, TimeUnit.SECONDS);
        if (!finished) errors.add(new TimeoutException("concurrency test timed out"));

        pool.shutdownNow();
        return new ConcurrentTestResult<>(successes, errors);
    }
}
