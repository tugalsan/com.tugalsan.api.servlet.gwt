package com.tugalsan.api.servlet.gwt.server;

import com.tugalsan.api.function.client.TGS_Func_In4;
import javax.servlet.http.HttpServletRequest;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.client.TGS_SGWTFuncBase;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;

abstract public class TS_SGWTExecutor implements TGS_Func_In4<TS_ThreadSyncTrigger, HttpServletRequest, TGS_SGWTFuncBase, Object> {

    abstract public String name();

    public int timeout_seconds() {
        return 60;
    }

    abstract public TGS_Tuple2<Boolean, Object> validate(TS_ThreadSyncTrigger servletKillTrigger, HttpServletRequest request, TGS_SGWTFuncBase funcBase);

    public static void ifValidExecute(TS_ThreadSyncTrigger servletKillTrigger, TS_SGWTExecutor executor, HttpServletRequest rq, TGS_SGWTFuncBase funcBase) {
        var pack = executor.validate(servletKillTrigger, rq, funcBase);
        if (!pack.value0) {
            return;
        }
        executor.run(servletKillTrigger, rq, funcBase, pack.value1);
    }
}
