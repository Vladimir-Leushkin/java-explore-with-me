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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ViewsStats> readHits(String rangeStart, String rangeEnd, List<String> uris, Boolean unique) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart == null || rangeEnd == null) {
            rangeStart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            rangeEnd = LocalDateTime.now().plusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        start = LocalDateTime.parse(
                URLDecoder.decode(rangeStart, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        end = LocalDateTime.parse(
                URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Collection<Stat> stats = new ArrayList<>();
        if (unique) {
            stats = statRepository.getUniqueViews(start, end, uris);
        } else {
            stats = statRepository.getNotUniqueViews(start, end, uris);
        }
        List<ViewsStats> viewsStats = new ArrayList<>();
        if (stats.size() != 0) {
            viewsStats = stats
                    .stream()
                    .map(stat -> StatMapper.toViewsStats(stat))
                    .collect(Collectors.toList());
        }
        log.info("Получена статистика по посещениям {}", stats);
        return viewsStats;
    }

    public Integer getViews(String uri) {
        log.info("Получение статистики просмотров по uri={}.", uri);
        return statRepository.getViews(uri);
    }
}
