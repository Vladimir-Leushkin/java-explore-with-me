package explore.with.me.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.with.me.State;
import explore.with.me.category.dto.CategoryDto;
import explore.with.me.event.location.Location;
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
public class EventFullDto extends EventShortDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    private Location location;
    @PositiveOrZero
    private Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @NonNull
    private Boolean requestModeration;
    private State state;

    public EventFullDto(Long id, @NotBlank @Size(min = 20, max = 2000) String annotation,
                        @NotNull CategoryDto category, @PositiveOrZero Integer confirmedRequests,
                        @NotNull LocalDateTime eventDate, @NotNull UserDtoShort initiator, Boolean paid,
                        @NotBlank @Size(min = 3, max = 120) String title, @PositiveOrZero Integer views,
                        LocalDateTime createdOn, String description, Location location, Integer participantLimit,
                        LocalDateTime publishedOn, @NonNull Boolean requestModeration, State state) {
        super(id, annotation, category, confirmedRequests, eventDate, initiator, paid, title, views);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
    }
}
