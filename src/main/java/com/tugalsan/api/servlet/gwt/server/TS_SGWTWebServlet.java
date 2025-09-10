package com.tugalsan.api.servlet.gwt.server;

import java.util.*;
import javax.servlet.annotation.*;
import com.google.gwt.user.server.rpc.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_OutTyped_In1;
import com.tugalsan.api.servlet.gwt.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.network.server.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import java.time.Duration;

@WebServlet("/" + TGC_SGWTService.LOC_PARENT + "/" + TGC_SGWTService.LOC_NAME)//AS IN "/app/g"
public class TS_SGWTWebServlet extends RemoteServiceServlet implements TGS_SGWTServiceInterface {

    final private static TS_Log d = TS_Log.of(false, TS_SGWTWebServlet.class);
    public static volatile TS_SGWTConfig config = TS_SGWTConfig.of();

    public static void warmUp(TS_ThreadSyncTrigger killTrigger) {
        TS_SGWTWebServlet.killTrigger = killTrigger;
    }
    private static volatile TS_ThreadSyncTrigger killTrigger = null;
//    private static final long serialVersionUID () 20201015L;

    @Override
    public TGS_SGWTFuncBase call(TGS_SGWTFuncBase funcBase) {
        return TGS_FuncMTCUtils.call(() -> {
            d.ci("call", "----------------------------------------------------------------");
            d.ci("call", "funcBase", funcBase);
            var request = TGS_FuncMTCUtils.call(() -> getThreadLocalRequest(), e -> null);
            if (request == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " (" + TGC_SGWTResponse.CANNOT_FETCH_REQUEST + ")");
            }
            var clientIp = TGS_FuncMTCUtils.call(() -> TS_NetworkIPUtils.getIPClient(request), e -> null);
            if (clientIp == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " (" + TGC_SGWTResponse.CANNOT_FETCH_CLIENTIP + ")");
            }
            var si = TS_SGWTExecutorList.get(funcBase.getSuperClassName());
            if (si == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " (" + TGC_SGWTResponse.CANNOT_FIND_SERVLET + ") " + funcBase.getSuperClassName() + ", for clientIp " + clientIp + ":\n" + getServletData());
            }
            TGS_FuncMTU_OutTyped_In1<Boolean, TS_ThreadSyncTrigger> callable = servletKillTrigger -> {
                return TGS_FuncMTCUtils.call(() -> {
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
            var servletKillTrigger_wt = TS_ThreadSyncTrigger.of(funcBase.getSuperClassName(), killTrigger).newChild(d.className());
            if (config.enableTimeout) {
                var servletKillTrigger_await_wt = servletKillTrigger_wt.newChild("await");
                var await = TS_ThreadAsyncAwait.callSingle(servletKillTrigger_await_wt, Duration.ofSeconds(si.exe().timeout_seconds()), callable);
                servletKillTrigger_await_wt.trigger("sgwt_post_await");
                if (await.timeout()) {
                    handleError(funcBase, "ERROR(AWAIT):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_TIMEOUT + ") for clientIp " + clientIp);
                    return funcBase;
                }
                if (await.hasError()) {
                    d.ce("call", "ERROR(AWAIT)", si.exe().getClass().toString(), si.exe().timeout_seconds(), await.exceptionIfFailed().get().getMessage());
                    d.ct("call", await.exceptionIfFailed().get());
//                    return;
                }
                if (await.result().isEmpty()) {
                    handleError(funcBase, "ERROR(AWAIT):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_EMPTY + ") for clientIp " + clientIp);
                    return funcBase;
                }
                if (!await.result().get()) {
                    handleError(funcBase, "ERROR(AWAIT):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_FALSE + ") for clientIp " + clientIp);
                    return funcBase;
                }
            } else {
                var servletKillTrigger_run_wt = servletKillTrigger_wt.newChild("run");
                if (!callable.call(servletKillTrigger_run_wt)) {
                    handleError(funcBase, "ERROR(SYNC):" + si.exe().getClass().toString() + " (" + TGC_SGWTResponse.VALIDATE_RESULT_KILLED + ") for clientIp " + clientIp);
                    servletKillTrigger_run_wt.trigger("surl_post_run_failed");
                    return funcBase;
                }
                servletKillTrigger_run_wt.trigger("surl_post_run_ok");
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
