package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.executable.client.*;

public class TGC_SGWTResponse<T extends TGS_SGWTFuncBase> implements AsyncCallback, IsSerializable {

    private static String PREFIX_SERVER_DOWN_MESSAGE() {
        return "0  ";
    }

    private static String PREFIX_SERVER_INTERNAL_MESSAGE() {
        return "500  ";
    }

    final private static TGC_Log d = TGC_Log.of(TGC_SGWTResponse.class.getSimpleName());

    public TGC_SGWTResponse() {
    }

    public TGC_SGWTResponse(TGS_ExecutableType1<T> executor, TGS_ExecutableType1<Throwable> onFail, TGS_Executable closure) {
        this.executor = executor;
        this.onFail = onFail;
        this.closure = closure;
    }
    private TGS_ExecutableType1<T> executor;
    private TGS_ExecutableType1<Throwable> onFail;
    private TGS_Executable closure;

    @Override
    final public void onFailure(Throwable caught) {
        if (caught == null) {
            d.ce("onFailure", "caught == null");
            return;
        }
        var msg = caught.getMessage();
        if (msg == null) {
            d.ce("onFailure", "caught.getMessage() == null");
            return;
        }
        if (msg.startsWith(PREFIX_SERVER_DOWN_MESSAGE())) {
            d.ce("onFailure", "HATA: Bağlantı koptu; güncelleniyor olabilir; network bağlantınızı kontrol edip, bekleyiniz...");
            return;
        }
        if (msg.startsWith(PREFIX_SERVER_INTERNAL_MESSAGE())) {
            d.ce("onFailure", "HATA: Server makinesinde hata oluştu! Ayrıntılar için server makinesinin hata kayıtlarına bakınız.");
            var tmp = d.infoEnable;
            d.infoEnable = true;
            d.ci("onFailure", "BİLGİ: Hiç işlem yapamıyorsanız, kullanıcı girişinizi kontrol edebilirsiniz.");
            d.infoEnable = tmp;
            return;
        }
        if (onFail != null) {
            onFail.execute(caught);
        }
        if (closure != null) {
            closure.execute();
        }
    }

    @Override
    final public void onSuccess(Object response) {
        
        if (response == null) {
            onFailure(new RuntimeException("ERROR: onSuccess -> response==null"));
            return;
        } else if (((T) response).getExceptionMessage() != null) {
            onFailure(new RuntimeException(((T) response).getExceptionMessage()));
            return;
        }
        if (!(response instanceof TGS_SGWTFuncBase)){
            onFailure(new RuntimeException("ERROR: !(response instanceof TGS_SGWTFuncBase): " + response));
            return;
        }
        executor.execute((T) response);
        if (closure != null) {
            closure.execute();
        }
    }
}
