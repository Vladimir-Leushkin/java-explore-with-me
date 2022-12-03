package explore.with.me.request;

import explore.with.me.request.dto.RequestDto;
import explore.with.me.request.model.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreatedOn(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

}
