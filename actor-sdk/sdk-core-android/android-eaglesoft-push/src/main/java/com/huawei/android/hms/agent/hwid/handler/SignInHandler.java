package com.huawei.android.hms.agent.hwid.handler;

import com.huawei.hms.support.api.hwid.SignInHuaweiId;

/**
 * 帐号登录回调
 */
public interface SignInHandler {
    /**
     * 登录结果回调
     * @param rtnCode 结果码
     * @param hwidInfo 登录结果
     */
    void onResult(int rtnCode, SignInHuaweiId hwidInfo);
}
