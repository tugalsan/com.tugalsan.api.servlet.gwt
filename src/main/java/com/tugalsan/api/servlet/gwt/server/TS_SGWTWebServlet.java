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
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.time.Duration;

@WebServlet("/" + TGC_SGWTService.LOC_PARENT + "/" + TGC_SGWTService.LOC_NAME)//AS IN "/app/g"
public class TS_SGWTWebServlet extends RemoteServiceServlet implements TGS_SGWTServiceInterface {

    final private static TS_Log d = TS_Log.of(TS_SGWTWebServlet.class);
    public static volatile TS_ThreadSyncTrigger killTrigger = null;

//    private static final long serialVersionUID () 20201015L;
    @Override
    public TGS_SGWTFuncBase call(TGS_SGWTFuncBase funcBase) {
        return TGS_UnSafe.call(() -> {
            d.ci("call", "----------------------------------------------------------------");
            d.ci("call", "funcBase", funcBase);
            var request = TGS_UnSafe.call(() -> getThreadLocalRequest(), e -> null);
            if (request == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " cannot fetch request");
            }
            var clientIp = TGS_UnSafe.call(() -> TS_NetworkIPUtils.getIPClient(request), e -> null);
            if (clientIp == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " cannot fetch clientIp");
            }
            var si = TS_SGWTExecutorList.get(funcBase.getSuperClassName());
            if (si == null) {
                return handleError(funcBase, "ERROR:" + funcBase.getSuperClassName() + " cannot find for clientIp " + clientIp + ":\n" + getServletData());
            }
            TGS_CallableType1<Boolean, TS_ThreadSyncTrigger> callable = kt -> {
                var validationResult = si.value1.validate(request, funcBase);
                if (!validationResult.value0) {
                    return false;
                }
                si.value1.run(request, funcBase, validationResult.value1);
                return true;
            };
            var await = TS_ThreadAsyncAwait.callSingle(killTrigger, Duration.ofSeconds(si.value1.timeout_seconds()), callable);
            if (await.timeout()) {
                handleError(funcBase, "ERROR:" + si.value1.getClass().toString() + " cannot run (timeout) for clientIp " + clientIp);
                return funcBase;
            }
            if (await.resultIfSuccessful.isEmpty()) {
                handleError(funcBase, "ERROR:" + si.value1.getClass().toString() + " cannot run (unknown) for clientIp " + clientIp);
                return funcBase;
            }
            if (!await.resultIfSuccessful.get()) {
                handleError(funcBase, "ERROR:" + si.value1.getClass().toString() + " cannot run (validate) for clientIp " + clientIp);
                return funcBase;
            }
            d.ci("call", "executed", funcBase.getSuperClassName());
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
                TS_SGWTExecutorList.SYNC.toList().stream()
                        .map(item -> item.value0 + ":" + item.value1.getClass().getSimpleName())
        );
    }
}
