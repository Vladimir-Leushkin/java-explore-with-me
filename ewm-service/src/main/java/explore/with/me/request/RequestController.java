package explore.with.me.request;

import explore.with.me.request.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam(name = "eventId", required = false) Long eventId) {
        RequestDto requestDto = requestService.createRequest(userId, eventId);
        return requestDto;
    }

    @GetMapping
    public List<RequestDto> readRequestByUser(@PathVariable Long userId) {
        List<RequestDto> requestDtos = requestService.getRequestsByUser(userId);
        return requestDtos;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto updateRequest(@PathVariable Long userId,
                                    @PathVariable Integer requestId) {
        RequestDto requestDto = requestService.cancelRequest(userId, requestId);
        return requestDto;
    }
}
