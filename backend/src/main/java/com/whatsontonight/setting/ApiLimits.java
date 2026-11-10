package com.whatsontonight.setting;

import org.springframework.stereotype.Component;

/**
 * API 可調數值。來源：WOT_Setting type='api'；沒有那筆就用這裡的預設值。
 */
@Component
public class ApiLimits {
    private final SettingService settings;

    public ApiLimits(SettingService settings) {
        this.settings = settings;
    }

    /** POST /api/start 每 IP 每分鐘次數 */
    public int startPerMinute() {
        return settings.getInt(SettingService.API, "start.perMinute", 5);
    }

    /** POST /api/birthmsg 每 IP 每分鐘次數 */
    public int birthMsgPerMinute() {
        return settings.getInt(SettingService.API, "birthmsg.perMinute", 3);
    }

    /** GET /api/comments?type=birthday 的 limit 預設值 */
    public int birthdayLimitDefault() {
        return settings.getInt(SettingService.API, "birthday.limitDefault", 100);
    }

    /** GET /api/comments?type=birthday 的 limit 上限 */
    public int birthdayLimitMax() {
        return settings.getInt(SettingService.API, "birthday.limitMax", 500);
    }
}
