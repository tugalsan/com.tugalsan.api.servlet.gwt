package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;

public class TGC_SGWTService {

    final public static String LOC_PARENT = "app"; //TS_SGWTWebServlet need it static
    final public static String LOC_NAME = "g";//TS_SGWTWebServlet need it static

    public static TGS_SGWTServiceInterfaceAsync getServiceInstance() {
        var exists = SYNC;
        if (exists != null) {
            return exists;
        }
        exists = (TGS_SGWTServiceInterfaceAsync) GWT.create(TGS_SGWTServiceInterface.class);
        var endpoint = (ServiceDefTarget) exists;
        var moduleRelativeURL = GWT.getModuleBaseURL() + LOC_NAME;
        endpoint.setServiceEntryPoint(moduleRelativeURL);
        SYNC = exists;
        return exists;
    }
    private static volatile TGS_SGWTServiceInterfaceAsync SYNC;
}
