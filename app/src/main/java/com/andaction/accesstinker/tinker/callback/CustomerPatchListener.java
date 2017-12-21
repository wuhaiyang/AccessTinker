/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker.callback;

import android.content.Context;
import android.util.Log;

import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.loader.shareutil.ShareConstants;


/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-11-08 下午11:55<br/>
 * <p>
 * 用来过滤Tinker收到的补丁包的修复、升级请求也就是决定我们是不是真的要唤起:patch进程去尝试补丁合成
 * </p>
 */
public class CustomerPatchListener extends DefaultPatchListener {

    public static final int ERROR_PATCH_ROM_SPACE = -21;

    protected static final long NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN = 60 * 1024 * 1024;

    public CustomerPatchListener(Context context) {
        super(context);
    }

    @Override
    protected int patchCheck(String path, String patchMd5) {
//        return super.patchCheck(path, patchMd5);
        Log.w("@@@@ L36", "CustomerPatchListener:patchCheck() -> " + path + '\n' + patchMd5);
        int returnCode = super.patchCheck(path, patchMd5);
        return returnCode;
    }


}
