package explore.with.me.compilation;

import explore.with.me.compilation.dto.CompilationDto;
import explore.with.me.compilation.dto.NewCompilationDto;
import explore.with.me.compilation.model.Compilation;
import explore.with.me.event.model.Event;
import explore.with.me.event.repository.EventRepository;
import explore.with.me.event.service.EventUserService;
import explore.with.me.exception.NotFoundException;
import explore.with.me.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventUserService eventUserService;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        if (newCompilationDto.getTitle() == null) {
            throw new ValidationException("Не указано поле title");
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        Compilation saveCompilation = compilationRepository.save(compilation);
        log.info("Добавлена новая подборка событий : {}", saveCompilation);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(saveCompilation);
        return compilationDto;
    }

    public List<CompilationDto> readCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = pagination(from, size);
        List<Compilation> compilations = new ArrayList<>();
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest).toList();
        }
        return CompilationMapper.toCompilationDtos(compilations);
    }

    public CompilationDto readCompilationById(Long compId) {
        Compilation compilation = getCompilation(compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilationRepository.delete(compilation);
        log.info("Удалена подборка событий : {}", compilation);
    }

    @Transactional
    public void addEventInCompilation(Long compId, Long eventId) {
        Event event = eventUserService.findEventById(eventId);
        Compilation compilation = getCompilation(compId);
        List<Event> events = compilation.getEvents();
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("В подборку событий {} добавлено мероприятие {}", compilation, event);
    }

    @Transactional
    public void deleteEventInCompilation(Long compId, Long eventId) {
        Event event = eventUserService.findEventById(eventId);
        Compilation compilation = getCompilation(compId);
        List<Event> events = compilation.getEvents();
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Из подборки событий {} удалено мероприятие {}", compilation, event);
    }

    @Transactional
    public void pinedCompilation(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.info("Закреплена подборка событий {} ", compilation);
    }

    @Transactional
    public void unPinedCompilation(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Откреплена подборка событий {} ", compilation);
    }

    public Compilation getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий не найдена"));
        log.info("Найдена подборка событий : {}", compilation);
        return compilation;
    }

    private PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size, Sort.unsorted());
    }
}
