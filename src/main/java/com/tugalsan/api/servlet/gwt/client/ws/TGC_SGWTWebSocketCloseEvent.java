package com.tugalsan.api.servlet.gwt.client.ws;

public class TGC_SGWTWebSocketCloseEvent {

    private final short code;
    private final String reason;
    private final boolean wasClean;

    public TGC_SGWTWebSocketCloseEvent(short code, String reason, boolean wasClean) {
        this.code = code;
        this.reason = reason;
        this.wasClean = wasClean;
    }

    public short code() {
        return code;
    }

    public String reason() {
        return reason;
    }

    public boolean wasClean() {
        return wasClean;
    }
}