package me.zort.nsm.client.response;

import lombok.Data;

import java.util.List;

@Data
public final class ServiceListResponse {

    private List<String> services;
    private Meta meta;

    @Data
    public static final class Meta {
        private int page;
        private int pageSize;
        private int total;
    }

}
