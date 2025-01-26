package com.tugalsan.api.servlet.gwt.client;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.function.client.TGS_Func;
import com.tugalsan.api.function.client.TGS_Func_In1;
import com.tugalsan.api.function.client.TGS_Func_OutTyped;
import com.tugalsan.api.function.client.TGS_Func_OutTyped_In1;
import com.tugalsan.api.log.client.*;

import com.tugalsan.api.unsafe.client.*;

public class TGC_SGWTResponse<T extends TGS_SGWTFuncBase> implements AsyncCallback, IsSerializable {

    final private static TGC_Log d = TGC_Log.of(TGC_SGWTResponse.class);
    final public static String CANNOT_FETCH_REQUEST = "CANNOT_FETCH_REQUEST";
    final public static String CANNOT_FETCH_CLIENTIP = "CANNOT_FETCH_CLIENTIP";
    final public static String CANNOT_FIND_SERVLET = "CANNOT_FIND_SERVLET";
    final public static String VALIDATE_RESULT_TIMEOUT = "VALIDATE_RESULT_TIMEOUT";
    final public static String VALIDATE_RESULT_EMPTY = "VALIDATE_RESULT_EMPTY";
    final public static String VALIDATE_RESULT_FALSE = "VALIDATE_RESULT_FALSE";
    final public static String VALIDATE_RESULT_KILLED = "VALIDATE_RESULT_KILLED";
    public static TGS_Func_OutTyped<Boolean> isUserOut = () -> {
        return null;//TGC_LibLoginCardUtils.async(_loginCard -> {});
    };
    public static TGS_Func_OutTyped_In1<String, String> onSuccess_and_exceptionMessageNotNull = errMsg -> {
        if (errMsg.contains(CANNOT_FETCH_REQUEST)) {
            return "UYARI: " + CANNOT_FETCH_REQUEST + " Hizmet sağlayıcı aktif değil; beklemek işe yaramaz ise, IT departmanından programın tekrar başlatılması için isteyiniz. Detaylar: " + errMsg;
        }
        if (errMsg.contains(CANNOT_FETCH_CLIENTIP)) {
            return "UYARI: " + CANNOT_FETCH_CLIENTIP + " Müşteri kimliği algılanamadı; kod hatası veya güvenlik ihlali söz konusu olabilir; Kod güncellemeleri için tüm sekmeleri kapatınız; geçmişi siliniz; tekrar giriniz. Detaylar: " + errMsg;
        }
        if (errMsg.contains(CANNOT_FIND_SERVLET)) {
            return "UYARI: " + CANNOT_FIND_SERVLET + " Hizmet sağlayıcı isteğe cevap vermiyor; yeniden başlıyor olabilir; beklemek işe yaramaz ise, IT departmanından programın tekrar başlatılması için isteyiniz. Detaylar: " + errMsg;
        }
        if (errMsg.contains(VALIDATE_RESULT_TIMEOUT)) {
            return "UYARI: " + VALIDATE_RESULT_TIMEOUT + " İstek zaman aşımına uğramış; [Tablo]'da [İşlemler]'den [max-saniye]'yi arttırabilirsiniz Detaylar: " + errMsg;
        }
        if (errMsg.contains(VALIDATE_RESULT_EMPTY)) {
            return "UYARI: " + VALIDATE_RESULT_EMPTY + " Doğrulama sonucu boş döndü; kod hatası veya güvenlik ihlali söz konusu olabilir; Ne yaparken bu hatayı aldığınız konusunda IT departmanını bilgilendiriniz. Detaylar: " + errMsg;
        }
        if (errMsg.contains(VALIDATE_RESULT_FALSE)) {
            var userOut = isUserOut.call();
            if (userOut == null) {
                return "UYARI: " + VALIDATE_RESULT_FALSE + " Kullancı çıkmış veya yetkilendirilmiş gün sayısından daha önceki bir kayıtta değişiklik yapılmaya çalışılmış] olabilir. Kullanıcı çıkmış ise, tüm sekmeleri kapatıp tekrar girin. Yetkilendirmeler için Üst yönetime başvurunuz. Detaylar: " + errMsg;
            } else {
                if (userOut) {
                    return "UYARI: " + VALIDATE_RESULT_FALSE + " Kullancı çıkmış; tüm sekmeleri kapatıp tekrar girin. Detaylar: " + errMsg;
                } else {
                    return "UYARI: " + VALIDATE_RESULT_FALSE + " Yetkilendirilmiş gün sayısından daha önceki bir kayıtta değişiklik yapılmaya çalışılmış] olabilir; Yetkilendirmeler için Üst yönetime başvurunuz. Detaylar: " + errMsg;
                }
            }
        }
        return errMsg;
    };

    private static String PREFIX_SERVER_DOWN_MESSAGE() {
        return "0  ";
    }

    private static String PREFIX_SERVER_INTERNAL_MESSAGE() {
        return "500  ";
    }

    public TGC_SGWTResponse() {
    }

    public TGC_SGWTResponse(TGS_Func_In1<T> runnable, TGS_Func_In1<Throwable> onFail, TGS_Func closure) {
        this.runnable = runnable;
        this.onFail = onFail;
        this.closure = closure;
    }
    private TGS_Func_In1<T> runnable;
    private TGS_Func_In1<Throwable> onFail;
    private TGS_Func closure;

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
            onFailure(TGS_UnSafe.toRuntimeException(d.className, "onSuccess", "ERROR: onSuccess -> getMessage: " + onSuccess_and_exceptionMessageNotNull.call(funcBase.getExceptionMessage())));
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
