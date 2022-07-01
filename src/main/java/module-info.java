module com.tugalsan.api.servlet.gwt {
    requires gwt.user;
    requires elemental2.promise;
    requires com.tugalsan.api.executable;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.validator;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.network;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.pack;
    requires com.tugalsan.api.url;
    exports com.tugalsan.api.servlet.gwt.client;
    exports com.tugalsan.api.servlet.gwt.server;
}
