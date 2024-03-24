package me.zort.nsm.client.response;

import lombok.Data;

@Data
public final class BasicActionResponse {

    private int status;
    private String message;
    // Nullable
    private String statusPath;

}
