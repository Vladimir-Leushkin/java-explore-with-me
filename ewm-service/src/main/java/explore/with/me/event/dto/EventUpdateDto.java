package explore.with.me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventUpdateDto {
    private Long eventId;
    @Nullable
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @Nullable
    private Integer category;
    @Nullable
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @Future
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Nullable
    private Boolean paid;
    @Nullable
    private Integer participantLimit;
    @Nullable
    @NotBlank
    @Size(min =  3, max = 120)
    private String title;
}

