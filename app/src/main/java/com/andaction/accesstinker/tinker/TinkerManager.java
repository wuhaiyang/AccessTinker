/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andaction.accesstinker.tinker.callback.CustomerLoadReporter;
import com.andaction.accesstinker.tinker.callback.CustomerPatchListener;
import com.andaction.accesstinker.tinker.callback.CustomerPatchReporter;
import com.andaction.accesstinker.tinker.callback.CustomerResultService;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;

import java.io.File;

/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-09-27 上午10:16<br/>
 * <p>
 * tinker api 调用封装
 * </p>
 */
public class TinkerManager {

    public static final String APPID = "949bc110-e0d9-11e7-bf41-a5294834d833";

    public static final String SP_FILE_NAME = "private-tinker-patch";
    public static final String SP_KEY_PATCH = "patch-md5";

    private static boolean isInstalled = false;

    private static ApplicationLike applicationLike;

    public static void installTinker(@NonNull ApplicationLike like) {
        applicationLike = like;
        if (isInstalled) {
            return;
        }
        TinkerInstaller.install(like, new CustomerLoadReporter(like.getApplication()),
                new CustomerPatchReporter(like.getApplication()),
                new CustomerPatchListener(like.getApplication()),
                CustomerResultService.class, new UpgradePatch());
        TinkerInstaller.install(like);
        // 自定义错误日志
        TinkerInstaller.setLogIml(new MyLogImp());
        isInstalled = true;
    }

    public static void loadPatch(String patchPath) {
        if (Tinker.isTinkerInstalled()) {
            File file = new File(patchPath);
            if (file.exists() && file.length() > 0) {
                TinkerInstaller.onReceiveUpgradePatch(getContext(), patchPath);
            }
        }
    }

    /**
     * 当补丁包存在lib so 更新或者新增，需调用 -> 待验证
     *
     * @param libName
     */
    public static void loadLibrary(String libName) {
        TinkerLoadLibrary.loadArmLibrary(getContext(), libName);
        TinkerLoadLibrary.loadArmV7Library(getContext(), libName);
    }

    private static Context getContext() {
        // 注意在ApplicationLike onBaseContextAttached  方法回调时，like.getContext 可能为null
        return applicationLike.getApplication();
    }

}
