package me.zort.nsm.client.response;

import lombok.Data;

import java.util.Map;

@Data
public final class InspectServiceResponse {

    private String id;
    private int port;
    private Map<String, Object> options;
    private Map<String, String> env;
    private Session session;

    @Data
    public static final class Session {
        private String serviceId;
        private String nodeId;
        private String containerId;
    }

}
