package com.tugalsan.api.servlet.gwt.server;

import java.util.*;
import javax.servlet.annotation.*;
import com.google.gwt.user.server.rpc.*;
import com.tugalsan.api.callable.client.TGS_CallableType1;
import com.tugalsan.api.servlet.gwt.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.network.server.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.thread.server.async.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import java.time.Duration;

@WebServlet("/" + TGC_SGWTService.LOC_PARENT + "/" + TGC_SGWTService.LOC_NAME)//AS IN "/app/g"
public class TS_SGWTWebServlet extends RemoteServiceServlet implements TGS_SGWTServiceInterface {

    final private static TS_Log d = TS_Log.of(TS_SGWTWebServlet.class);
    public static volatile TS_ThreadSyncTrigger killTrigger = null;
    public static volatile TS_SGWTConfig config = TS_SGWTConfig.of();

//    private static final long serialVersionUID () 20201015L;
    @Override
    public TGS_SGWTFuncBase call(TGS_SGWTFuncBase funcBase) {
        d.ci("call", "----------------------------------------------------------------");
        d.ci("call", "funcBase", funcBase);
        var request = getThreadLocalRequest();
        if (request == null) {
            return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " cannot fetch request");
        }
        var u_clientIp = TS_NetworkIPUtils.getIPClient(request);
        if (u_clientIp.isExcuse()) {
            return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " cannot fetch clientIp: " + u_clientIp.excuse().getMessage());
        }
        var clientIp = u_clientIp.value();
        var si = TS_SGWTExecutorList.get(funcBase.getSuperClassName());
        if (si == null) {
            return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " cannot find for clientIp " + clientIp + ":\n" + getServletData());
        }
        TGS_CallableType1<TGS_UnionExcuseVoid, TS_ThreadSyncTrigger> callable = kt -> {
            var validationResult = si.executor().validate(request, funcBase);
            if (!validationResult.validated()) {
                return TGS_UnionExcuseVoid.ofExcuse(d.className, "callable", "!validationResult.validated()");
            }
            si.executor().run(request, funcBase, validationResult.itermediateObject());
            return TGS_UnionExcuseVoid.ofVoid();
        };
        if (config.enableTimeout) {
            var await = TS_ThreadAsyncAwait.callSingle(killTrigger, Duration.ofSeconds(si.executor().timeout_seconds()), callable);
            if (await.timeout()) {
                handleError(funcBase, "ERROR(AWAIT):" + si.executor().getClass().toString() + " cannot run (timeout) for clientIp " + clientIp);
                return funcBase;
            }
            if (await.resultIfSuccessful.isEmpty()) {
                handleError(funcBase, "ERROR(AWAIT):" + si.executor().getClass().toString() + " cannot run (unknown) for clientIp " + clientIp);
                return funcBase;
            }
            var callableResult = await.resultIfSuccessful.get();
            if (callableResult.isExcuse()) {
                handleError(funcBase, "ERROR(AWAIT):" + si.executor().getClass().toString() + " cannot run (validate) for clientIp " + clientIp + ": " + callableResult.excuse().getMessage());
                return funcBase;
            }
        } else {
            var callableResult = callable.call(killTrigger);
            if (callableResult.isExcuse()) {
                handleError(funcBase, "ERROR(SYNC):" + si.executor().getClass().toString() + " cannot run (validate) for clientIp " + clientIp + ": " + callableResult.excuse().getMessage());
                return funcBase;
            }
        }
        d.ci("call", "executed", funcBase.getSuperClassName());
        return funcBase;
    }

    private static TGS_SGWTFuncBase handleError(TGS_SGWTFuncBase funcBase, String errorMessage) {
        d.ce("call", errorMessage, funcBase);
        funcBase.setExceptionMessage(errorMessage);
        return funcBase;
    }

    private static List<String> getServletData() {
        return TGS_StreamUtils.toLst(
                TS_SGWTExecutorList.SYNC.toList().stream()
                        .map(item -> item.name() + ":" + item.executor().getClass().getSimpleName())
        );
    }
}
