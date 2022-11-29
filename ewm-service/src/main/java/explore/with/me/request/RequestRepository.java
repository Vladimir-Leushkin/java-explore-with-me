package explore.with.me.request;

import explore.with.me.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventId(Long eventId);
}
