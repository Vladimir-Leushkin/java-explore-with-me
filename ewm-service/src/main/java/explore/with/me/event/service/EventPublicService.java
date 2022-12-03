package explore.with.me.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import explore.with.me.State;
import explore.with.me.client.StatClient;
import explore.with.me.event.EventMapper;
import explore.with.me.event.EventSort;
import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.dto.EventShortDto;
import explore.with.me.event.model.Event;
import explore.with.me.event.model.QEvent;
import explore.with.me.event.repository.EventRepository;
import explore.with.me.exception.ForbiddenException;
import explore.with.me.exception.NotFoundException;
import explore.with.me.exception.ValidationException;
import explore.with.me.request.RequestState;
import explore.with.me.request.model.QRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPublicService {

    private final EventRepository eventRepository;
    private final StatClient statClient;

    public EventFullDto readEventByUser(Long id, HttpServletRequest request) {
        Event event = findEventById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Событие не опубликовано");
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        statClient.saveStat(request);
        log.info("Добавлен просмотр событию : {}", event);
        statClient.setViews(eventFullDto);
        log.info("Найдено событие : {}", eventFullDto);
        return eventFullDto;
    }

    public List<EventShortDto> readEventsByUserFilter(String text, List<Integer> categories, Boolean paid,
                                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                      String sort, Integer from, Integer size, HttpServletRequest request) {
        PageRequest page = pagination(from, size, sort);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        Optional<BooleanExpression> filters = getUserFilters(text, categories, paid, start,
                end, onlyAvailable);
        List<Event> events = filters.map(f -> eventRepository.findAll(f, page)
                        .getContent())
                .orElseGet(() -> eventRepository.findAll(page).getContent())
                .stream().collect(Collectors.toList());
        List<EventShortDto> eventShortDtos = events
                .stream()
                .map(event -> EventMapper.toEventShortDto(event))
                .collect(Collectors.toList());
        if (sort != null) {
            EventSort extractedSort;
            try {
                extractedSort = EventSort.valueOf(sort);
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Некорректный вид сортировки");
            }
            switch (extractedSort) {
                case EVENT_DATE:
                    eventShortDtos = eventShortDtos.stream()
                            .sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case VIEWS:
                    eventShortDtos = eventShortDtos.stream()
                            .sorted(Comparator.comparingLong(EventShortDto::getViews))
                            .collect(Collectors.toList());
                    break;
            }
        }
        statClient.saveStat(request);
        log.info("Добавлен просмотр по запросу : {}", request.getRequestURI());
        statClient.setViewsByList(eventShortDtos);
        log.info("Найдены события : {}", eventShortDtos);
        return eventShortDtos;

    }

    public Event findEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId));
        log.info("Найдено событие : {}", event);
        return event;
    }

    private Optional<BooleanExpression> getUserFilters(String text, List<Integer> categories, Boolean paid,
                                                       LocalDateTime start, LocalDateTime end, Boolean onlyAvailable) {
        List<BooleanExpression> filters = new ArrayList<>();
        QEvent event = QEvent.event;
        if (text != null) {
            filters.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            filters.add(event.category.id.in(categories));
        }
        if (paid != null) {
            filters.add(paid ? event.paid.isTrue() : event.paid.isFalse());
        }
        if (start != null) {
            filters.add(event.eventDate.after(start));
        }
        if (end != null) {
            filters.add(event.eventDate.before(end));
        }
        if (onlyAvailable) {
            QRequest request = QRequest.request;
            BooleanExpression unlimit = event.participantLimit.eq(0);
            BooleanExpression requestModerationFalse = event.requestModeration.isFalse()
                    .and(event.participantLimit.goe(request.count()));
            BooleanExpression requestModerationTrue = event.requestModeration.isTrue()
                    .and(event.participantLimit.goe(request.status.eq(RequestState.CONFIRMED).count()));
            filters.add(unlimit.or(requestModerationFalse).or(requestModerationTrue));
        }
        filters.add(event.state.eq(State.PUBLISHED));
        return filters.stream().reduce(BooleanExpression::and);
    }

    private PageRequest pagination(int from, int size, String sort) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }
}
