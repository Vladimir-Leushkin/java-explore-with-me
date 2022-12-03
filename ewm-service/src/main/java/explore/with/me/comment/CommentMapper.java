package explore.with.me.comment;

import explore.with.me.comment.dto.CommentDto;
import explore.with.me.comment.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent(),
                comment.getCommentator(),
                comment.getCreatedDate(),
                comment.getEditDate()
        );
    }
}
