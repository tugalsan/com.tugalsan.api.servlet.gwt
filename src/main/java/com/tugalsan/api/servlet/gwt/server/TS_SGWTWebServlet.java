package com.tugalsan.api.servlet.gwt.server;

import java.util.*;
import javax.servlet.annotation.*;
import com.google.gwt.user.server.rpc.*;
import com.tugalsan.api.function.client.TGS_Func_OutTyped_In1;
import com.tugalsan.api.servlet.gwt.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.network.server.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.thread.server.async.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.time.Duration;

@WebServlet("/" + TGC_SGWTService.LOC_PARENT + "/" + TGC_SGWTService.LOC_NAME)//AS IN "/app/g"
public class TS_SGWTWebServlet extends RemoteServiceServlet implements TGS_SGWTServiceInterface {

    final private static TS_Log d = TS_Log.of(false, TS_SGWTWebServlet.class);
    public static volatile TS_ThreadSyncTrigger killTrigger = null;
    public static volatile TS_SGWTConfig config = TS_SGWTConfig.of();

//    private static final long serialVersionUID () 20201015L;
    @Override
    public TGS_SGWTFuncBase call(TGS_SGWTFuncBase funcBase) {
        return TGS_UnSafe.call(() -> {
            d.ci("call", "----------------------------------------------------------------");
            d.ci("call", "funcBase", funcBase);
            var request = TGS_UnSafe.call(() -> getThreadLocalRequest(), e -> null);
            if (request == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " (" + TGC_SGWTResponse.CANNOT_FETCH_REQUEST + ")");
            }
            var clientIp = TGS_UnSafe.call(() -> TS_NetworkIPUtils.getIPClient(request), e -> null);
            if (clientIp == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " (" + TGC_SGWTResponse.CANNOT_FETCH_CLIENTIP + ")");
            }
            var si = TS_SGWTExecutorList.get(funcBase.getSuperClassName());
            if (si == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " (" + TGC_SGWTResponse.CANNOT_FIND_SERVLET + ") " + funcBase.getSuperClassName() + ", for clientIp " + clientIp + ":\n" + getServletData());
            }
            TGS_Func_OutTyped_In1<Boolean, TS_ThreadSyncTrigger> callable = servletKillTrigger -> {
                return TGS_UnSafe.call(() -> {
                    var validationResult = si.exe().validate(servletKillTrigger, request, funcBase);
                    if (!validationResult.result()) {
                        return false;
                    }
                    si.exe().run(servletKillTrigger, request, funcBase, validationResult.data());
                    return true;
                }, e -> {
                    d.ct("call", e);
                    return false;
                });
            };
            if (config.enableTimeout) {
                var servletKillTrigger = TS_ThreadSyncTrigger.ofParent(killTrigger);
                var await = TS_ThreadAsyncAwait.callSingle(servletKillTrigger, Duration.ofSeconds(si.exe().timeout_seconds()), callable);
                servletKillTrigger.trigger();
                if (await.timeout()) {
                    handleError(funcBase, "ERROR(AWAIT):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_TIMEOUT + ") for clientIp " + clientIp);
                    return funcBase;
                }
                if (await.hasError()) {
                    d.ce("call", si.name(), "ERROR(AWAIT)", si.exe().timeout_seconds(), await.exceptionIfFailed.get().getMessage());
//                    return;
                }
                if (await.resultIfSuccessful.isEmpty()) {
                    handleError(funcBase, "ERROR(AWAIT):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_EMPTY + ") for clientIp " + clientIp);
                    return funcBase;
                }
                if (!await.resultIfSuccessful.get()) {
                    handleError(funcBase, "ERROR(AWAIT):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_FALSE + ") for clientIp " + clientIp);
                    return funcBase;
                }
            } else {
                if (!callable.call(killTrigger)) {
                    handleError(funcBase, "ERROR(SYNC):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_KILLED + ") for clientIp " + clientIp);
                    return funcBase;
                }
            }
            d.ci("call", "executed", "config.enableTimeout", config.enableTimeout, funcBase.getSuperClassName(), "ex:" + funcBase.getExceptionMessage());
            return funcBase;
        }, e -> handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " -> RUNTIME_ERROR: " + e.getMessage()));
    }

    private static TGS_SGWTFuncBase handleError(TGS_SGWTFuncBase funcBase, String errorMessage) {
        d.ce("call", errorMessage, funcBase);
        funcBase.setExceptionMessage(errorMessage);
        return funcBase;
    }

    private static List<String> getServletData() {
        return TGS_StreamUtils.toLst(
                TS_SGWTExecutorList.SYNC.stream()
                        .map(item -> item.name() + ":" + item.exe().getClass().getSimpleName())
        );
    }
}
