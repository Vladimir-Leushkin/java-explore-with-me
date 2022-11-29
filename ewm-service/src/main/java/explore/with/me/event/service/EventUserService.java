package explore.with.me.event.service;

import explore.with.me.State;
import explore.with.me.category.CategoryService;
import explore.with.me.category.model.Category;
import explore.with.me.event.EventMapper;
import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.dto.EventShortDto;
import explore.with.me.event.dto.EventUpdateDto;
import explore.with.me.event.dto.NewEventDto;
import explore.with.me.event.model.Event;
import explore.with.me.event.repository.EventRepository;
import explore.with.me.exception.NotFoundException;
import explore.with.me.exception.ValidationException;
import explore.with.me.request.RequestMapper;
import explore.with.me.request.RequestRepository;
import explore.with.me.request.RequestService;
import explore.with.me.request.RequestState;
import explore.with.me.request.dto.RequestDto;
import explore.with.me.request.model.Request;
import explore.with.me.user.UserService;
import explore.with.me.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventUserService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final RequestService requestService;
    private final EventMapper eventMapper;


    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkActualTime(newEventDto.getEventDate());
        if (newEventDto.getAnnotation() == null || newEventDto.getDescription() == null
                || newEventDto.getEventDate() == null || newEventDto.getTitle() == null) {
            throw new ValidationException("Неверно указаны параметры события");
        }
        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(newEventDto.getCategory());
        Event event = eventMapper.toEvent(newEventDto, user, category);
        Event saveEvent = eventRepository.save(event);
        log.info("Добавлено новое событие : {}", saveEvent);
        return eventMapper.toEventFullDto(saveEvent);
    }

    public List<EventShortDto> readEventsByUser(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = pagination(from, size);
        User user = userService.getUser(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest).toList();
        log.info("Найдены события : {}", events);
        List<EventShortDto> eventDtos = new ArrayList<>();
        if (events.size() != 0) {
            eventDtos = events.stream()
                    .map(event -> eventMapper.toEventShortDto(event))
                    .collect(Collectors.toList());
        }
        return eventDtos;
    }

    public EventFullDto readEventByUser(Long eventId, Long userId) {
        User user = userService.getUser(userId);
        Event event = findEventById(eventId);
        checkInitiatorEvent(event, user);
        log.info("Найдено событие : {}", event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        return eventFullDto;
    }

    public List<RequestDto> readRequestsByEvent(Long eventId, Long userId) {
        User user = userService.getUser(userId);
        Event event = findEventById(eventId);
        checkInitiatorEvent(event, user);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Найдены заявки : {} на событие : {}", requests, event);
        List<RequestDto> requestDtos = requests.stream()
                .map(request -> RequestMapper.toRequestDto(request))
                .collect(Collectors.toList());
        return requestDtos;
    }

    @Transactional
    public EventFullDto updateEventByUser(Long userId, EventUpdateDto eventUpdateDto) {
        User user = userService.getUser(userId);
        Event event = findEventById(eventUpdateDto.getEventId());
        Category category = categoryService.getCategory(eventUpdateDto.getCategory());
        checkInitiatorEvent(event, user);
        checkActualTime(event.getEventDate());
        checkPublishedEvent(event);
        Event newEvent = EventMapper.toEventUpdate(eventUpdateDto, user, category);
        newEvent.setRequestModeration(event.getRequestModeration());
        newEvent.setLocationLat(event.getLocationLat());
        newEvent.setLocationLon(event.getLocationLon());
        Event saveEvent = eventRepository.save(newEvent);
        log.info("Добавлено отредактированное событие : {}", saveEvent);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(saveEvent);
        return eventFullDto;
    }

    @Transactional
    public EventFullDto canceledEventByUser(Long eventId, Long userId) {
        User user = userService.getUser(userId);
        Event event = findEventById(eventId);
        checkInitiatorEvent(event, user);
        event.setState(State.CANCELED);
        Event saveEvent = eventRepository.save(event);
        log.info("Отменено событие : {}", saveEvent);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(saveEvent);
        return eventFullDto;
    }

    @Transactional
    public RequestDto confirmRequest(Long eventId, Long userId, Integer reqId) {
        User user = userService.getUser(userId);
        Event event = findEventById(eventId);
        Request request = requestService.findRequest(reqId);
        checkInitiatorEvent(event, user);
        Event saveEvent = new Event();
        if (event.getParticipantLimit() > event.getConfirmedRequests()) {
            request.setStatus(RequestState.CONFIRMED);
            requestRepository.save(request);
            log.info("Одобрена заявка : {}", request);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            saveEvent = eventRepository.save(event);
            log.info("Лимит участников : {}", event.getParticipantLimit() - event.getConfirmedRequests());
        }
        if (saveEvent.getParticipantLimit() == saveEvent.getConfirmedRequests()) {
            List<Request> requests = requestRepository.findAllByEventId(eventId);
            for (Request oldRequest : requests) {
                if (oldRequest.getStatus().equals(RequestState.PENDING)) {
                    oldRequest.setStatus(RequestState.CANCELED);
                    log.info("Отклонена заявка : {}", oldRequest);
                }
            }
            requestRepository.saveAll(requests);
        }
        RequestDto requestDto = RequestMapper.toRequestDto(request);
        return requestDto;
    }

    @Transactional
    public RequestDto rejectRequest(Long eventId, Long userId, Integer reqId) {
        User user = userService.getUser(userId);
        Event event = findEventById(eventId);
        Request request = requestService.findRequest(reqId);
        checkInitiatorEvent(event, user);
        request.setStatus(RequestState.REJECTED);
        requestRepository.save(request);
        log.info("Отклонена заявка : {}", request);
        RequestDto requestDto = RequestMapper.toRequestDto(request);
        return requestDto;
    }

    public Event findEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено событие с id = " + eventId));
        log.info("Найдено событие : {}", event);
        return event;
    }

    protected void checkActualTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("До события осталось слишком мало времени");
        }
    }

    protected void checkInitiatorEvent(Event event, User user) {
        if (event.getInitiator() != user) {
            throw new ValidationException("Событие создано другим пользователем");
        }
    }

    protected void checkPublishedEvent(Event event) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Опубликованное событие редактировать нельзя");
        }
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }

}
