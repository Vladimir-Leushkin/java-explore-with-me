package explore.with.me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.with.me.category.dto.CategoryDto;
import explore.with.me.user.dto.UserDtoShort;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventShortDto {
    private Long id;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private CategoryDto category;
    @PositiveOrZero
    private Integer confirmedRequests;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private UserDtoShort initiator;
    private Boolean paid;
    @NotBlank
    @Size(min =  3, max = 120)
    private String title;
    @PositiveOrZero
    private Integer views;

}
