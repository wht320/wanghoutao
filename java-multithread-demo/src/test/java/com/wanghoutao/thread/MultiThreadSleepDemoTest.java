package com.wanghoutao.thread;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiThreadSleepDemoTest {

    @Test
    void workersSleepInParallel() throws InterruptedException {
        Instant start = Instant.now();
        MultiThreadSleepDemo.run(3, 200);
        long elapsedMillis = Duration.between(start, Instant.now()).toMillis();

        assertTrue(elapsedMillis >= 200, "should sleep at least the requested duration");
        assertTrue(elapsedMillis < 600, "three workers should overlap instead of sleeping one by one");
    }
}
