module com.tugalsan.api.servlet.gwt {
    requires gwt.user;
    requires javax.websocket.api;
    requires javax.servlet.api;
//    requires elemental2.promise;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.file.json;
    requires com.tugalsan.api.file.txt;    
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.time;
    requires com.tugalsan.api.function;
    requires com.tugalsan.api.thread;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.network;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.url;
    exports com.tugalsan.api.servlet.gwt.client;
    exports com.tugalsan.api.servlet.gwt.client.ws;
    exports com.tugalsan.api.servlet.gwt.server;
}
