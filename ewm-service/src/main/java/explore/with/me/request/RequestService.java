package explore.with.me.request;

import explore.with.me.State;
import explore.with.me.event.model.Event;
import explore.with.me.event.repository.EventRepository;
import explore.with.me.exception.ForbiddenException;
import explore.with.me.exception.NotFoundException;
import explore.with.me.exception.ValidationException;
import explore.with.me.request.dto.RequestDto;
import explore.with.me.request.model.Request;
import explore.with.me.user.UserService;
import explore.with.me.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getUser(userId);
        if (eventId == null) {
            throw new ValidationException("Неверно указано событие");
        }
        Event event = findEventById(eventId);
        checkUnPublishedEvent(event);
        checkRequestEvent(event, user);
        Integer comfReq = getConfirmedRequest(event);
        checkParticipantLimit(event, comfReq);
        checkReplaceRequest(event, user);
        Request request = new Request(null, LocalDateTime.now(), event, user, RequestState.PENDING);
        if (!event.getRequestModeration()) {
            request.setStatus(RequestState.CONFIRMED);
        }
        Request saveRequest = requestRepository.save(request);
        log.info("Добавлен новый запрос на участие в событии : {}", saveRequest);
        RequestDto requestDto = RequestMapper.toRequestDto(saveRequest);
        return requestDto;
    }

    public List<RequestDto> getRequestsByUser(Long userId) {
        User user = userService.getUser(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Найдены заявки на участие : {}", requests);
        List<RequestDto> requestDtos = requests
                .stream().map(request -> RequestMapper.toRequestDto(request))
                .collect(Collectors.toList());
        return requestDtos;
    }

    @Transactional
    public RequestDto cancelRequest(Long userId, Integer requestId) {
        User user = userService.getUser(userId);
        Request request = findRequest(requestId);
        checkRequestRequester(request, user);
        request.setStatus(RequestState.CANCELED);
        requestRepository.save(request);
        log.info("Отменена заявка на участие : {}", request);
        RequestDto requestDto = RequestMapper.toRequestDto(request);
        return requestDto;
    }

    public Event findEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        log.info("Найдено событие : {}", event);
        return event;
    }

    public Request findRequest(Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие не найден"));
        return request;
    }

    protected void checkRequestRequester(Request request, User user) {
        if (!request.getRequester().equals(user)) {
            throw new ForbiddenException("Запрос на участие создан другим пользователем");
        }
    }

    protected void checkRequestEvent(Event event, User user) {
        if (event.getInitiator().equals(user)) {
            throw new ForbiddenException("Запрос подан инициатором события");
        }
    }

    protected void checkUnPublishedEvent(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Событие не опубликовано");
        }
    }

    protected Integer getConfirmedRequest(Event event) {
        Integer conReq = requestRepository.countByEventIdAndStatus(event.getId(), RequestState.CONFIRMED);
        return conReq;
    }

    protected void checkParticipantLimit(Event event, Integer comfReq) {
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= comfReq) {
            throw new ForbiddenException("Лимит заявок на участие в событии исчерпан");
        }
    }

    public void checkReplaceRequest(Event event, User user) {
        List<Request> requests = requestRepository.findAllByEventId(event.getId());
        for (Request request : requests) {
            if (request.getRequester().equals(user)) {
                throw new ForbiddenException("Заявка на участие в событии уже существует");
            }
        }
    }
}
