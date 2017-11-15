package im.actor.core.js.entity;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by zhangshanbo on 2017/11/6.
 */
public class JSElectronNotifications extends JavaScriptObject {
    public static native JSElectronNotifications create(String sender, boolean isChannel, String text, int relatedUser, String contentType, String senderName)/*-{
        return { sender: sender, isChannel: isChannel, text: text, relateduser: relatedUser, contentType: contentType, senderName: senderName };
    }-*/;

    protected JSElectronNotifications(){

    }


    public final native void close()/*-{
        this.close();
    }-*/;
}
