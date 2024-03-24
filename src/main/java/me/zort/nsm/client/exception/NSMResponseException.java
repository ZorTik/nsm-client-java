package me.zort.nsm.client.exception;

import lombok.Getter;

@Getter
public class NSMResponseException extends Exception {

    private final int status;
    private final String message;

    public NSMResponseException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

}
