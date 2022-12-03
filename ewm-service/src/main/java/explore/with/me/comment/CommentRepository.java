package explore.with.me.comment;

import explore.with.me.comment.model.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByCommentatorId(Long userId, PageRequest pageRequest);

    List<Comment> findAllByEventId(Long eventId, PageRequest pageRequest);

    @Query(value = "select * " +
            "from COMMENTS " +
            "where user_id like ?1 " +
            "and event_id like ?2 ", nativeQuery = true)
    Comment findAllByCommentatorIdAndEventId(Long userId, Long eventId);
}
