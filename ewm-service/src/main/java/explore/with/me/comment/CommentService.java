package explore.with.me.comment;


import explore.with.me.comment.dto.CommentDto;
import explore.with.me.comment.model.Comment;
import explore.with.me.event.model.Event;
import explore.with.me.event.repository.EventRepository;
import explore.with.me.exception.NotFoundException;
import explore.with.me.exception.ValidationException;
import explore.with.me.request.RequestRepository;
import explore.with.me.request.RequestState;
import explore.with.me.request.model.Request;
import explore.with.me.user.UserRepository;
import explore.with.me.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public CommentDto createComment(Long userId, Long eventId, String text) {
        User user = findUser(userId);
        Event event = findEvent(eventId);
        Comment oldComment = commentRepository.findAllByCommentatorIdAndEventId(userId, eventId);
        if (oldComment != null) {
            log.info("Комментарий уже создан");
            throw new ValidationException("Комментарий уже создан");
        }
        Request request = findRequest(userId, eventId);
        if (request == null || !request.getStatus().equals(RequestState.CONFIRMED)) {
            log.info("Пользователь не зарегистрирован на событие");
            throw new NotFoundException("Пользователь не зарегистрирован на событие");
        }
        checkText(text);
        Comment comment = new Comment(null, text, event, user, LocalDateTime.now(), null);
        Comment saveComment = commentRepository.save(comment);
        log.info("Добавлен новый комментарий : {}", saveComment.getText());
        return CommentMapper.toCommentDto(comment);
    }

    public List<CommentDto> readAllCommentsByUser(Long userId, Integer from, Integer size) {
        findUser(userId);
        PageRequest pageRequest = pagination(from, size);
        List<Comment> comments = commentRepository.findAllByCommentatorId(userId, pageRequest);
        List<CommentDto> commentsDto;
        commentsDto = comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        log.info("Найдены комментарии : {}", commentsDto);
        return commentsDto;
    }

    public List<CommentDto> readAllCommentsByEvent(Long eventId, Integer from, Integer size) {
        findEvent(eventId);
        PageRequest pageRequest = pagination(from, size);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageRequest);
        List<CommentDto> commentsDto;
        commentsDto = comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        log.info("Найдены комментарии : {}", commentsDto);
        return commentsDto;
    }

    public CommentDto readCommentById(Long commId) {
        Comment comment = findCommentById(commId);
        log.info("Найден комментарий : {}", comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    public CommentDto updateComment(Long commId, Long userId, String text) {
        User user = findUser(userId);
        Comment comment = findCommentById(commId);
        if (!comment.getCommentator().equals(user)) {
            log.info("Комментарий не принадлежит пользователю");
            throw new ValidationException("Комментарий не принадлежит пользователю");
        }
        checkText(text);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String oldText = comment.getText();
        StringBuilder newText = new StringBuilder();
        newText.append(oldText);
        newText.append(System.lineSeparator());
        newText.append(LocalDateTime.now().format(formatter));
        newText.append(System.lineSeparator());
        newText.append(text);
        comment.setText(String.valueOf(newText));
        comment.setEditDate(LocalDateTime.now());
        Comment saveComment = commentRepository.save(comment);
        log.info("Обновлен комментарий : {}", saveComment);
        return CommentMapper.toCommentDto(saveComment);
    }

    @Transactional
    public void deleteComment(Long commId) {
        Comment comment = findCommentById(commId);
        commentRepository.deleteById(commId);
        log.info("Удален комментарий : {}", comment);
    }

    protected User findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Найден пользователь : {}", user);
        return user;
    }

    protected Event findEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        log.info("Найдено событие : {}", event);
        return event;
    }

    protected Request findRequest(Long userId, Long eventId) {
        Request request;
        request = requestRepository.findRequestByRequesterIdAndEventId(userId, eventId);
        log.info("Найден запрос на участие в событии : {}", request);
        return request;
    }

    protected Comment findCommentById(Long commId) {
        Comment comment = commentRepository.findById(commId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        log.info("Найден комментарий : {}", comment);
        return comment;
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }

    protected void checkText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            log.info("Текст комментария не может быть пустым");
            throw new ValidationException("Текст комментария не может быть пустым");
        }
    }
}
