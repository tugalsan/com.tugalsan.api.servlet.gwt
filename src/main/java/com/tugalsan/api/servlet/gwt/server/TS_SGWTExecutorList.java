package com.tugalsan.api.servlet.gwt.server;

import java.util.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.pack.client.*;
import com.tugalsan.api.thread.server.*;

public class TS_SGWTExecutorList {

    final private static TS_Log d = TS_Log.of(TS_SGWTExecutorList.class);

    public static TS_ThreadSafeLst<TGS_Pack2<String, TS_SGWTExecutor>> SYNC = new TS_ThreadSafeLst();

    public static TS_SGWTExecutor add(TS_SGWTExecutor exe) {
        SYNC.add(new TGS_Pack2(exe.name(), exe));
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

    public static TGS_Pack2<String, TS_SGWTExecutor> get(String name) {
        return SYNC.findFirst(item -> Objects.equals(item.value0, name));
    }
}
