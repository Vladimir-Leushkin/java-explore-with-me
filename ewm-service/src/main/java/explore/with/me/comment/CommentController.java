package explore.with.me.comment;

import explore.with.me.comment.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/user/{userId}/event/{eventId}")
    public CommentDto createComment(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @RequestBody String text) {
        return commentService.createComment(userId, eventId, text);
    }

    @GetMapping("/user/{userId}")
    public List<CommentDto> readAllCommentsByUser(@PathVariable("userId") Long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return commentService.readAllCommentsByUser(userId, from, size);
    }

    @GetMapping("/{commId}")
    public CommentDto readCommentById(@PathVariable("commId") Long commId) {
        return commentService.readCommentById(commId);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> readAllCommentsByEvent(@PathVariable("eventId") Long eventId,
                                                   @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return commentService.readAllCommentsByEvent(eventId, from, size);
    }

    @PatchMapping("/{commId}/user/{userId}")
    public CommentDto updateComment(@PathVariable("commId") Long commId,
                                    @PathVariable("userId") Long userId,
                                    @RequestBody String text) {
        return commentService.updateComment(commId, userId, text);
    }

    @DeleteMapping("/{commId}")
    public void deleteComment(@PathVariable("commId") Long commId) {
        commentService.deleteComment(commId);
    }
}
