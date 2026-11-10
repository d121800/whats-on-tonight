package com.whatsontonight.comments;

import java.util.ArrayList;
import java.util.List;

import com.whatsontonight.common.ApiException;
import com.whatsontonight.setting.ApiLimits;
import com.whatsontonight.setting.SettingService;
import org.springframework.stereotype.Service;

/** API 2 的規則，見 backend/API.md 第 2 節。 */
@Service
public class CommentsService {
    public static final String BIRTHDAY = "birthday";

    private final CommentRepository repo;
    private final SettingService settings;
    private final ApiLimits limits;

    public CommentsService(CommentRepository repo, SettingService settings, ApiLimits limits) {
        this.repo = repo;
        this.settings = settings;
        this.limits = limits;
    }

    public List<CommentDto> get(String typeParam, String whoParam, String limitParam, String orderParam) {
        int limit = parseLimit(limitParam);
        boolean latest = parseOrderLatest(orderParam);

        // 不在 WOT_Setting 裡的 type / who 一律當作沒帶
        String type = blankToNull(typeParam);
        if (!settings.has(SettingService.LIVETYPE, type)) type = null;
        String who = blankToNull(whoParam);
        if (!settings.has(SettingService.WHO, who)) who = null;

        if (BIRTHDAY.equals(type)) {
            return birthday(limit, latest);
        }
        return repo.findLiveTxt(type, who);
    }

    private List<CommentDto> birthday(int limit, boolean latest) {
        List<CommentDto> result = new ArrayList<>(repo.findApprovedBirthMsg(limit, latest));
        int missing = limit - result.size();
        if (missing > 0) {
            result.addAll(repo.findBirthdayFill(missing));
        }
        return result;
    }

    /** 沒帶 → 預設；不是正整數 → 400；超過上限 → 上限。 */
    int parseLimit(String s) {
        if (blankToNull(s) == null) return limits.birthdayLimitDefault();
        int n;
        try {
            n = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw ApiException.badRequest("limit 必須是正整數");
        }
        if (n < 1) throw ApiException.badRequest("limit 必須是正整數");
        return Math.min(n, limits.birthdayLimitMax());
    }

    /** 沒帶或 random → false；latest → true；其他 → 400。 */
    static boolean parseOrderLatest(String s) {
        String v = blankToNull(s);
        if (v == null || v.equals("random")) return false;
        if (v.equals("latest")) return true;
        throw ApiException.badRequest("order 只能是 random 或 latest");
    }

    /** null、空白、字串 "null" 都視為沒帶。 */
    static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty() || t.equalsIgnoreCase("null")) return null;
        return t;
    }
}
