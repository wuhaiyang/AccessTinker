/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker.callback;

import android.util.Log;

import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;

import java.io.File;


/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-09-27 上午10:18<br/>
 * <p>
 * 自定义Tinker合成完成行为
 * </p>
 */
public class CustomerResultService extends DefaultTinkerResultService {


    @Override
    public void onPatchResult(PatchResult result) {
        if (null == result) {
            Log.w("@@@@ L28", "CustomerResultService:onPatchResult() -> " + "null == result why");
            return;
        }
        if (result.isSuccess) {
//            getTinkerLoadResult();
            Log.w("@@@@ L37", "CustomerResultService:onPatchResult() -> " + "patchVersion = " + result.patchVersion);
            // 补丁合成成功
            deleteRawPatchFile(new File(result.rawPatchFilePath));
            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            if (checkIfNeedKill(result)) {
                restartProcess();
            } else {
                Log.w("@@@@ L50", "CustomerResultService:onPatchResult() -> " + "I have already install the newly patch version!");
            }
        } else {

        }
    }

    private void getTinkerLoadResult() {
        boolean tinkerLoaded = Tinker.with(getApplicationContext()).isTinkerLoaded();
        Log.w("@@@@ L60", "CustomerResultService:getTinkerLoadResult() -> " + "tinkerLoaded = " + tinkerLoaded);
        if (tinkerLoaded) {
            TinkerLoadResult tinkerLoadResult = Tinker.with(getApplicationContext()).getTinkerLoadResultIfPresent();
            String patchMessage = tinkerLoadResult.getPackageConfigByName("patchMessage");
            Log.w("@@@@ L64", "CustomerResultService:getTinkerLoadResult() -> " + patchMessage);
        }
    }


    private void restartProcess() {
        //you can send service or broadcast intent to restart your process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
