package com.whatsontonight.birthmsg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.whatsontonight.common.ApiException;
import org.junit.jupiter.api.Test;

class BirthMsgValidatorTest {

    private static String err(Object name, Object msg) {
        return assertThrows(ApiException.class, () -> BirthMsgValidator.validate(name, msg)).getMessage();
    }

    @Test
    void normalMessagePasses() {
        var c = BirthMsgValidator.validate("  小明  ", "生日快樂！\n一起慶生 <3");
        assertEquals("小明", c.name());
        assertEquals("生日快樂！\n一起慶生 <3", c.msg());
    }

    @Test
    void sqlLookingTextIsAllowed() {
        // SQL injection 由參數綁定處理，不擋字
        var c = BirthMsgValidator.validate("O'Brien", "I'm here; drop table everything -- lol");
        assertEquals("O'Brien", c.name());
    }

    @Test
    void blankRejected() {
        assertEquals("name 不可空白", err("   ", "hi"));
        assertEquals("msg 不可空白", err("a", "​​ "));
    }

    @Test
    void nonStringRejected() {
        assertEquals("name 必須是字串", err(null, "hi"));
        assertEquals("msg 必須是字串", err("a", 123));
    }

    @Test
    void lengthCountsCodePoints() {
        BirthMsgValidator.validate("🎂".repeat(40), "x");               // 40 個 emoji 可以
        assertEquals("name 最多 40 字", err("字".repeat(41), "x"));
        BirthMsgValidator.validate("a", "字".repeat(200));
        assertEquals("msg 最多 200 字", err("a", "字".repeat(201)));
    }

    @Test
    void htmlRejected() {
        String e = "內容含不允許的字元";
        assertEquals(e, err("a", "<script>alert(1)</script>"));
        assertEquals(e, err("a", "<img src=x onerror=alert(1)>"));
        assertEquals(e, err("<b>a</b>", "hi"));
        assertEquals(e, err("a", "點我 javascript:alert(1)"));
        assertEquals(e, err("a", "x\" onclick = \"alert(1)"));
        assertEquals(e, err("a", "<!-- hi -->"));
    }

    @Test
    void controlCharsRemoved() {
        var c = BirthMsgValidator.validate("a\u0000b\tc", "x‮y\r\nz");
        assertEquals("abc", c.name());
        assertEquals("xy\nz", c.msg());
    }

    @Test
    void nameNewlineRemoved() {
        assertEquals("ab", BirthMsgValidator.validate("a\nb", "x").name());
    }
}
