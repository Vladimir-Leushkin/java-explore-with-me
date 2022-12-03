package explore.with.me;


import explore.with.me.dto.EndpointHit;
import explore.with.me.dto.ViewsStats;
import explore.with.me.model.Stat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatService {

    private final StatRepository statRepository;

    @Transactional
    public void createHit(EndpointHit endpointHit) {
        Stat stat = StatMapper.toStat(endpointHit);
        log.info("Добавлена статистика по посещениям {}", stat);
        statRepository.save(stat);
    }

    public Collection<ViewsStats> readHits(String rangeStart, String rangeEnd, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(
                URLDecoder.decode(rangeStart, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(
                URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Collection<ViewsStats> stats;
        if (unique) {
            stats = statRepository.getUniqueViews(start, end, uris);
        } else {
            stats = statRepository.getNotUniqueViews(start, end, uris);
        }
        log.info("Получена статистика по посещениям {}", stats);
        return stats;
    }

    public List<ViewsStats> getViews(List<String> uris) {
        log.info("Получение статистики просмотров по uri={}.", uris);
        return statRepository.getViews(uris);
    }
}
