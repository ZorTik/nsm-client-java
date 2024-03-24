package me.zort.nsm.client.response;

import lombok.Data;

@Data
public final class CreateServiceResponse {

    private String message;
    private String serviceId;
    private String statusPath;
    private long time;

}
