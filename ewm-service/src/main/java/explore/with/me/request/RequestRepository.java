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

    Integer countByEventIdAndStatus(Long evenId, RequestState status);

    @Query(value = "select * " +
            "from REQUESTS " +
            "where requester_id = ?1 " +
            "and event_id = ?2 ", nativeQuery = true)
    Request findRequestByRequesterIdAndEventId(Long userId, Long eventId);
}
