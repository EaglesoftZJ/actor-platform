/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.viewmodel;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.List;

import im.actor.core.entity.Group;
import im.actor.runtime.files.FileSystemReference;

/**
 * File State View Model callback. All methods are called in Main Thread.
 */
public interface GroupAllGetCallback {

    /**
     *
     *
     * @param groupVMS
     */
    @ObjectiveCName("responseCallBack:")
    void responseCallBack(List<GroupVM> groupVMS);
}
