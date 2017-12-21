/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker.callback;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;

import java.io.File;


import static com.tencent.tinker.loader.shareutil.ShareConstants.ERROR_PACKAGE_CHECK_TINKER_ID_NOT_EQUAL;

/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-11-08 下午11:49<br/>
 * <p>
 * Tinker在修复或者升级补丁时的一些回调
 * </p>
 */
public class CustomerPatchReporter extends DefaultPatchReporter {

    public CustomerPatchReporter(Context context) {
        super(context);
    }

    /**
     * 补丁合成失败或者成功都会回调的接口
     * 此时，仅仅是合成成功，还需要重新加载才可以看到效果
     *
     * @param patchFile
     * @param success
     * @param cost
     */
    @Override
    public void onPatchResult(File patchFile, boolean success, long cost) {
        super.onPatchResult(patchFile, success, cost);
        if (success) {
            String tips = "补丁合成成功，消耗时间为：" + (cost / 1000) + "s";
            Toast.makeText(context.getApplicationContext(), tips, Toast.LENGTH_LONG).show();
            Log.w("@@@@ L42", "CustomerPatchReporter:onPatchResult() -> " + "补丁合成成功：" + patchFile.getAbsolutePath());
        } else {
            Toast.makeText(context.getApplicationContext(), "补丁合成失败", Toast.LENGTH_LONG).show();
            // 合成失败
            Log.w("@@@@ L42", "CustomerPatchReporter:onPatchResult() -> " + "补丁合成失败");
        }
    }

    /**
     * 补丁合成过程对输入补丁包的检查失败，这里可以通过错误码区分，例如签名校验失败、tinkerId不一致等原因。默认我们会删除临时文件
     *
     * @param patchFile
     * @param errorCode
     */
    @Override
    public void onPatchPackageCheckFail(File patchFile, int errorCode) {
        //  如果补丁文件不删除 每次重启 调用tinker加载补丁 都会回调该方法
        Log.w("@@@@ L54", "CustomerPatchReporter:onPatchPackageCheckFail() -> " + errorCode);
        super.onPatchPackageCheckFail(patchFile, errorCode);
        if (errorCode == ERROR_PACKAGE_CHECK_TINKER_ID_NOT_EQUAL) {
            Tinker.with(context).cleanPatchByVersion(patchFile);
        }
    }

    @Override
    public void onPatchServiceStart(Intent intent) {
        super.onPatchServiceStart(intent);
    }

    @Override
    public void onPatchException(File patchFile, Throwable e) {
        super.onPatchException(patchFile, e);
    }
}
