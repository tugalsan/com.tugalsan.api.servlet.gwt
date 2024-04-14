package com.tugalsan.api.servlet.gwt.client.ws;

import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.thread.client.TGC_ThreadUtils;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import com.tugalsan.api.url.client.TGS_Url;

//https://stackoverflow.com/questions/74175629/broadcast-message-in-websocket-java
public class TGC_SGWTWebSocketTest {

    final private static TGC_Log d = TGC_Log.of(true, TGC_SGWTWebSocketTest.class);

    public static TGS_UnionExcuseVoid testClient(TGS_Url urlApp) {
        if (!TGC_SGWTWebSocket.isSupported()) {
            return TGS_UnionExcuseVoid.ofExcuse(d.className, "testClient", "!TGC_SGWTWebSocket.isSupported()");
        }
        d.ci("testClient", "urlApp", urlApp);
        var u_ws = TGC_SGWTWebSocket.ofUrlApp(urlApp);
        if (u_ws.isExcuse()) {
            return u_ws.toExcuseVoid();
        }
        var ws = u_ws.value();
        ws.addListener(new TGC_SGWTWebSocketListener() {

            @Override
            public void onClose(TGC_SGWTWebSocketCloseEvent event) {
                d.ci("testClient", "onClose");
            }

            @Override
            public void onMessage(String msg) {
                d.ci("testClient", "onMessage", msg);
            }

            @Override
            public void onOpen() {
                d.ci("testClient", "onOpen");
            }
        });
        d.ci("testClient", "state", "CONNECTING = 0, OPEN = 1, CLOSING = 2, CLOSED = 3", ws.url);
        ws.open();
        if (true) {
            return TGS_UnionExcuseVoid.ofVoid();
        }
        d.ci("testClient", "state", "after_open", ws.afterOpenState());
        TGC_ThreadUtils.create_afterGUIUpdate(t -> {
            if (ws.afterOpenState() == 0) {
                d.ci("testClient", "state", "state0", ws.afterOpenState());
                t.run_afterSeconds(5);
                return;
            }
            if (ws.afterOpenState() != 1) {
                d.ci("testClient", "state", "not1", ws.afterOpenState());
                return;
            }
            ws.send("time is " + TGS_Time.toString_now());
            d.ci("testClient", "state", "after_send", ws.afterOpenState());
            TGC_ThreadUtils.run_afterSeconds_afterGUIUpdate(t2 -> {
                ws.close();
                d.ci("testClient", "state", "after_close", ws.afterOpenState());
            }, 10);
        }).run_afterSeconds(1);
        return TGS_UnionExcuseVoid.ofVoid();
    }
}
