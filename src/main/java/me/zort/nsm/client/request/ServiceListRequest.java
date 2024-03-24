package me.zort.nsm.client.request;

import lombok.Builder;

@Builder
public class ServiceListRequest {

    private final int page;
    private final int pageSize;
    @Builder.Default
    private final boolean all = false;

    public ServiceListRequestBuilder toBuilder() {
        return ServiceListRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .all(all);
    }

}
