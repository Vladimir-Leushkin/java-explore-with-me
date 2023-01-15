package explore.with.me.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.with.me.event.dto.EventShortDto;
import explore.with.me.user.dto.UserDtoShort;
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
    private EventShortDto event;
    private UserDtoShort commentator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editDate;
}
