/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.core.js.entity;

public class JsContentJson extends JsContent {
    public native static JsContentJson create(String operation,String content)/*-{
        return {content: "customJson", operation: operation, text: content};
    }-*/;

    protected JsContentJson() {

    }
}
