package com.huawei.android.hms.agent.pay.handler;

import com.huawei.hms.support.api.pay.OrderResult;

/**
 * 查询订单结果回调
 */
public interface GetOrderHandler {
    /**
     * 检查支付结果回调
     * @param retCode 结果码
     * @param checkPayResult 支付结果信息
     */
    void onResult(int retCode, OrderResult checkPayResult);
}
