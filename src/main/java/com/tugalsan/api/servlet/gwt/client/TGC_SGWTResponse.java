package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.runnable.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TGC_SGWTResponse<T extends TGS_SGWTFuncBase> implements AsyncCallback, IsSerializable {

    final private static TGC_Log d = TGC_Log.of(TGC_SGWTResponse.class);

    private static String PREFIX_SERVER_DOWN_MESSAGE() {
        return "0  ";
    }

    private static String PREFIX_SERVER_INTERNAL_MESSAGE() {
        return "500  ";
    }

    public TGC_SGWTResponse() {
    }

    public TGC_SGWTResponse(TGS_RunnableType1<T> runnable, TGS_RunnableType1<Throwable> onFail, TGS_Runnable closure) {
        this.runnable = runnable;
        this.onFail = onFail;
        this.closure = closure;
    }
    private TGS_RunnableType1<T> runnable;
    private TGS_RunnableType1<Throwable> onFail;
    private TGS_Runnable closure;

    @Override
    final public void onFailure(Throwable caught) {
        d.ci("onFailure", "#0", caught);
        if (onFail == null) {
            d.ci("onFailure", "onFail == null");
            if (caught == null) {
                d.ci("onFailure", "onFail == null", "#1");
                d.ce("onFailure", "caught == null");
            } else if (caught.getMessage() == null) {
                d.ci("onFailure", "onFail == null", "#2");
                d.ce("onFailure", "caught.getMessage() == null");
            } else if (caught.getMessage().startsWith(PREFIX_SERVER_DOWN_MESSAGE())) {
                d.ci("onFailure", "onFail == null", "#3");
                d.ce("onFailure", "HATA: Bağlantı koptu; güncelleniyor olabilir; network bağlantınızı kontrol edip, bekleyiniz...");
            } else if (caught.getMessage().startsWith(PREFIX_SERVER_INTERNAL_MESSAGE())) {
                d.ci("onFailure", "onFail == null", "#4");
                d.ce("onFailure", "HATA: Server makinesinde hata oluştu! Ayrıntılar için server makinesinin hata kayıtlarına bakınız. (Hiç işlem yapamıyorsanız, kullanıcı girişinizi kontrol edebilirsiniz.)");
            } else {
                d.ci("onFailure", "onFail == null", "#5");
                d.ce("onFailure", "HATA: " + caught.getMessage());
            }
        } else {
            d.ci("onFailure", "onFail != null", "#6");
            onFail.run(caught);
            d.ci("onFailure", "onFail != null", "#7");
        }
        d.ci("onFailure", "closure", "#8");
        if (closure != null) {
            closure.run();
        }
        d.ci("onFailure", "closure", "#9");
    }

    @Override
    final public void onSuccess(Object response) {
        d.ci("onSuccess", "#1", response);
        if (response == null) {
            d.ci("onSuccess", "#2");
            onFailure(TGS_UnSafe.toRuntimeException(d.className, "onSuccess", "ERROR: onSuccess -> response==null"));
            return;
        }
        if (!(response instanceof TGS_SGWTFuncBase)) {
            d.ci("onSuccess", "#3");
            onFailure(TGS_UnSafe.toRuntimeException(d.className, "onSuccess", "ERROR: !(response instanceof " + TGS_SGWTFuncBase.class.getSimpleName() + "): " + response));
            return;
        }
        d.ci("onSuccess", "#4");
        var funcBase = (T) response;
        d.ci("onSuccess", "#5");
        if (funcBase.getExceptionMessage() != null) {
            d.ci("onSuccess", "#6");
            var errMsg = funcBase.getExceptionMessage();
            if (errMsg.contains("cannot run (validate)")) {
                errMsg = "[UYARI: Yetkilendirilmiş gün sayısından daha önceki bir kayıtta değişiklik yapılmaya çalışılmış olabilir.]" + errMsg;
            }
            d.ci("onSuccess", "#7");
            onFailure(TGS_UnSafe.toRuntimeException(d.className, "onSuccess", "ERROR: onSuccess -> getMessage: " + errMsg));
            d.ci("onSuccess", "#8");
            return;
        }
        d.ci("onSuccess", "#10");
        runnable.run((T) response);
        d.ci("onSuccess", "#11");
        if (closure != null) {
            closure.run();
        }
        d.ci("onSuccess", "#12");
    }
}
