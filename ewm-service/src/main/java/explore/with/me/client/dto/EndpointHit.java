package explore.with.me.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class EndpointHit {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public EndpointHit(String app, String uri, String ip) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = LocalDateTime.now();
    }
}
