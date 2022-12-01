package explore.with.me.request;

import explore.with.me.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    @Query(value = "select " +
            "count(r.id) " +
            "from REQUESTS r " +
            "where r.event_id = ?1 " +
            "and r.status like ?2", nativeQuery = true)
    Integer findConfirmedRequest(Long eventId, RequestState requestState);
}
