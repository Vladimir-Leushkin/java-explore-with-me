package explore.with.me;

import explore.with.me.model.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query(value = "select h.id, " +
            "h.app, " +
            "distinct(h.uri), " +
            "h.ip, " +
            "h.created_on " +
            "from HITS h " +
            "where h.created_on between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri ", nativeQuery = true)
    Collection<Stat> getUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select * " +
            "from HITS h " +
            "where h.created_on between ?1 and ?2 " +
            "and h.uri in (?3) " +
            "group by h.app, h.uri ", nativeQuery = true)
    Collection<Stat> getNotUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select " +
            "count(h.id) " +
            "from HITS h " +
            "where h.uri = ?1", nativeQuery = true)
    Integer getViews(String uri);

}
