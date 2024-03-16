package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.core.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.url.client.*;

abstract public class TGS_SGWTFuncBase implements IsSerializable {

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public int timeout_seconds() {
        return 60;
    }

    public void setExceptionMessage(CharSequence exceptionMessage) {
        this.exceptionMessage = exceptionMessage.toString();
    }
    private String exceptionMessage = null;

    abstract public String getSuperClassName();

    public TGS_SGWTFuncBase() {
        if (GWT.isClient()) {
            input_url = TGC_UrlCurrentUtils.getUrl().toString();
        }
    }
    private CharSequence input_url = null;

    public CharSequence getInput_url() {
        return input_url;
    }

    public void setInput_url(CharSequence input_url) {
        this.input_url = input_url;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(TGS_SGWTFuncBase.class.getSimpleName());
        sb.append('}');
        return sb.toString();
    }
}
