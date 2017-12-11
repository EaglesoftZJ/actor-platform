package im.actor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import im.actor.develop.R;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.controllers.auth.AuthActivity;
import im.actor.sdk.intents.WebServiceLogionUtil;
import im.actor.sdk.intents.WebServiceUtil;
import im.actor.tour.TourActivity;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class WelcomeActivity extends AppCompatActivity {
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

        spIp.edit().putString("港务集团", "http://61.175.100.14").commit();

        SharedPreferences ipLogin = getSharedPreferences("ipLogin", Context.MODE_PRIVATE);


        ipLogin.edit().putString("url", "http://61.175.100.14")
                .commit();

        ipLogin.edit()
                .putString("urlForCompany", "港务集团")
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
