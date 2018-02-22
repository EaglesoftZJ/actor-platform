package im.actor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.LinearLayout;

import im.actor.develop.R;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.controllers.auth.AuthActivity;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class WelcomeActivity extends Activity {
    private long SPLASH_LENGTH = 1000;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_welcome);

        LinearLayout lay = (LinearLayout) findViewById(R.id.welcome_lay);
        SharedPreferences sp = this.getSharedPreferences("flyChatSp", MODE_PRIVATE);
        String welcomePage_bg = sp.getString("welcomePage_bg", null);

        if (welcomePage_bg != null) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(welcomePage_bg);
            if (imageBitmap != null) {
                lay.setBackgroundDrawable(new BitmapDrawable(imageBitmap));/**/
            }
        }
        SharedPreferences spIp = getSharedPreferences("ipList", Context.MODE_PRIVATE);

        spIp.edit().putString(getString(R.string.urlForCompany), getString(R.string.url)).commit();

        SharedPreferences ipLogin = getSharedPreferences("ipLogin", Context.MODE_PRIVATE);


        ipLogin.edit().putString("url", getString(R.string.url))
                .commit();

        ipLogin.edit()
                .putString("urlForCompany", getString(R.string.urlForCompany))
                .commit();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                ActorSDK.sharedActor().waitForReady();
//
//                if (!messenger().isLoggedIn()) {
//                    Bundle authExtras = new Bundle();
//                    authExtras.putInt(AuthActivity.SIGN_TYPE_KEY, AuthActivity.SIGN_TYPE_UP);
//                    ActorSDK.sharedActor().startAuthActivity(WelcomeActivity.this, authExtras);
//                    finish();
////                    startActivity(new Intent(WelcomeActivity.this, TourActivity.class));
////                    finish();
//                    return;
//                }
//
//                ActorSDK.sharedActor().startMessagingApp(WelcomeActivity.this);
//                finish();


                ActorSDK.sharedActor().waitForReady();

//                if (!messenger().isLoggedIn()) {
//                    startActivity(new Intent(WelcomeActivity.this, TourActivity.class));
//                    finish();
//                    return;
//                }
                if (!messenger().isLoggedIn()) {
                    Bundle authExtras = new Bundle();
                    authExtras.putInt(AuthActivity.SIGN_TYPE_KEY, AuthActivity.SIGN_TYPE_UP);
                    ActorSDK.sharedActor().startAuthActivity(WelcomeActivity.this, authExtras);
                    finish();
                    return;
                }

                ActorSDK.sharedActor().startMessagingApp(WelcomeActivity.this);
                finish();
            }
        }, SPLASH_LENGTH);// 1秒后跳转

    }


}
