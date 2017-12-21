/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker;

import android.util.Log;

import com.tencent.tinker.lib.util.TinkerLog;

/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@danlu.com">wuhaiyang@danlu.com</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-11-09 上午10:45<br/>
 * <p>
 * 自定义tinker错误日志
 * </p>
 */
public class MyLogImp implements TinkerLog.TinkerLogImp {

    @Override
    public void v(String tag, String msg, Object... obj) {
        final String log = obj == null ? msg : String.format(msg, obj);
        Log.v("@@@@ L21", "MyLogImp:v() -> " + log);
    }

    @Override
    public void i(String tag, String msg, Object... obj) {
        final String log = obj == null ? msg : String.format(msg, obj);
        Log.i("@@@@ L28", "MyLogImp:i() -> " + log);
    }

    @Override
    public void w(String tag, String msg, Object... obj) {
        final String log = obj == null ? msg : String.format(msg, obj);
        Log.w("@@@@ L33", "MyLogImp:w() -> " + log);
    }

    @Override
    public void d(String tag, String msg, Object... obj) {
        final String log = obj == null ? msg : String.format(msg, obj);
        Log.d("@@@@ L38", "MyLogImp:d() -> " + log);
    }

    @Override
    public void e(String tag, String msg, Object... obj) {
        final String log = obj == null ? msg : String.format(msg, obj);
        Log.e("@@@@ L43", "MyLogImp:e() -> " + log);
    }

    @Override
    public void printErrStackTrace(String tag, Throwable tr, String format, Object... obj) {
        Log.w("@@@@ L48", "MyLogImp:printErrStackTrace() -> tag = " + tag + "\n" +
                " tr = " + tr.getMessage() + "\n" +
                " format = " + format + "\n");
    }
}
