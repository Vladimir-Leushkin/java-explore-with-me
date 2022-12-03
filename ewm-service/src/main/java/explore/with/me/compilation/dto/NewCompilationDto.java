package explore.with.me.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCompilationDto {

    @NotNull
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
