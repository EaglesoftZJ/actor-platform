package im.actor.core.js.entity;

import com.google.gwt.core.client.JavaScriptObject;
import im.actor.core.viewmodel.UserPresence;

/**
 * Created by zhangshanbo on 2017/11/9.
 */

public class JsUserPresence extends JavaScriptObject {

    public native static JsUserPresence create(String state, double lastSeen)/*-{
        return {state: state, lastSeen: lastSeen};
    }-*/;

    protected JsUserPresence() {

    }

    public final native String getState()/*-{
        return this.state;
    }-*/;

    public final native double getLastSeen()/*-{
        return this.lastSeen;
    }-*/;

}