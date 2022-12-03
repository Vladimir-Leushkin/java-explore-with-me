package explore.with.me.comment.dto;

import explore.with.me.event.model.Event;
import explore.with.me.user.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDto {
    private Long id;
    private String text;
    private Event event;
    private User commentator;
    private LocalDateTime createdDate;
    private LocalDateTime editDate;
}
