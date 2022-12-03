package explore.with.me.client.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ViewsStats {

    private String app;
    private String uri;
    private Long hits;
}
