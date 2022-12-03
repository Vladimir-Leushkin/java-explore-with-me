package explore.with.me.event.controller;

import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.dto.EventShortDto;
import explore.with.me.event.service.EventPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventPublicService eventPublicService;

    @GetMapping
    public List<EventShortDto> readEventsByUserFilter(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") String sort,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        List<EventShortDto> eventShortDtos = eventPublicService.readEventsByUserFilter(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        return eventShortDtos;
    }

    @GetMapping("/{id}")
    public EventFullDto readEventByUser(@PathVariable Long id,
                                        HttpServletRequest request) {
        EventFullDto eventFullDto = eventPublicService.readEventByUser(id, request);
        return eventFullDto;
    }
}
