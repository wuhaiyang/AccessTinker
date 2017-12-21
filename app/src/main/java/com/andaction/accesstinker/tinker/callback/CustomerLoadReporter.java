/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker.callback;

import android.content.Context;
import android.util.Log;

import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.File;

/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-11-08 下午11:27<br/>
 * <p>
 * tinker在加载补丁时的一些回调
 * </p>
 */
public class CustomerLoadReporter extends DefaultLoadReporter {

    public CustomerLoadReporter(Context context) {
        super(context);
    }

    @Override
    public void onLoadResult(File patchDirectory, int loadCode, long cost) {
        super.onLoadResult(patchDirectory, loadCode, cost);
        Log.w("@@@@ L32", "CustomerLoadReporter:onLoadResult() -> " + "loadCode = " + loadCode + " cost = " + cost);
        if (loadCode == ShareConstants.ERROR_LOAD_OK) {
            // 一旦补丁加载成功 此方法每次都会回调
            Log.w("@@@@ L38", "CustomerLoadReporter:onLoadResult() -> " + patchDirectory.getAbsolutePath());
            // 补丁真正加载成功 效果展现出来
            getTinkerLoadResult();
            Log.w("@@@@ L35", "CustomerLoadReporter:onLoadResult() -> " + "补丁加载成功");
        } else {
            // 其他错误原因
        }

    }

    private void getTinkerLoadResult() {
        boolean tinkerLoaded = Tinker.with(context).isTinkerLoaded();
        Log.w("@@@@ L51", "CustomerLoadReporter:getTinkerLoadResult() -> " + tinkerLoaded);
        if (tinkerLoaded) {
            TinkerLoadResult tinkerLoadResult = Tinker.with(context).getTinkerLoadResultIfPresent();
            String patchMessage = tinkerLoadResult.getPackageConfigByName("patchMessage");
            Log.w("@@@@ L55", "CustomerLoadReporter:getTinkerLoadResult() -> " + patchMessage);
        }
    }


    @Override
    public void onLoadException(Throwable e, int errorCode) {
        Log.w("@@@@ L46", "CustomerLoadReporter:onLoadException() -> " + errorCode);
        super.onLoadException(e, errorCode);
    }

    @Override
    public void onLoadPatchListenerReceiveFail(File patchFile, int errorCode) {
        super.onLoadPatchListenerReceiveFail(patchFile, errorCode);
        // 加载补丁之前，补丁文件校验无法通过
        Log.w("@@@@ L72", "CustomerLoadReporter:onLoadPatchListenerReceiveFail() -> " + errorCode);
    }

    @Override
    public void onLoadPackageCheckFail(File patchFile, int errorCode) {
        Log.w("@@@@ L56", "CustomerLoadReporter:onLoadPackageCheckFail() -> " + errorCode);
        //加载补丁 发现补丁无效：签名、 tinkerID 如果补丁文件无褚篡改
        super.onLoadPackageCheckFail(patchFile, errorCode);
    }
}
