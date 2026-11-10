package com.whatsontonight.comments;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** 所有 SQL 都用 ? 參數綁定，不拼接使用者輸入。 */
@Repository
public class CommentRepository {
    private final JdbcTemplate jdbc;

    public CommentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<CommentDto> LIVE_TXT = (rs, i) -> new CommentDto(
            rs.getLong("id"), rs.getString("msg"), rs.getString("type"),
            rs.getString("who"), rs.getString("remark"), null, CommentDto.SOURCE_LIVE_TXT);

    private static final RowMapper<CommentDto> BIRTH_MSG = (rs, i) -> new CommentDto(
            rs.getLong("id"), rs.getString("msg"), CommentsService.BIRTHDAY,
            null, null, rs.getString("name"), CommentDto.SOURCE_BIRTH_MSG);

    /**
     * 一般直播留言。type / who 為 null 代表「通用」「不限朋友」，只取該欄為 null 的留言。
     */
    public List<CommentDto> findLiveTxt(String type, String who) {
        StringBuilder sql = new StringBuilder("""
                select "id", "msg", "type", "who", "remark" from "WOT_LiveTxt" where
                """);
        List<Object> args = new ArrayList<>();
        if (type == null) {
            sql.append(" \"type\" is null");
        } else {
            sql.append(" (\"type\" is null or \"type\" = ?)");
            args.add(type);
        }
        if (who == null) {
            sql.append(" and \"who\" is null");
        } else {
            sql.append(" and (\"who\" is null or \"who\" = ?)");
            args.add(who);
        }
        return jdbc.query(sql.toString(), LIVE_TXT, args.toArray());
    }

    /** 已審核的生日留言。latest = 由新到舊，否則隨機。 */
    public List<CommentDto> findApprovedBirthMsg(int limit, boolean latest) {
        String order = latest ? "\"createTime\" desc" : "random()";
        return jdbc.query("""
                select "id", "name", "msg" from "WOT_BirthMsg"
                where "isOk" = true
                order by %s
                limit ?
                """.formatted(order), BIRTH_MSG, limit);
    }

    /** 生日補位：type 為 birthday 或通用，隨機取。who 不限。 */
    public List<CommentDto> findBirthdayFill(int limit) {
        return jdbc.query("""
                select "id", "msg", "type", "who", "remark" from "WOT_LiveTxt"
                where "type" = ? or "type" is null
                order by random()
                limit ?
                """, LIVE_TXT, CommentsService.BIRTHDAY, limit);
    }
}
