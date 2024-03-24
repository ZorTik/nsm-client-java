package me.zort.nsm.client.request;

import lombok.Builder;

import java.util.Map;

@Builder
public final class ChServiceOptionsRequest {

    @Builder.Default
    private final Long ram = null;
    @Builder.Default
    private final Long cpu = null;
    @Builder.Default
    private final Long disk = null;
    @Builder.Default
    private final Map<String, String> env = null;

}
