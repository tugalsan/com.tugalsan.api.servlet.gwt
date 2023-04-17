package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.runnable.client.*;
import com.tugalsan.api.unsafe.client.*;

public class TGC_SGWTResponse<T extends TGS_SGWTFuncBase> implements AsyncCallback, IsSerializable {

    private static String PREFIX_SERVER_DOWN_MESSAGE() {
        return "0  ";
    }

    private static String PREFIX_SERVER_INTERNAL_MESSAGE() {
        return "500  ";
    }

    final private static TGC_Log d = TGC_Log.of(TGC_SGWTResponse.class);

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
        if (caught == null) {
            d.ce("onFailure", "caught == null");
        } else if (caught.getMessage() == null) {
            d.ce("onFailure", "caught.getMessage() == null");
        } else if (caught.getMessage().startsWith(PREFIX_SERVER_DOWN_MESSAGE())) {
            d.ce("onFailure", "HATA: Bağlantı koptu; güncelleniyor olabilir; network bağlantınızı kontrol edip, bekleyiniz...");
        } else if (caught.getMessage().startsWith(PREFIX_SERVER_INTERNAL_MESSAGE())) {
            d.ce("onFailure", "HATA: Server makinesinde hata oluştu! Ayrıntılar için server makinesinin hata kayıtlarına bakınız. (Hiç işlem yapamıyorsanız, kullanıcı girişinizi kontrol edebilirsiniz.)");
        }
        if (onFail != null) {
            onFail.run(caught);
        }
        if (closure != null) {
            closure.run();
        }
    }

    @Override
    final public void onSuccess(Object response) {
        if (response == null) {
            onFailure(TGS_UnSafe.toRuntimeException(TGC_SGWTResponse.class.getSimpleName(), "onSuccess", "ERROR: onSuccess -> response==null"));
            return;
        } else if (((T) response).getExceptionMessage() != null) {
            onFailure(TGS_UnSafe.toRuntimeException(TGC_SGWTResponse.class.getSimpleName(), "onSuccess", ((T) response).getExceptionMessage()));
            return;
        }
        if (!(response instanceof TGS_SGWTFuncBase)) {
            onFailure(TGS_UnSafe.toRuntimeException(TGC_SGWTResponse.class.getSimpleName(), "onSuccess", "ERROR: !(response instanceof " + TGS_SGWTFuncBase.class.getSimpleName() + "): " + response));
            return;
        }
        runnable.run((T) response);
        if (closure != null) {
            closure.run();
        }
    }
}
