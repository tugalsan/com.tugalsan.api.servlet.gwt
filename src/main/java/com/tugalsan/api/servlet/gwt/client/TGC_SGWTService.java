package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;

public class TGC_SGWTService {

    private TGC_SGWTService() {

    }

    final public static String LOC_PARENT = "app"; //TS_SGWTWebServlet need it static
    final public static String LOC_NAME = "g";//TS_SGWTWebServlet need it static

    public static TGS_SGWTServiceInterfaceAsync singleton() {
        return SYNC.orElseSet(() -> {
            var a = (TGS_SGWTServiceInterfaceAsync) GWT.create(TGS_SGWTServiceInterface.class);
            var endpoint = (ServiceDefTarget) a;
            var moduleRelativeURL = GWT.getModuleBaseURL() + LOC_NAME;
            endpoint.setServiceEntryPoint(moduleRelativeURL);
            return a;
        });
    }
    private static volatile StableValue<TGS_SGWTServiceInterfaceAsync> SYNC = StableValue.of();
}
