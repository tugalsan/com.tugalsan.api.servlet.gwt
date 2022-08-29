package com.tugalsan.api.servlet.gwt.server;

import java.util.*;
import javax.servlet.annotation.*;
import com.google.gwt.user.server.rpc.*;
import com.tugalsan.api.servlet.gwt.client.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.network.server.*;
import com.tugalsan.api.stream.client.*;

@WebServlet("/" + TGC_SGWTService.LOC_PARENT + "/" + TGC_SGWTService.LOC_NAME)//AS IN "/app/g"
public class TS_SGWTWebServlet extends RemoteServiceServlet implements TGS_SGWTServiceInterface {

    final private static TS_Log d = TS_Log.of(TS_SGWTWebServlet.class);

//    private static final long serialVersionUID () 20201015L;
    @Override
    public TGS_SGWTFuncBase call(TGS_SGWTFuncBase funcBase) {
        var request = getThreadLocalRequest();
        d.ci("call", "----------------------------------------------------------------");
        d.ci("call", "assured", funcBase);
        var si = TS_SGWTExecutorList.get(funcBase.getSuperClassName());
        if (si == null) {
            var exceptionMessage = "HATA/ERROR: GET FUNC UNKNOWN: [" + funcBase.getSuperClassName() + "] of " + getServletData();
            d.ce("call", exceptionMessage, funcBase);
            funcBase.setExceptionMessage(exceptionMessage);
            return funcBase;
        }
        var validationResult = si.value1.validate(request, funcBase);
        if (!validationResult.value0) {
            var clientIp = TS_NetworkIPUtils.getIPClient(request);
            var exceptionMessage = "ERROR:" + si.value1.getClass().toString() + " cannot run (validate) for clientIp " + clientIp;
            d.ce("call", exceptionMessage);
            funcBase.setExceptionMessage(exceptionMessage);
            return funcBase;
        }

        si.value1.execute(request, funcBase, validationResult.value1);
        d.ci("call", "executed", funcBase);
        return funcBase;
    }

    private static List<String> getServletData() {
        return TGS_StreamUtils.toList(
                TS_SGWTExecutorList.SYNC.toList().stream()
                        .map(item -> item.value0 + ":" + item.value1.getClass().getSimpleName())
        );
    }
}
