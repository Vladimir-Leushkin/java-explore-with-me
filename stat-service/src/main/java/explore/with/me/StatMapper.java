package explore.with.me;

import explore.with.me.dto.EndpointHit;
import explore.with.me.model.Stat;


public class StatMapper {

    public static Stat toStat(EndpointHit endpointHit) {
        return new Stat(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }
}
