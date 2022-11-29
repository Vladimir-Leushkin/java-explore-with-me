package explore.with.me.client;

import explore.with.me.client.dto.EndpointHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StatClient {

    private String appName = "ewm-service";
    @Value("${stat-service.url}")
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

    public Object getViews(String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                baseUri + "/hit?uri=" + uri,
                HttpMethod.GET,
                requestEntity,
                Object.class
        );
    }
}
