package im.actor.core.js.entity;

/**
 * Created by zhangshanbo on 2017/10/31.
 */

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.j2objc.annotations.Property;
import im.actor.core.entity.GroupMember;
import im.actor.core.entity.GroupMembersSlice;
import im.actor.core.entity.Peer;
import im.actor.core.js.JsMessenger;
import im.actor.core.js.modules.JsBindedValue;

import java.util.ArrayList;

public class JsGroupMembersSlice extends JavaScriptObject {
    public static native JsGroupMembersSlice create(JsArray<JsGroupMember> members, JsArrayInteger next )/*-{
        return {members: members, next: next};
    }-*/;

    protected JsGroupMembersSlice() {

    }

    public static JsGroupMembersSlice fromGroupMembersSlice(GroupMembersSlice groupMembersSlice, JsMessenger messenger){
        JsArray<JsGroupMember> res = JsArray.createArray().cast();
        for (GroupMember u : groupMembersSlice.getMembers()) {
            JsGroupMember m = JsGroupMember.create(messenger.buildPeerInfo(Peer.user(u.getUid())), u.isAdministrator(), u.getInviterUid() == messenger.myUid());
            res.push(m);
        }

        JsArrayInteger nextRes = (JsArrayInteger) JsArrayInteger.createArray();

        for (byte u : groupMembersSlice.getNext()) {


            nextRes.push(u);
        }

        return JsGroupMembersSlice.create(res, nextRes);
    }

    public final native JsArray<JsGroupMember> getMembers()/*-{
        return this.members;
    }-*/;

    public final native JsArrayInteger getNext()/*-{
        return this.next;
    }-*/;


}