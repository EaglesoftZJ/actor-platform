package com.huawei.android.hms.agent.hwid.handler;

import com.huawei.hms.support.api.hwid.SignOutResult;

/**
 * 帐号登出回调
 */
public interface SignOutHandler {
    /**
     * 登出结果回调
     * @param rtnCode 结果码
     * @param signOutResult 登出结果
     */
    void onResult(int rtnCode, SignOutResult signOutResult);
}
