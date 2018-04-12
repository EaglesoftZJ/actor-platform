/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.viewmodel;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

/**
 * File State View Model callback. All methods are called in Main Thread.
 */
public interface WebServiceRunCallBack {

    /**
     *
     * @param str 请求webService之后的返回值
     */
    @ObjectiveCName("webSrviceResCallBack:")
    void webSrviceResCallBack(String str);
}
