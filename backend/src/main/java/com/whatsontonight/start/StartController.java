package com.whatsontonight.start;

import java.util.Map;

import com.whatsontonight.common.ClientIp;
import com.whatsontonight.common.RateLimiter;
import com.whatsontonight.setting.ApiLimits;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** API 1：記錄遊戲開始時間（統計用）。 */
@RestController
public class StartController {
    private final JdbcTemplate jdbc;
    private final RateLimiter rateLimiter;
    private final ClientIp clientIp;
    private final ApiLimits limits;

    public StartController(JdbcTemplate jdbc, RateLimiter rateLimiter, ClientIp clientIp, ApiLimits limits) {
        this.jdbc = jdbc;
        this.rateLimiter = rateLimiter;
        this.clientIp = clientIp;
        this.limits = limits;
    }

    @PostMapping("/api/start")
    public Map<String, Object> start(HttpServletRequest req) {
        rateLimiter.check("start", clientIp.of(req), limits.startPerMinute());
        jdbc.update("""
                insert into "WOT_StartTime" ("createTime") values (now())
                """);
        return Map.of("isSuccess", true);
    }
}
