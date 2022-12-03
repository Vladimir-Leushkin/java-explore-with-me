package explore.with.me.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ViewsStats {

    private String app;
    private String uri;
    private Long hits;
}
