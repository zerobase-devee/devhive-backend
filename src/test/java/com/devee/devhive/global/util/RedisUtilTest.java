package com.devee.devhive.global.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisUtilTest {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testLockAndUnlock() throws InterruptedException {
        // given
        int numThreads = 10;
        String lockKey = "testLockKey";
        final AtomicInteger success = new AtomicInteger(0);


        // when
        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // 모든 스레드가 시작될 때까지 대기
                    boolean isLocked = redisUtil.getLock(lockKey, 5);
                    if (isLocked) {
                        success.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 스레드 실행이 완료될 때까지 대기합니다.
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        // then
        Assertions.assertEquals(1, success.get());
    }
}