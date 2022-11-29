package explore.with.me;

import explore.with.me.dto.EndpointHit;
import explore.with.me.dto.ViewsStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "")
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public void createHit(@RequestBody EndpointHit endpointHit) {
        statService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public Collection<ViewsStats> readHits(
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique
    ) {
        return statService.readHits(start, end, uris, unique);
    }

    @GetMapping("/hit")
    public Integer getViews(@RequestParam String uri) {
        log.info("Получение статистики просмотров по uri={}.", uri);
        return statService.getViews(uri);
    }
}
