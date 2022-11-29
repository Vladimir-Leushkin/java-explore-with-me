package explore.with.me.compilation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewCompilationDto {

    private String title;
    private Boolean pinned;
    private List<Long> events;
}
