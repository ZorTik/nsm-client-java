package me.zort.nsm.client.request;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public class CreateServiceRequest {

    private final String template;
    @Builder.Default
    private final Integer ram = null;
    @Builder.Default
    private final Integer cpu = null;
    @Builder.Default
    private final Integer disk = null;
    @Builder.Default
    private final Map<String, String> env = new HashMap<>();

}
