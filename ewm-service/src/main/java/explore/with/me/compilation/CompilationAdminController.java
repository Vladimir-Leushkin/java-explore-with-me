package explore.with.me.compilation;

import explore.with.me.compilation.dto.CompilationDto;
import explore.with.me.compilation.dto.NewCompilationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = compilationService.createCompilation(newCompilationDto);
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventInCompilation(@PathVariable Long compId,
                                      @PathVariable Long eventId) {
        compilationService.addEventInCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventInCompilation(@PathVariable Long compId,
                                         @PathVariable Long eventId) {
        compilationService.deleteEventInCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinedCompilation(@PathVariable Long compId) {
        compilationService.pinedCompilation(compId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unPinedCompilation(@PathVariable Long compId) {
        compilationService.unPinedCompilation(compId);
    }

}
