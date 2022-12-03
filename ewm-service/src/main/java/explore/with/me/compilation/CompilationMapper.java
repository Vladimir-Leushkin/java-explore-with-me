package explore.with.me.compilation;

import explore.with.me.compilation.dto.CompilationDto;
import explore.with.me.compilation.dto.NewCompilationDto;
import explore.with.me.compilation.model.Compilation;
import explore.with.me.event.EventMapper;
import explore.with.me.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;


public class CompilationMapper {


    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(
                null,
                newCompilationDto.getTitle(),
                newCompilationDto.getPinned(),
                events
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getPinned(),
                compilation.getEvents().stream()
                        .map(event -> EventMapper.toEventShortDto(event)).collect(Collectors.toList())
        );
    }

    public static List<CompilationDto> toCompilationDtos(List<Compilation> compilations) {
        List compilationDtos = compilations
                .stream()
                .map(compilation -> toCompilationDto(compilation))
                .collect(Collectors.toList());
        return compilationDtos;
    }

}
