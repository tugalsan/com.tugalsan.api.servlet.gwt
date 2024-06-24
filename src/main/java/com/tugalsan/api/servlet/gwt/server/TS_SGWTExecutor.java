package com.tugalsan.api.servlet.gwt.server;


import com.tugalsan.api.callable.client.TGS_CallableType3Void;
import javax.servlet.http.HttpServletRequest;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.client.TGS_SGWTFuncBase;

abstract public class TS_SGWTExecutor implements TGS_CallableType3Void<HttpServletRequest, TGS_SGWTFuncBase, Object> {

    abstract public String name();

    public int timeout_seconds() {
        return 60;
    }

    abstract public TGS_Tuple2<Boolean, Object> validate(HttpServletRequest request, TGS_SGWTFuncBase funcBase);

    public static void ifValidExecute(TS_SGWTExecutor executor, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var pack = executor.validate(rq, funcBase);
        if (!pack.value0) {
            return;
        }
        executor.run(rq, funcBase, pack.value1);
    }
}
