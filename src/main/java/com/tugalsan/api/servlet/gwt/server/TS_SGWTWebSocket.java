package com.tugalsan.api.servlet.gwt.server;

import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;
import java.io.*;
import javax.websocket.*;
import javax.websocket.server.*;

@ServerEndpoint("/ws")
public class TS_SGWTWebSocket {

//    final private static TS_Log d = TS_Log.of(TS_SGWTWebSocket.class);
    private TGS_UnionExcuseVoid onBroadcast(Session session, String msg) {
        for (var s : session.getOpenSessions()) {
            try {
                s.getBasicRemote().sendText("onBroadcast: " + msg);
            } catch (IOException ex) {
                return TGS_UnionExcuseVoid.ofExcuse(ex);
            }
        }
        return TGS_UnionExcuseVoid.ofVoid();
    }

    @OnOpen
    public void onOpen(Session session) {
        var u = onBroadcast(session, "onOpen");
        if (u.isExcuse()) {
            //WHAT TO DO
        }
    }

    @OnClose
    public void onClose(Session session) {
        var u = onBroadcast(session, "onClose");
        if (u.isExcuse()) {
            //WHAT TO DO
        }
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        var u = onBroadcast(session, "onError:" + error.getMessage());
        if (u.isExcuse()) {
            //WHAT TO DO
        }
    }

    @OnMessage
    public String onMessage(Session session, String msg) {
        var u = onBroadcast(session, msg);
        if (u.isExcuse()) {
            return "onMessage.failed:" + msg;
        }
        return "onMessage.success:" + msg;
    }

//    public static testClient(TGS_Url url) {
//        TGS_UnSafe.run(() -> {
//            var parser = TGS_UrlParser.of(url);
//            var url = TGS_Url.of("wss://" + parser.host.domain + ":" + parser.host.port + "/" + parser.path.paths.get(0) + "/ws");
//            var client = new TS_SGWTWebSocket();
//            client.connect(URI.create(url.toString()));
//            client.send("Hello World!");
//            Thread.sleep(Integer.MAX_VALUE);
//        });
//    }
}
