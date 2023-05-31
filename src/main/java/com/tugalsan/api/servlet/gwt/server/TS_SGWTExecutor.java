package com.tugalsan.api.servlet.gwt.server;

import com.tugalsan.api.runnable.client.*;
import javax.servlet.http.HttpServletRequest;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.client.TGS_SGWTFuncBase;

abstract public class TS_SGWTExecutor implements TGS_RunnableType3<HttpServletRequest, TGS_SGWTFuncBase, Object> {

    abstract public String name();

    abstract public TGS_Tuple2<Boolean, Object> validate(HttpServletRequest request, TGS_SGWTFuncBase funcBase);

    public static void ifValidExecute(TS_SGWTExecutor executor, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var pack = executor.validate(rq, funcBase);
        if (!pack.value0) {
            return;
        }
        executor.run(rq, funcBase, pack.value1);
    }
}
