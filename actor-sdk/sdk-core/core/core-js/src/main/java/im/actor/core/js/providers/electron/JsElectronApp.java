package im.actor.core.js.providers.electron;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import im.actor.core.js.entity.JSElectronNotifications;
import im.actor.runtime.json.JSONObject;

public class JsElectronApp {

    public static native boolean isElectron()/*-{
        return 'require' in $wnd;
    }-*/;

    public static native void bounce()/*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.send('tray-bounce');
    }-*/;

    public static native void hideNewMessages()/*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.send('new-messages-hide');
    }-*/;

    public static native void updateBadge(int count)/*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.send('tray-badge', { count: count });
    }-*/;

    public static native void notification(String key, String title, String message, String avatarUrl, JsArray<JSElectronNotifications> array)/*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.send('new-messages-notification', {key: key, title: title, message: message, avatar: avatarUrl,notifications: array});
    }-*/;


    public static native void subscribe(String topic, JsElectronListener listener)/*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.on(topic, function(event, message) {
            listener.@im.actor.core.js.providers.electron.JsElectronListener::onEvent(*)(message);
        });
    }-*/;

    public static native void sendToElectron(String topic, JSONObject args)/*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.send(topic, args);
    }-*/;

    public static native void openUrlExternal(String url)/*-{
        var shell = $wnd.require('electron').shell;
        shell.openExternal(url);
    }-*/;

    public static native void listenOnRender(String topic, JavaScriptObject func) /*-{
        var ipc = $wnd.require("electron").ipcRenderer;
        ipc.on(topic, func);
    }-*/;{
    }
}