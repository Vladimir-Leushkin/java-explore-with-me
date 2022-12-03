package explore.with.me.compilation;

import explore.with.me.compilation.dto.CompilationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> readCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<CompilationDto> compilationDtos = compilationService.readCompilations(pinned, from, size);
        return compilationDtos;
    }

    @GetMapping("/{compId}")
    public CompilationDto readCompilationById(@PathVariable Long compId) {
        CompilationDto compilationDto = compilationService.readCompilationById(compId);
        return compilationDto;
    }
}
