package explore.with.me;

import explore.with.me.dto.ViewsStats;
import explore.with.me.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("select new explore.with.me.dto.ViewsStats(h.app, h.uri, count(distinct h.ip)) " +
            "from Stat h " +
            "where h.timestamp between ?1 and ?2 " +
            "and h.uri in (?3) " +
            " group by h.app, h.uri")
    Collection<ViewsStats> getUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new explore.with.me.dto.ViewsStats(h.app, h.uri, count(h.ip)) " +
            "from Stat h " +
            "where h.timestamp between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri")
    Collection<ViewsStats> getNotUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select " +
            "count(h.id) " +
            "from HITS h " +
            "where h.uri = ?1", nativeQuery = true)
    Integer getViews(String uri);
}
