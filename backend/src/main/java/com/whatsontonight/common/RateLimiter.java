package com.whatsontonight.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每 IP 每分鐘次數限制（固定一分鐘視窗，存在記憶體）。
 * 服務重啟會歸零；Render 免費方案只有一台機器，夠用。
 */
@Component
public class RateLimiter {
    private static final long WINDOW_MS = 60_000;

    private record Window(long start, int count) {}

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    /** 超過限制丟 429。perMinute ≤ 0 視為不限制。 */
    public void check(String bucket, String ip, int perMinute) {
        if (perMinute <= 0) return;
        long now = System.currentTimeMillis();
        Window w = windows.compute(bucket + "|" + ip, (k, old) ->
                (old == null || now - old.start() >= WINDOW_MS)
                        ? new Window(now, 1)
                        : new Window(old.start(), old.count() + 1));
        if (w.count() > perMinute) {
            throw ApiException.tooManyRequests();
        }
    }

    /** 每 5 分鐘清掉過期紀錄，避免記憶體一直長。 */
    @Scheduled(fixedRate = 300_000)
    void cleanup() {
        long now = System.currentTimeMillis();
        windows.values().removeIf(w -> now - w.start() >= WINDOW_MS);
    }
}
