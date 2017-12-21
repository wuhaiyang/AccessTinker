package com.andaction.accesstinker.tinker;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-12-21 下午2:41<br/>
 * <p>
 * application
 * </p>
 */
@DefaultLifeCycle(application = "com.andaction.accesstinker.tinker.BaseTinkerApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL)
public class TinkerApplicationLike extends DefaultApplicationLike {

    public TinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
//        MultiDex.install(base);
        TinkerManager.installTinker(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
