package com.whatsontonight.birthmsg;

import java.util.regex.Pattern;

import com.whatsontonight.common.ApiException;

/**
 * 生日留言檢核，規則見 backend/API.md 第 3 節。
 * 只依賴 JDK，方便單獨測試。
 *
 * SQL injection 不在這裡處理（由參數綁定處理），所以不擋 select、drop、' 這類字。
 */
public final class BirthMsgValidator {
    public static final int NAME_MAX = 40;
    public static final int MSG_MAX = 200;

    /** 網頁語法：HTML 標籤、危險協定、事件屬性。 */
    private static final Pattern UNSAFE = Pattern.compile(
            "<[a-z/!?]"                    // <script、</div、<!--、<?xml
            + "|javascript\\s*:"
            + "|vbscript\\s*:"
            + "|data\\s*:\\s*text/html"
            + "|\\bon[a-z]+\\s*=",         // onerror=、onclick =
            Pattern.CASE_INSENSITIVE);

    public record Cleaned(String name, String msg) {}

    private BirthMsgValidator() {}

    public static Cleaned validate(Object rawName, Object rawMsg) {
        if (!(rawName instanceof String n)) throw ApiException.badRequest("name 必須是字串");
        if (!(rawMsg instanceof String m)) throw ApiException.badRequest("msg 必須是字串");

        String name = clean(n, false);
        String msg = clean(m, true);

        if (name.isEmpty()) throw ApiException.badRequest("name 不可空白");
        if (msg.isEmpty()) throw ApiException.badRequest("msg 不可空白");

        if (name.codePointCount(0, name.length()) > NAME_MAX) {
            throw ApiException.badRequest("name 最多 " + NAME_MAX + " 字");
        }
        if (msg.codePointCount(0, msg.length()) > MSG_MAX) {
            throw ApiException.badRequest("msg 最多 " + MSG_MAX + " 字");
        }

        if (UNSAFE.matcher(name).find() || UNSAFE.matcher(msg).find()) {
            throw ApiException.badRequest("內容含不允許的字元");
        }
        return new Cleaned(name, msg);
    }

    /**
     * 移除控制字元（keepNewline 時保留 \n）、零寬字元、文字方向控制字元，再去除前後空白。
     */
    static String clean(String s, boolean keepNewline) {
        String t = s.replace("\r\n", "\n").replace('\r', '\n');
        StringBuilder sb = new StringBuilder(t.length());
        t.codePoints().forEach(cp -> {
            if (cp == '\n') {
                if (keepNewline) sb.append('\n');
                return;
            }
            if (Character.getType(cp) == Character.CONTROL) return;  // 其他 C0 / C1 控制字元
            if (isInvisible(cp)) return;
            sb.appendCodePoint(cp);
        });
        return sb.toString().strip();
    }

    private static boolean isInvisible(int cp) {
        return (cp >= 0x200B && cp <= 0x200F)   // 零寬空白、零寬連接、LRM / RLM
            || (cp >= 0x202A && cp <= 0x202E)   // 文字方向覆寫（可用來偽裝內容）
            || (cp >= 0x2066 && cp <= 0x2069)   // 文字方向隔離
            || cp == 0x2060                     // word joiner
            || cp == 0xFEFF;                    // BOM / 零寬不換行空白
    }
}
