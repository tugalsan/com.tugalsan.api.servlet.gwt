package com.tugalsan.api.servlet.gwt.server;

import module com.tugalsan.api.log;
import module com.tugalsan.api.thread;
import java.util.*;

public class TS_SGWTExecutorList {

    final private static TS_Log d = TS_Log.of(TS_SGWTExecutorList.class);

    public static TS_ThreadSyncLst<TS_SGWTExecutorListItem> SYNC = TS_ThreadSyncLst.ofSlowWrite();

    public static TS_SGWTExecutor add(TS_SGWTExecutor exe) {
        SYNC.add(new TS_SGWTExecutorListItem(exe.name(), exe));
        d.cr("add", exe.name());
        return exe;
    }

    public static TS_SGWTExecutor[] add(TS_SGWTExecutor... exe) {
        Arrays.stream(exe).forEachOrdered(f -> add(f));
        return exe;
    }

    public static List<TS_SGWTExecutor> add(List<TS_SGWTExecutor> exe) {
        exe.forEach(f -> add(f));
        return exe;
    }

    public static TS_SGWTExecutorListItem get(String name) {
        return SYNC.findFirst(item -> Objects.equals(item.name(), name));
    }
}
