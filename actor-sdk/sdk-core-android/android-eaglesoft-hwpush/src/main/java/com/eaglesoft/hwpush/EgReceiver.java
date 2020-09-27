package com.eaglesoft.hwpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class EgReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.huawei.codelabpush.ON_NEW_TOKEN".equals(intent.getAction())) {
            String token = intent.getStringExtra("token");

        }
    }
}
