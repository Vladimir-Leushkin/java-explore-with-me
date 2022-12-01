package explore.with.me.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import explore.with.me.State;
import explore.with.me.category.CategoryService;
import explore.with.me.category.model.Category;
import explore.with.me.event.EventMapper;
import explore.with.me.event.dto.AdminUpdateEventDto;
import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.model.Event;
import explore.with.me.event.model.QEvent;
import explore.with.me.event.repository.EventRepository;
import explore.with.me.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventAdminService {
    private final EventRepository eventRepository;
    private final EventUserService eventUserService;
    private final CategoryService categoryService;
    private final EventMapper eventMapper;

    public List<EventFullDto> readEventsByFilter(List<Long> user, List<String> states, List<Integer> categories,
                                                 String rangeStart, String rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = pagination(from, size);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<EventFullDto> eventFullDtos;
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        List<State> stateValue = null;
        if (states != null) {
            stateValue = states.stream()
                    .map(s -> State.valueOf(s)).collect(Collectors.toList());
        }
        Optional<BooleanExpression> filters = getAdminFilters(user, stateValue, categories, start, end);
        List<Event> events = filters.map(f -> eventRepository.findAll(f, pageRequest)
                        .getContent())
                .orElseGet(() -> eventRepository.findAll(pageRequest).getContent())
                .stream().collect(Collectors.toList());
        eventFullDtos = events
                .stream()
                .map(event -> eventMapper.toEventFullDto(event))
                .collect(Collectors.toList());
        return eventFullDtos;
    }

    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, AdminUpdateEventDto adminEventDto) {
        Event event = eventUserService.findEventById(eventId);
        if (!adminEventDto.getAnnotation().isEmpty()) {
            event.setAnnotation(adminEventDto.getAnnotation());
        }
        if (adminEventDto.getCategory() != 0) {
            Category category = categoryService.getCategory(adminEventDto.getCategory());
            event.setCategory(category);
        }
        if (!adminEventDto.getDescription().isEmpty()) {
            event.setDescription(adminEventDto.getDescription());
        }
        if (adminEventDto.getEventDate() != null) {
            event.setEventDate(adminEventDto.getEventDate());
        }
        if (adminEventDto.getLocation() != null) {
            event.setLocationLat(adminEventDto.getLocation().getLat());
            event.setLocationLon(adminEventDto.getLocation().getLon());
        }
        if (adminEventDto.getPaid() != null) {
            event.setPaid(adminEventDto.getPaid());
        }
        if (adminEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(adminEventDto.getParticipantLimit());
        }
        if (adminEventDto.getRequestModeration() != null) {
            event.setRequestModeration(adminEventDto.getRequestModeration());
        }
        if (!adminEventDto.getTitle().isEmpty()) {
            event.setTitle(adminEventDto.getTitle());
        }
        Event saveEvent = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(saveEvent);
        return eventFullDto;
    }

    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        Event event = eventUserService.findEventById(eventId);
        LocalDateTime now = LocalDateTime.now();
        checkEventTime(event, now);
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(now);
            eventRepository.save(event);
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        return eventFullDto;
    }

    @Transactional
    public EventFullDto rejectEvent(Long eventId) {
        Event event = eventUserService.findEventById(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Событие уже опубликовано");
        }
        event.setState(State.CANCELED);
        Event saveEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(saveEvent);
    }

    protected void checkEventTime(Event event, LocalDateTime now) {
        if (event.getEventDate().isBefore(now.plusHours(1))) {
            throw new ForbiddenException("До события осталось слишком мало времени");
        }
    }

    private Optional<BooleanExpression> getAdminFilters(List<Long> user, List<State> states,
                                                        List<Integer> categories, LocalDateTime start,
                                                        LocalDateTime end) {
        List<BooleanExpression> filter = new ArrayList<>();
        QEvent event = QEvent.event;

        if (user != null) {
            filter.add(event.initiator.id.in(user));
        }
        if (states != null) {
            filter.add(event.state.in(states));
        }
        if (categories != null) {
            filter.add(event.category.id.in(categories));
        }
        if (start != null) {
            filter.add(event.eventDate.after(start));
        }
        if (end != null) {
            filter.add(event.eventDate.before(end));
        }
        return filter.stream()
                .reduce(BooleanExpression::and);
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }

}
