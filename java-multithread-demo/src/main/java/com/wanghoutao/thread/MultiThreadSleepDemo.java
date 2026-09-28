package com.wanghoutao.thread;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 同时启动多个线程，每个线程睡眠 1 分钟后结束。
 * <p>
 * 用法：
 * {@code java com.wanghoutao.thread.MultiThreadSleepDemo [线程数] [睡眠毫秒]}
 * 默认 3 个线程、每个睡眠 60000 毫秒。
 */
public final class MultiThreadSleepDemo {

    public static final long DEFAULT_SLEEP_MILLIS = TimeUnit.MINUTES.toMillis(1);

    private MultiThreadSleepDemo() {
    }

    public static void main(String[] args) throws InterruptedException {
        int threadCount = parseInt(args, 0, 3);
        long sleepMillis = parseLong(args, 1, DEFAULT_SLEEP_MILLIS);
        run(threadCount, sleepMillis);
    }

    public static void run(int threadCount, long sleepMillis) throws InterruptedException {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("threadCount must be positive");
        }
        if (sleepMillis < 0) {
            throw new IllegalArgumentException("sleepMillis must not be negative");
        }

        System.out.printf("启动 %d 个线程，每个睡眠 %d 毫秒（%d 秒）%n",
                threadCount, sleepMillis, TimeUnit.MILLISECONDS.toSeconds(sleepMillis));

        Instant start = Instant.now();
        List<Thread> workers = new ArrayList<>(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            int id = i;
            Thread worker = new Thread(() -> sleepQuietly(id, sleepMillis), "worker-" + id);
            workers.add(worker);
            worker.start();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        System.out.printf("全部线程结束，总耗时 %d 秒%n", Duration.between(start, Instant.now()).toSeconds());
    }

    static void sleepQuietly(int id, long sleepMillis) {
        String name = Thread.currentThread().getName();
        System.out.printf("[%s] 线程 %d 开始，准备睡眠%n", name, id);
        try {
            Thread.sleep(sleepMillis);
            System.out.printf("[%s] 线程 %d 睡醒%n", name, id);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.printf("[%s] 线程 %d 被中断%n", name, id);
        }
    }

    private static int parseInt(String[] args, int index, int defaultValue) {
        if (args == null || args.length <= index || args[index].isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(args[index]);
    }

    private static long parseLong(String[] args, int index, long defaultValue) {
        if (args == null || args.length <= index || args[index].isBlank()) {
            return defaultValue;
        }
        return Long.parseLong(args[index]);
    }
}
