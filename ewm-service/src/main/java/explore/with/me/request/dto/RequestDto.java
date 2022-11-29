package explore.with.me.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.with.me.request.RequestState;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestDto {

    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestState status;
}
