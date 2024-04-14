package com.tugalsan.api.servlet.gwt.server;

import com.tugalsan.api.runnable.client.*;
import javax.servlet.http.HttpServletRequest;
import com.tugalsan.api.servlet.gwt.client.TGS_SGWTFuncBase;

abstract public class TS_SGWTExecutor implements TGS_RunnableType3<HttpServletRequest, TGS_SGWTFuncBase, Object> {

    abstract public String name();

    public int timeout_seconds() {
        return 60;
    }

    abstract public TS_SGWTExecutorValidation validate(HttpServletRequest request, TGS_SGWTFuncBase funcBase);

    public static void ifValidExecute(TS_SGWTExecutor executor, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var pack = executor.validate(rq, funcBase);
        if (!pack.validated()) {
            return;
        }
        executor.run(rq, funcBase, pack.itermediateObject());
    }
}
