/*
 * Copyright (c) 2017. danlu.com Co.Ltd. All rights reserved.
 */

package com.andaction.accesstinker.tinker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import java.io.File;


/**
 * author: wuhaiyang(<a href="mailto:wuhaiyang@andthink.cn">wuhaiyang@andthink.cn</a>)<br/>
 * version: 1.0.0<br/>
 * since: 2017-11-09 上午10:39<br/>
 * <p>
 * 1. 判断当前基准版本是否有最新的补丁版本
 * 2. 下载补丁文件
 * 3. 合成补丁文件
 * </p>
 */
public class LoadPatchService extends Service {


    /**
     * 获取最新补丁
     */
    private static final int FETCH_PATCH = 593;
    /**
     * 下载补丁操作
     */
    private static final int DOWNLOAD_PATCH = 500;
    /**
     * 调用合成补丁
     */
    private static final int COMPOSITING_PATCH = 177;

    /**
     * 日志上报：更新当前补丁下载量
     */
    private static final int REPORT_DOWNLOADCNT = 476;

    // tinker 官方推荐不使用.apk  防止被运营商劫持
    private static final String PATCH_SUFFIX = ".apk";
    private static final String PATCH_FILE_PATH_SUFFIX = "tinker-patch";

    private String patchFileDir;

    /**
     * 启动补丁检查服务
     *
     * @param context 宿主上下文
     * @param bundle  携带参数
     */
    public static void startSelf(Context context, @Nullable Bundle bundle) {
        Intent patchService = new Intent(context, LoadPatchService.class);
        if (null != bundle) {
            patchService.putExtras(bundle);
        }
        context.startService(patchService);
    }

    private Handler weakHandler = new Handler((Message msg) -> {
        int what = msg.what;
        switch (what) {
            case FETCH_PATCH:
                getNewestPatch();
                break;
            case DOWNLOAD_PATCH:
                downloadPatchFile("");
                break;
            case COMPOSITING_PATCH:
                String patchPath = (String) msg.obj;
                compositePatch(patchPath);
                break;
            case REPORT_DOWNLOADCNT:
                updatePatchDownloadCnt("patchMd5");
                break;
        }
        return true;
    });

    private void updatePatchDownloadCnt(String patchMd5) {
        // 调用http 接口更新补丁下载数量接口
    }

    /**
     * 开始合成补丁文件
     */
    private void compositePatch(String patchPath) {
        TinkerManager.loadPatch(patchPath);
    }

    /**
     * 下载补丁文件
     *
     * @param url
     */
    private void downloadPatchFile(String url) {
        // 1. 下载补丁文件
        // 2. 合成补丁文件
        // 3. 更新补丁下载量
    }

    /**
     * 调用http接口上报当前补丁下载量
     *
     * @param patchMD5
     */
    private void notifyPatchDonwloadCnt(String patchMD5) {
        Message message = Message.obtain();
        message.what = REPORT_DOWNLOADCNT;
        message.obj = patchMD5;
        weakHandler.sendMessageDelayed(message, 100);
    }

    /**
     * 执行合成补丁操作
     *
     * @param absolutePath
     */
    private void notifyCompositePatch(String absolutePath) {
        Message message = Message.obtain();
        message.what = COMPOSITING_PATCH;
        message.obj = absolutePath;
        weakHandler.sendMessage(message);
    }

    /**
     * 检查是否有符合当前自身情况的补丁文件
     */
    private void getNewestPatch() {

    }


    private void checkPatch(String patchMd5) {
        //判断最新补丁用户是否已经合成过

        String lastPatchMd5 = getHasCompositedPatchMd5();
        if (!TextUtils.isEmpty(lastPatchMd5) && lastPatchMd5.equals(patchMd5)) {
            // 说明当前最新补丁文件已被合成成功过
            stopSelf();
            return;
        }
//        String newestPatchLinkUrl = ApiCreator.HOST_URL + patchInfo.downloadUrl;
//        DLog.w("最新补丁文件下载链接是：" + newestPatchLinkUrl);
        Message message = Message.obtain();
        message.obj = "patch_download_url";
        message.what = DOWNLOAD_PATCH;
        weakHandler.sendMessage(message);
    }

    private String getHasCompositedPatchMd5() {
        SharedPreferences sp = getSharedPreferences(TinkerManager.SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(TinkerManager.SP_KEY_PATCH, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        patchFileDir = getExternalCacheDir().getAbsolutePath() + "/" + PATCH_FILE_PATH_SUFFIX;
        File file = new File(patchFileDir);
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weakHandler.sendEmptyMessage(FETCH_PATCH);
        return START_NOT_STICKY; // 被系统回收了不再重启
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != weakHandler) {
            weakHandler.removeCallbacksAndMessages(null);
            weakHandler = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
