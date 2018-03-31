package com.huawei.android.hms.agent.pay.handler;


import com.huawei.hms.support.api.pay.PayResultInfo;

/**
 * 支付结果回调
 */
public interface PayHandler {
    /**
     * 支付结果回调
     * @param retCode 结果码
     * @param payInfo 支付结果信息
     */
    void onResult(int retCode, PayResultInfo payInfo);
}
