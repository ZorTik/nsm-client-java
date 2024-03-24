package me.zort.nsm.client.response;

import lombok.Data;

import java.util.List;

@Data
public final class NodeStatusResponse {

    private String nodeId;
    private List<String> running;
    private int all;
    private System system;

    @Data
    public static final class System {
        private int totalmem;
        private int freemem;
        private int totaldisk;
        private int freedisk;
    }

}
