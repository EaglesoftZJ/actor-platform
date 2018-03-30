package com.huawei.android.hms.agent.sns.Handler;

import android.content.Intent;

/**
 * 获取发送消息的intent回调
 */
public interface GetMsgSendIntentHandler {
    /**
     * 获取到的intent回调
     * @param retnCode 结果码
     * @param intent 用于拉起发送消息界面的intent
     */
    void onResult(int retnCode, Intent intent);
}
