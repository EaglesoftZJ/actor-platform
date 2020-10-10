package im.actor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import im.actor.develop.R;
import im.actor.runtime.Runtime;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.controllers.auth.AuthActivity;

import android.Manifest;
import android.widget.Toast;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class WelcomeActivity extends Activity {
    private long SPLASH_LENGTH = 1000;
    Handler handler = new Handler();


    protected static final int PERMISSIONS_REQUEST_FOR_CONTACTS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_welcome);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
//                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                this.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
//                        PERMISSIONS_REQUEST_FOR_CONTACTS);
//            }
//        }

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


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_FOR_CONTACTS) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Catch all phone book changes
//                Runtime.dispatch(() ->
//                        messenger().getContext().getContentResolver()
//                                .registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
//                                        new ContentObserver(null) {
//                                            @Override
//                                            public void onChange(boolean selfChange) {
//                                                messenger().onPhoneBookChanged();
//                                            }
//                                        }));
//            } else {
//                Toast toast = Toast.makeText(messenger().getContext(), "请前往手机系统设置，允许本应用读取和写入通讯录数据", Toast.LENGTH_LONG);
//                toast.show();
//            }
//        }
//    }

}
