package com.whatsontonight.birthmsg;

import java.util.Map;

import com.whatsontonight.common.ApiException;
import com.whatsontonight.common.ClientIp;
import com.whatsontonight.common.RateLimiter;
import com.whatsontonight.setting.ApiLimits;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** API 3：寫入生日留言。寫入後 isOk = false，人工審核通過才公開。 */
@RestController
public class BirthMsgController {
    private final JdbcTemplate jdbc;
    private final RateLimiter rateLimiter;
    private final ClientIp clientIp;
    private final ApiLimits limits;

    public BirthMsgController(JdbcTemplate jdbc, RateLimiter rateLimiter, ClientIp clientIp, ApiLimits limits) {
        this.jdbc = jdbc;
        this.rateLimiter = rateLimiter;
        this.clientIp = clientIp;
        this.limits = limits;
    }

    @PostMapping("/api/birthmsg")
    public Map<String, Object> create(@RequestBody(required = false) Map<String, Object> body,
                                      HttpServletRequest req) {
        // 先算次數再檢核：亂送格式錯誤的請求也會被限流
        rateLimiter.check("birthmsg", clientIp.of(req), limits.birthMsgPerMinute());
        if (body == null) throw ApiException.badRequest("body 必須是 JSON 物件");

        BirthMsgValidator.Cleaned c = BirthMsgValidator.validate(body.get("name"), body.get("msg"));
        jdbc.update("""
                insert into "WOT_BirthMsg" ("name", "msg", "isOk", "createTime")
                values (?, ?, false, now())
                """, c.name(), c.msg());
        return Map.of("isSuccess", true);
    }
}
