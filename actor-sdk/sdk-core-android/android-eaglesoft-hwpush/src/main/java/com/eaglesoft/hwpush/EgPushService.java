
package com.eaglesoft.hwpush;

import android.content.Intent;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import im.actor.sdk.ActorSDK;

public class EgPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "receive token:" + token);
        sendTokenToDisplay(token);
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {
            Log.i("PushMoa", "收到一条Push消息");
//            Toast.makeText(context.getApplicationContext(), "收到一条Push消息：", Toast.LENGTH_LONG).show();
            String message = remoteMessage.getData();

            try {
                im.actor.runtime.json.JSONObject object = new im.actor.runtime.json.JSONObject(message);
                if (object.has("data")) {
                    im.actor.runtime.json.JSONObject data = object.getJSONObject("data");
                    ActorSDK.sharedActor().waitForReady();
                    if (data.has("seq")) {
                        int seq = data.getInt("seq");
                        int authId = data.optInt("authId");
                        ActorSDK.sharedActor().getMessenger().onPushReceived(seq, authId);
                    } else if (data.has("callId")) {
                        Long callId = Long.parseLong(data.getString("callId"));
                        int attempt = 0;
                        if (data.has("attemptIndex")) {
                            attempt = data.getInt("attemptIndex");
                        }
                        im.actor.runtime.Log.d("SDKPushReceiver", "Received Call #" + callId + " (" + attempt + ")");
                        ActorSDK.sharedActor().getMessenger().checkCall(callId, attempt);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageSent(String s) {
    }

    @Override
    public void onSendError(String s, Exception e) {
    }

    private void sendTokenToDisplay(String token) {
        Intent intent = new Intent("com.huawei.codelabpush.ON_NEW_TOKEN");
        intent.putExtra("token", token);
        sendBroadcast(intent);
    }
}
