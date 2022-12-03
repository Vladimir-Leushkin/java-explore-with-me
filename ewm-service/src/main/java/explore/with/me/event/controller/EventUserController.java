package explore.with.me.event.controller;

import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.dto.EventShortDto;
import explore.with.me.event.dto.EventUpdateDto;
import explore.with.me.event.dto.NewEventDto;
import explore.with.me.event.service.EventUserService;
import explore.with.me.request.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventUserController {

    private final EventUserService eventUserService;

    @PostMapping
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody NewEventDto newEventDto) {
        EventFullDto saveEventDto = eventUserService.createEvent(userId, newEventDto);
        return saveEventDto;
    }

    @GetMapping
    public List<EventShortDto> readEventsByUser(
            @PathVariable Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<EventShortDto> eventShortDtos = eventUserService.readEventsByUser(userId, from, size);
        return eventShortDtos;
    }

    @GetMapping("/{eventId}")
    public EventFullDto readEventById(@PathVariable Long eventId,
                                      @PathVariable Long userId) {
        EventFullDto eventFullDto = eventUserService.readEventByUser(eventId, userId);
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> readRequestsByEvent(@PathVariable Long eventId,
                                                @PathVariable Long userId) {
        List<RequestDto> requestDtos = eventUserService.readRequestsByEvent(eventId, userId);
        return requestDtos;
    }

    @PatchMapping
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody EventUpdateDto eventUpdateDto) {
        EventFullDto eventFullDto = eventUserService.updateEventByUser(userId, eventUpdateDto);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto rejectEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId) {
        EventFullDto eventFullDto = eventUserService.canceledEventByUser(eventId, userId);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @PathVariable Integer reqId) {
        RequestDto requestDto = eventUserService.confirmRequest(eventId, userId, reqId);
        return requestDto;
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Integer reqId) {
        RequestDto requestDto = eventUserService.rejectRequest(eventId, userId, reqId);
        return requestDto;
    }

}
