package com.whatsontonight.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 取得呼叫者 IP（頻率限制用）。
 * proxy-hops = 0：直接用連線 IP（本機）。
 * proxy-hops = N：取 X-Forwarded-For 從右邊數來第 N 個值。
 * 不取第一個值，因為最左邊的值可以被呼叫者自己偽造；proxy 是往右邊附加的。
 */
@Component
public class ClientIp {
    private final int proxyHops;

    public ClientIp(@Value("${wot.proxy-hops:0}") int proxyHops) {
        this.proxyHops = proxyHops;
    }

    public String of(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (proxyHops <= 0 || xff == null || xff.isBlank()) {
            return req.getRemoteAddr();
        }
        String[] parts = xff.split(",");
        int idx = Math.max(0, parts.length - proxyHops);
        return parts[idx].trim();
    }
}
