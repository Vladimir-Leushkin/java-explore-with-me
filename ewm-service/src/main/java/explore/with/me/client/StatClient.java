package explore.with.me.client;

import explore.with.me.client.dto.EndpointHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StatClient {

    @Value("http://stats-server:9090")
    private String baseUri;

    public void saveStat(EndpointHit endpointHit) {
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

    public ResponseEntity<Integer> getViews(String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                baseUri + "/hit?uri=" + uri,
                HttpMethod.GET,
                requestEntity,
                Integer.class
        );
    }
}
