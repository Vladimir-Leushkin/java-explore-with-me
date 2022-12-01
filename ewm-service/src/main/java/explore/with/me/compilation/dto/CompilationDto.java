package explore.with.me.compilation.dto;

import explore.with.me.event.dto.EventShortDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDto {
    @NotNull
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private Boolean pinned;
    private List<EventShortDto> events;
}
