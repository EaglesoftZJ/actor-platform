package com.huawei.android.hms.agent.game.handler;

import com.huawei.hms.support.api.entity.game.GameUserData;

/**
 * 游戏登录结果回调接口
 */
public interface LoginHandler {
    /**
     * 游戏登录结果回调
     *
     * @param retCode 游戏结果码
     * @param userData 登录的用户数据
     */
    void onResult(int retCode, GameUserData userData);

    /**
     * 登录帐号发生变化时回调
     */
    void onChange();
}
