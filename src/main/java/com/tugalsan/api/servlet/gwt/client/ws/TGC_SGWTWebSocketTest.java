package com.tugalsan.api.servlet.gwt.client.ws;

import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocketCloseEvent;
import com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocketWebsocket;
import com.tugalsan.api.thread.client.TGC_ThreadUtils;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocketListener;

public class TGC_SGWTWebSocketTest {

    final private static TGC_Log d = TGC_Log.of(TGC_SGWTWebSocketTest.class);

    public static boolean testClient(TGS_Url urlApp) {
        if (!TGC_SGWTWebSocketWebsocket.isSupported()) {
            return false;
        }
        d.ci("ws", "url", urlApp);
        var ws = TGC_SGWTWebSocketWebsocket.ofUrlApp(urlApp);
        ws.addListener(new TGC_SGWTWebSocketListener() {

            @Override
            public void onClose(TGC_SGWTWebSocketCloseEvent event) {
                d.ci("ws", "onClose");
            }

            @Override
            public void onMessage(String msg) {
                d.ci("ws", "onMessage", msg);
            }

            @Override
            public void onOpen() {
                d.ci("ws", "onOpen");
            }
        });
        d.ci("ws", "state", "CONNECTING = 0, OPEN = 1, CLOSING = 2, CLOSED = 3");
        ws.open();
        d.ci("ws", "state", "after_open", ws.afterOpenState());
        TGC_ThreadUtils.create_afterGUIUpdate(t -> {
            if (ws.afterOpenState() == 0) {
                d.ci("ws", "state", "state0", ws.afterOpenState());
                t.run_afterSeconds(5);
                return;
            }
            if (ws.afterOpenState() != 1) {
                d.ci("ws", "state", "not1", ws.afterOpenState());
                return;
            }
            ws.send("naber" + TGS_Time.toString_now());
            d.ci("ws", "state", "after_send", ws.afterOpenState());
            TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t2 -> {
                ws.close();
                d.ci("ws", "state", "after_close", ws.afterOpenState());
            }, 10);
        }).run_afterSeconds(1);
        return true;
    }
}
