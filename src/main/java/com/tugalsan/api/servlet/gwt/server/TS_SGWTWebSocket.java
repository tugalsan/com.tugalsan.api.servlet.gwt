package com.tugalsan.api.servlet.gwt.server;

import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuse;

import java.io.*;
import javax.websocket.*;
import javax.websocket.server.*;

@ServerEndpoint("/ws")
public class TS_SGWTWebSocket {

//    final private static TS_Log d = TS_Log.of(TS_SGWTWebSocket.class);
    private TGS_UnionExcuse<String> onBroadcast(Session session, String msg) {
        return TGS_FuncMTCUtils.call(() -> {
            for (var s : session.getOpenSessions()) {
                s.getBasicRemote().sendText("onBroadcast: " + msg);
            }
            return TGS_UnionExcuse.of(msg);
        }, e -> {
            return TGS_UnionExcuse.ofExcuse(e);
        });
    }

    @OnOpen
    public void onOpen(Session session) {
        onBroadcast(session, "onOpen");
    }

    @OnClose
    public void onClose(Session session) {
        onBroadcast(session, "onClose");
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        onBroadcast(session, "onError:" + error.getMessage());
    }

    @OnMessage
    public String onMessage(Session session, String msg) {
        onBroadcast(session, msg);
        return "onMessage:" + msg;
    }

//    public static testClient(TGS_Url url) {
//        TGS_FuncMTCUtils.run(() -> {
//            var parser = TGS_UrlParser.of(url);
//            var url = TGS_Url.of("wss://" + parser.host.domain + ":" + parser.host.port + "/" + parser.path.paths.get(0) + "/ws");
//            var client = new TS_SGWTWebSocket();
//            client.connect(URI.create(url.toString()));
//            client.send("Hello World!");
//            Thread.sleep(Integer.MAX_VALUE);
//        });
//    }
}
