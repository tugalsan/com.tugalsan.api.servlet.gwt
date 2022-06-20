package com.tugalsan.api.servlet.gwt.server;

import com.tugalsan.api.executable.client.*;
import javax.servlet.http.HttpServletRequest;
import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.servlet.gwt.client.TGS_SGWTFuncBase;

abstract public class TS_SGWTExecutor implements TGS_ExecutableType3<HttpServletRequest, TGS_SGWTFuncBase, Object> {

    abstract public String name();

    abstract public TGS_Pack2<Boolean, Object> validate(HttpServletRequest request, TGS_SGWTFuncBase funcBase);

    public static void ifValidExecute(TS_SGWTExecutor executor, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var pack = executor.validate(rq, funcBase);
        if (!pack.value0) {
            return;
        }
        executor.execute(rq, funcBase, pack.value1);
    }
}
