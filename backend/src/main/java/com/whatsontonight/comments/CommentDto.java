package com.whatsontonight.comments;

/**
 * API 2 回傳的一筆留言。兩種來源格式一致，沒有值的欄位為 null。
 * id 是在來源表的 id，兩張表各自編號，要搭配 source 辨識。
 */
public record CommentDto(
        long id,
        String msg,
        String type,
        String who,
        String remark,
        String name,
        String source
) {
    public static final String SOURCE_LIVE_TXT = "liveTxt";
    public static final String SOURCE_BIRTH_MSG = "birthMsg";
}
