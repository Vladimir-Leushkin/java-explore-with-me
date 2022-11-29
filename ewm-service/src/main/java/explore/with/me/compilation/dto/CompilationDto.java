package explore.with.me.compilation.dto;

import explore.with.me.event.dto.EventShortDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationDto {

    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
