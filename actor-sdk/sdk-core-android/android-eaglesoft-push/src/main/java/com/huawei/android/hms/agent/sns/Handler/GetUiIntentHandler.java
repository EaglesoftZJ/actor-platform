package com.huawei.android.hms.agent.sns.Handler;

import android.content.Intent;

/**
 * 获取拉起社交界面的intent
 */
public interface GetUiIntentHandler {
    /**
     * 社交界面intent回调
     * @param retnCode 结果码
     * @param intent 拉起社交界面的intent
     */
    void onResult(int retnCode, Intent intent);
}
