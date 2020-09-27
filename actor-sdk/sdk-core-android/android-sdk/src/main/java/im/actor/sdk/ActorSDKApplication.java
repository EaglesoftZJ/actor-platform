package im.actor.sdk;

import android.app.ActivityManager;
import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

import im.actor.runtime.android.AndroidContext;

/**
 * Implementation of Application object that handles everything required for creating and
 * managing Actor SDK
 */
public class ActorSDKApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        int id = android.os.Process.myPid();
        String myProcessName = getPackageName();



        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo procInfo : activityManager.getRunningAppProcesses()) {
            if (id == procInfo.pid) {
                myProcessName = procInfo.processName;
            }
        }

        // Protection on double start
        if (!myProcessName.endsWith(":actor_push")) {
            AndroidContext.setContext(this);
            onConfigureActorSDK();
            ActorSDK.sharedActor().createActor(this);
        }

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    /**
     * Override this method for implementing Actor SDK Implementation
     */
    public void onConfigureActorSDK() {

    }
}