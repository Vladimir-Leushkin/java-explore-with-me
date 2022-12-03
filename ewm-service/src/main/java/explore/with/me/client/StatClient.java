package explore.with.me.client;

import explore.with.me.client.dto.EndpointHit;
import explore.with.me.client.dto.ViewsStats;
import explore.with.me.event.dto.EventFullDto;
import explore.with.me.event.dto.EventShortDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class StatClient {

    @Value("http://stats-server:9090")
    private String baseUri;

    public void saveStat(HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit(
                null,
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> requestEntity = new HttpEntity<>(endpointHit, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(
                baseUri + "/hit",
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
    }

    public EventShortDto setViews(EventShortDto eventShortDto) {
        String path = "uris=/events/" + eventShortDto.getId();
        RestTemplate restTemplate = new RestTemplate();
        String path1 = baseUri + "/hit?" + path;
        List<ViewsStats> views = List.of(restTemplate.getForObject(path1, ViewsStats[].class));
        if (views.size() != 0) {
            eventShortDto.setViews(Math.toIntExact(views.get(0).getHits()));
        }
        return eventShortDto;
    }

    public List<EventShortDto> setViewsByList(List<EventShortDto> eventsShortDto) {
        String uri = "uris=/events/";
        String uris = new String();
        for (EventShortDto event : eventsShortDto) {
            uris = uri + event.getId() + "&";
        }
        RestTemplate restTemplate = new RestTemplate();
        String path1 = baseUri + "/hit?" + uris;
        List<ViewsStats> views = List.of(restTemplate.getForObject(path1, ViewsStats[].class));
        for (int i = 0; i < views.size(); i++) {
            String[] uri1 = views.get(i).getUri().split("/");
            Long idEvent = Long.valueOf(uri1[uri1.length - 1]);
            for (EventShortDto event : eventsShortDto) {
                if (idEvent.equals(event.getId())) {
                    event.setViews(Math.toIntExact(views.get(i).getHits()));
                }
            }
        }
        return eventsShortDto;
    }

    public List<EventFullDto> setViewsByListFullDto(List<EventFullDto> eventsFullDto) {
        String uri = "uris=/events/";
        String uris = new String();
        for (EventFullDto event : eventsFullDto) {
            uris = uri + event.getId() + "&";
        }
        RestTemplate restTemplate = new RestTemplate();
        String path1 = baseUri + "/hit?" + uris;
        List<ViewsStats> views = List.of(restTemplate.getForObject(path1, ViewsStats[].class));
        for (int i = 0; i < views.size(); i++) {
            String[] uri1 = views.get(i).getUri().split("/");
            Long idEvent = Long.valueOf(uri1[uri1.length - 1]);
            for (EventFullDto event : eventsFullDto) {
                if (idEvent.equals(event.getId())) {
                    event.setViews(Math.toIntExact(views.get(i).getHits()));
                }
            }
        }
        return eventsFullDto;
    }

}
