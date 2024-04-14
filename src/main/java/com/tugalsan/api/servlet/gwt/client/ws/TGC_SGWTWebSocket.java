/**
 * Copyright 2013 Stephen Samuel
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tugalsan.api.servlet.gwt.client.ws;

import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.api.url.client.parser.TGS_UrlParser;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Stephen K Samuel 14 Sep 2012 08:58:55
 */
public class TGC_SGWTWebSocket {

    private static int counter = 1;

    private static native boolean _isWebsocket() /*-{
        return ("WebSocket" in window);
    }-*/;

    public static boolean isSupported() {
        return _isWebsocket();
    }

    private final Set<TGC_SGWTWebSocketListener> listeners = new HashSet();

    public final String varName;
    public final String url;

    private TGC_SGWTWebSocket(String url) {
        this.url = url;
        this.varName = "gwtws-" + counter++;
    }

    public static TGC_SGWTWebSocket ofUrlWs(TGS_Url wsUrl) {
        return new TGC_SGWTWebSocket(wsUrl.toString());
    }

    public static TGS_UnionExcuse<TGC_SGWTWebSocket> ofUrlApp(TGS_Url urlApp) {
        var u_parser = TGS_UrlParser.of(urlApp);
        if (u_parser.isExcuse()) {
            return u_parser.toExcuse();
        }
        var parser = u_parser.value();
        var wsUrl = TGS_Url.of("wss://" + parser.host.domain + ":" + parser.host.port + "/" + parser.path.paths.get(0) + "/ws");
        return TGS_UnionExcuse.of(ofUrlWs(wsUrl));
    }

    private native void _close(String s) /*-{
        $wnd[s].close();
    }-*/;

    private native void _open(TGC_SGWTWebSocket ws, String s, String url)/*-{
        $wnd[s] = new WebSocket(url);
        $wnd[s].onopen = function() { ws.@com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocket::onOpen()(); };
        $wnd[s].onclose = function(evt) { ws.@com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocket::onClose(SLjava/lang/String;Z)(evt.code, evt.reason, evt.wasClean); };
        $wnd[s].onerror = function() { ws.@com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocket::onError()(); };
        $wnd[s].onmessage = function(msg) { ws.@com.tugalsan.api.servlet.gwt.client.ws.TGC_SGWTWebSocket::onMessage(Ljava/lang/String;)(msg.data); }
    }-*/;

    private native void _send(String s, String msg) /*-{
        $wnd[s].send(msg);
    }-*/;

    private native int _state(String s) /*-{
        return $wnd[s].readyState;
    }-*/;

    public void addListener(TGC_SGWTWebSocketListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TGC_SGWTWebSocketListener listener) {
        listeners.remove(listener);
    }

    public void close() {
        _close(varName);
    }

    public int afterOpenState() {
        return _state(varName);
    }

    protected void onClose(short code, String reason, boolean wasClean) {
        var ev = new TGC_SGWTWebSocketCloseEvent(code, reason, wasClean);
        listeners.forEach(l -> l.onClose(ev));
    }

    protected void onError() {
        listeners.forEach(l -> {
            if (l instanceof TGC_SGWTWebSocketListenerExt) {
                ((TGC_SGWTWebSocketListenerExt) l).onError();
            }
        });
    }

    protected void onMessage(String msg) {
        listeners.forEach(l -> {
            l.onMessage(msg);
            if (l instanceof TGC_SGWTWebSocketListenerBinary) {
                ((TGC_SGWTWebSocketListenerBinary) l).onMessage(TGC_SGWTWebSocketBase64Utils.fromBase64(msg));
            }
        });
    }

    protected void onOpen() {
        listeners.forEach(l -> l.onOpen());
    }

    public void open() {
        _open(this, varName, url);
    }

    public void send(String msg) {
        _send(varName, msg);
    }

    public void send(byte[] bytes) {
        send(TGC_SGWTWebSocketBase64Utils.toBase64(bytes));
    }
}
