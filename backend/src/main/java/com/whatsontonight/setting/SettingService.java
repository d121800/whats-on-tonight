package com.whatsontonight.setting;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

/**
 * 讀取 WOT_Setting，整張表快取在記憶體，過期才重讀。
 * 在 Supabase 改了設定，最晚 setting-cache-seconds 秒後生效，不用重新部署。
 */
@Service
public class SettingService {
    private static final Logger log = LoggerFactory.getLogger(SettingService.class);

    public static final String LIVETYPE = "livetype";
    public static final String WHO = "who";
    public static final String API = "api";

    private final JdbcTemplate jdbc;
    private final long ttlMs;

    private volatile Map<String, Map<String, String>> cache = Map.of();
    private volatile long loadedAt = 0;

    public SettingService(JdbcTemplate jdbc, @Value("${wot.setting-cache-seconds:300}") long ttlSeconds) {
        this.jdbc = jdbc;
        this.ttlMs = ttlSeconds * 1000;
    }

    private Map<String, Map<String, String>> current() {
        if (System.currentTimeMillis() - loadedAt >= ttlMs) {
            refresh();
        }
        return cache;
    }

    private synchronized void refresh() {
        if (System.currentTimeMillis() - loadedAt < ttlMs) return; // 別的執行緒剛讀完
        try {
            Map<String, Map<String, String>> next = new HashMap<>();
            jdbc.query("""
                    select "type", "name", "value" from "WOT_Setting"
                    """, (RowCallbackHandler) rs -> {
                next.computeIfAbsent(rs.getString("type"), k -> new HashMap<>())
                        .put(rs.getString("name"), rs.getString("value"));
            });
            cache = next;
            loadedAt = System.currentTimeMillis();
        } catch (RuntimeException e) {
            if (loadedAt == 0) throw e;            // 從沒讀成功過，只能報錯
            log.warn("重新讀取 WOT_Setting 失敗，沿用舊設定", e);
            loadedAt = System.currentTimeMillis(); // 下個週期再試
        }
    }

    public boolean has(String type, String name) {
        return name != null && current().getOrDefault(type, Map.of()).containsKey(name);
    }

    public Set<String> names(String type) {
        return current().getOrDefault(type, Map.of()).keySet();
    }

    /** 讀整數設定；沒有這筆或不是整數就用 def。 */
    public int getInt(String type, String name, int def) {
        String v = current().getOrDefault(type, Map.of()).get(name);
        if (v == null) return def;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            log.warn("WOT_Setting {}/{} 不是整數：{}，改用預設 {}", type, name, v, def);
            return def;
        }
    }
}
