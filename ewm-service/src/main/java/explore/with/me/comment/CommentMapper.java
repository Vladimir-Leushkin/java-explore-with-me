package explore.with.me.comment;

import explore.with.me.comment.dto.CommentDto;
import explore.with.me.comment.model.Comment;
import explore.with.me.event.EventMapper;
import explore.with.me.user.UserMapper;


public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                EventMapper.toEventShortDto(comment.getEvent()),
                UserMapper.toUserDtoShort(comment.getCommentator()),
                comment.getCreatedDate(),
                comment.getEditDate()
        );
    }
}
