package com.tugalsan.api.servlet.gwt.client;

import com.tugalsan.api.executable.client.*;
import com.tugalsan.api.log.client.*;

public class TGC_SGWTCalller {

    final private static TGC_Log d = TGC_Log.of(TGC_SGWTCalller.class);

    public static <T extends TGS_SGWTFuncBase> void async(T func, TGS_ExecutableType1<T> executor) {
        async(func, executor, null, null);
    }

    public static <T extends TGS_SGWTFuncBase> void async(T func, TGS_ExecutableType1<T> executor, TGS_Executable closure) {
        async(func, executor, null, closure);
    }

    public static <T extends TGS_SGWTFuncBase> void async(T func, TGS_ExecutableType1<T> executor, TGS_ExecutableType1<Throwable> onFail) {
        async(func, executor, onFail, null);
    }

    public static <T extends TGS_SGWTFuncBase> void async(T func, TGS_ExecutableType1<T> executor, TGS_ExecutableType1<Throwable> onFail, TGS_Executable closure) {
        d.ci("async", func.getSuperClassName(), func);
        TGC_SGWTService.getServiceInstance().call(func, new TGC_SGWTResponse(executor, onFail, closure));
    }

    /*
    @Deprecated //NOT SUPPORTED YET
    public static <T extends TGS_SGWTFuncBase> T syncPromise(T func) {
        Promise<String> promise = new Promise((resolve, reject) -> {
            Promise.resolve("done");
            Promise.reject(new RE("Whoops!"));
        });
//        promise.then((resolve)->{});
        return null;
    }

    @Deprecated //NOT SUPPORTED YET
    public static <T extends TGS_SGWTFuncBase> T syncCompletableFuture(T func) {
        tryy {
            var future = new CompletableFuture<TGS_SGWTFuncBase>();
            TGC_SGWTService.getServiceInstance().call(func, new AsyncCallback<TGS_SGWTFuncBase>() {
                @Override
                public void onFailure(Throwable t) {
                    future.completeExceptionally(t);
                }

                @Override
                public void onSuccess(TGS_SGWTFuncBase f) {
                    if (f == null) {
                        onFailure(new RE("onSuccess." + TGS_SGWTFuncBase.class.getSimpleName() + " == null"));
                    } else {
                        future.complete(f);
                    }
                }
            });
            return (T) future.get();
        } catch (Exception e) {
            d.ct("syncCompletableFuture", e);
            return null;
        }
    }
     */
}
