package explore.with.me.event.controller;

import explore.with.me.event.dto.AdminUpdateEventDto;
import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.service.EventAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService eventAdminService;

    @GetMapping
    public List<EventFullDto> readEventsByFilter(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<EventFullDto> eventShortDtos = eventAdminService
                .readEventsByFilter(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventShortDtos;
    }

    @PutMapping("/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable Long eventId,
                                         @RequestBody AdminUpdateEventDto adminEventDto) {
        EventFullDto saveEventDto = eventAdminService.adminUpdateEvent(eventId, adminEventDto);
        return saveEventDto;
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        EventFullDto eventFullDto = eventAdminService.publishEvent(eventId);
        return eventFullDto;
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        EventFullDto eventFullDto = eventAdminService.rejectEvent(eventId);
        return eventFullDto;
    }
}
