package com.whatsontonight.comments;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** API 2：取得直播留言；type=birthday 時改取生日留言。 */
@RestController
public class CommentsController {
    private final CommentsService service;

    public CommentsController(CommentsService service) {
        this.service = service;
    }

    // 參數都用字串接，格式錯誤由 service 回我們自己的 400 訊息
    @GetMapping("/api/comments")
    public List<CommentDto> comments(@RequestParam(required = false) String type,
                                     @RequestParam(required = false) String who,
                                     @RequestParam(required = false) String limit,
                                     @RequestParam(required = false) String order) {
        return service.get(type, who, limit, order);
    }
}
