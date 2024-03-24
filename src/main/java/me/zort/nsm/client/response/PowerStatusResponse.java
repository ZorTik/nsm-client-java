package me.zort.nsm.client.response;

import lombok.Data;

@Data
public final class PowerStatusResponse {

    private String id;
    private Status status;
    // Nullable
    private String error;

    public enum Status {
        IDLE, PENDING, ERROR
    }

}
