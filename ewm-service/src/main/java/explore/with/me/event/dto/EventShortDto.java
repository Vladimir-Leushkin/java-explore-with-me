package explore.with.me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.with.me.category.dto.CategoryDto;
import explore.with.me.user.dto.UserDtoShort;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserDtoShort initiator;
    private Boolean paid;
    private String title;
    private Long views;

}
