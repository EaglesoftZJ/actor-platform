package im.actor.sdk.controllers.root;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import im.actor.core.AuthState;
import im.actor.core.entity.SearchEntity;
import im.actor.core.viewmodel.AppStateVM;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.runtime.android.AndroidContext;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.promise.Promise;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseActivity;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;
import im.actor.sdk.controllers.compose.ComposeEaglesoftFragment;
import im.actor.sdk.controllers.tools.InviteHandler;
import im.actor.sdk.intents.WebServiceLogionUtil;
import im.actor.sdk.intents.WebServiceUtil;
import im.actor.sdk.permisson_interface.OnPermissionListener;
import im.actor.sdk.push.Utils;
import im.actor.sdk.services.UpdataService;
import im.actor.sdk.view.PagerSlidingTabStrip;
import im.actor.sdk.view.adapters.FragmentNoMenuStatePagerAdapter;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Root Activity of Application
 */
public class RootActivity extends BaseFragmentActivity {

    private RootPageFragment rootPageFragment;
    private SharedPreferences sp;


    static HuaweiApiClient client;
    // user your appid the key.
    private static final String APP_ID = "2882303761517562000";
    // user your appid the key.
    private static final String APP_KEY = "5731756231000";

    //    ViewPager pager;
//    private HomePagerAdapter homePagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        sp = this.getSharedPreferences("flyChatSp", MODE_PRIVATE);

        int phoneFlag = Utils.isWhatPhone();
        if (phoneFlag == 0) {
//百度推送
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                    Utils.getMetaValue(this, "api_key"));
        } else if (phoneFlag == 1) {
// 华为推送
            HuaweiIdSignInOptions options = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                    .build();
            huaWeiCallBack callBack = new huaWeiCallBack(this);
            client = new HuaweiApiClient.Builder(this) //
                    .addApi(HuaweiId.SIGN_IN_API, options)//
                    .addConnectionCallbacks(callBack) //
                    .addOnConnectionFailedListener(callBack) //
                    .build();
            client.connect();
        } else if (phoneFlag == 2) {
            //小米推送
//            注意：因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化。
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_CONTACTS},
//                    PERMISSIONS_REQUEST_READ_CONTACTS);
        String[] PERMISSIONS_CONTACT = {
                Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE};

        requestPermission(PERMISSIONS_CONTACT, new OnPermissionListener() {
            @Override
            public void permissionGranted() {
                AppStateVM appStateVM = ActorSDK.sharedActor().getMessenger().getAppStateVM();
                if (appStateVM.isDialogsLoaded() && appStateVM.isContactsLoaded() && appStateVM.isSettingsLoaded()) {
                    ActorSDK.sharedActor().getMessenger().startImport();
                }
            }
        });
//        }
        execute(new Command<String>() {
            @Override
            public void start(CommandCallback<String> callback) {
                zjjgData(callback);
            }
        });

        //
        // Configure Toolbar
        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        if (ActorSDK.sharedActor().style.getToolBarColor() != 0) {
            toolbar.setBackgroundDrawable(new ColorDrawable(ActorSDK.sharedActor().style.getToolBarColor()));
        }

        if (savedInstanceState == null) {
            Fragment fragment = ActorSDK.sharedActor().getDelegate().fragmentForRoot();
//            if (fragment == null) {
//                fragment = new RootFragment();
//            }
            rootPageFragment = new RootPageFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.root, rootPageFragment)
                    .commit();
        }

        InviteHandler.handleIntent(this, getIntent());

        HashMap<String, Object> par = new HashMap<>();
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // e.printStackTrace(System.err);
            // L.i("getPackageInfo err = " + e.getMessage());
        }
        if (info == null)
            info = new PackageInfo();
        par.put("version", info.versionName);
//        System.out.println("iGem:"+info.versionName);
//        http://61.175.100.14:8012
        WebServiceLogionUtil.webServiceRun(ActorSDK.getWebServiceUri(getApplicationContext()) + ":8012", par, "updatePhotoFlyChat", getApplicationContext(),
                new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        Bundle b = message.getData();
                        String datasource = b.getString("datasource");
                        try {
                            JSONObject json = new JSONObject(datasource);
//                            System.out.println("iGem:"+json.toString());
                            if (json.getBoolean("canUpdate")) {
                                final String url = json.getString("url");
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RootActivity.this);
                                AlertDialog alertDialog = alertDialogBuilder.setPositiveButton("更新",// 设置确定按钮
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                dialog.dismiss();
                                                String[] PERMISSIONS_CONTACT = {
                                                        Manifest.permission.READ_EXTERNAL_STORAGE};

                                                requestPermission(PERMISSIONS_CONTACT, new OnPermissionListener() {
                                                    @Override
                                                    public void permissionGranted() {
                                                        Intent intent = new Intent(RootActivity.this,
                                                                UpdataService.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("url", url);
                                                        intent.putExtras(bundle);
                                                        startService(intent);
                                                    }
                                                });
                                            }
                                        }
                                ).setNegativeButton("暂不更新",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                // 点击"取消"按钮之后退出程序
                                                dialog.dismiss();
                                            }
                                        }

                                ).create();
//                                alertDialog.setTitle("软件更新");
                                alertDialog.setMessage("发现新版本，是否在该网络环境下更新？");
                                alertDialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }));


        HashMap<String, Object> parImage = new HashMap<>();
        parImage.put("version", sp.getString("imageVersion", "0"));
        WebServiceLogionUtil.webServiceRun(ActorSDK.getWebServiceUri(getApplicationContext()) + ":8012", parImage, "phone_image", getApplicationContext(),
                new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        Bundle b = message.getData();
                        String datasource = b.getString("datasource");
                        try {
                            JSONObject json = new JSONObject(datasource);
//                            System.out.println("iGem:" + json.toString());
                            sp.edit().putString("imageVersion", json.getString("version"));

                            final String welcomePage_bg = json.getString("welcomePage_bg");
                            final String login_logo = json.getString("login_logo");
                            final String loginPage_but = json.getString("loginPage_but");
                            final String loginPage_bg_bottom_small = json.getString("loginPage_bg_bottom_small");
                            final String loginPage_bg_bottom = json.getString("loginPage_bg_bottom");
                            final String loginPage_bg_top = json.getString("loginPage_bg_top");
                            if (json.getBoolean("canUpdate")) {
                                new Thread() {
                                    public void run() {
                                        sp.edit().putString("welcomePage_bg",
                                                getImageURI(welcomePage_bg, "welcomePage_bg.png")).commit();
                                        sp.edit().putString("login_logo",
                                                getImageURI(login_logo, "login_logo.png")).commit();
                                        sp.edit().putString("loginPage_but",
                                                getImageURI(loginPage_but, "loginPage_but.png")).commit();

                                        sp.edit().putString("loginPage_bg_bottom_small",
                                                getImageURI(loginPage_bg_bottom_small, "loginPage_bg_bottom_small.png")).commit();
                                        sp.edit().putString("loginPage_bg_bottom",
                                                getImageURI(loginPage_bg_bottom, "loginPage_bg_bottom.png")).commit();
                                        sp.edit().putString("loginPage_bg_top",
                                                getImageURI(loginPage_bg_top, "loginPage_bg_top.png")).commit();
                                    }
                                }.start();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }));

    }

    private void zjjgData(final CommandCallback<String> callback) {
        if (ActorSDK.getZjjgData() == null) {
            WebServiceUtil.webServiceRun(ActorSDK.getWebServiceUri(getApplicationContext()) + ":8004", new HashMap<String, String>(),
                    "GetAllUserFullData", AndroidContext.getContext(), new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            Bundle b = msg.getData();
                            String datasource = b.getString("datasource");
                            try {
                                JSONObject json = new JSONObject(datasource);
                                ActorSDK.setZjjgData(json);
                                ComposeEaglesoftFragment fragment = (ComposeEaglesoftFragment) rootPageFragment.getHomePagerAdapter().getContactsFragment();
                                fragment.changeAdapter();
                                callback.onResult("");
                            } catch (Exception e) {
                                e.printStackTrace();
                                callback.onError(e);
                            }
                            return false;
                        }
                    }));
        } else {
            callback.onResult("");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        InviteHandler.handleIntent(this, intent);
    }


    //6.0之后权限返回方法

    public static final int PERMISSIONS_REQUEST = 0;
    private OnPermissionListener activitylistener;

    public void requestPermission(String[] permission, OnPermissionListener listener) {
        boolean flag = true;
        activitylistener = listener;
        for (int i = 0; i < permission.length; i++) {
            if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, permission[i])
                    != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            requestContactsPermissions(permission);
        } else {
            listener.permissionGranted();
        }

    }

    private boolean requestShowRequestPermission(String[] permission) {
        boolean flag = false;
        for (int i = 0; i < permission.length; i++) {
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this, permission[i])) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void requestContactsPermissions(String[] permission) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (requestShowRequestPermission(permission)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            // Display a SnackBar with an explanation and a button to trigger the request.
            android.support.v4.app.ActivityCompat.requestPermissions(this, permission,
                    PERMISSIONS_REQUEST);

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            android.support.v4.app.ActivityCompat.requestPermissions(this, permission, PERMISSIONS_REQUEST);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            boolean flag = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    flag = false;
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(this, "因位置权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else if (permissions[i].equals(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "因相机权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(this, "因存储权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else if (permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
                        Toast.makeText(this, "因使用电话权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        Toast.makeText(this, "因部分权限未开启，有功能尚无法使用，请去设置中开启", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            if (flag) {
                activitylistener.permissionGranted();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }


    public String getImageURI(String path, String fileName) {
        File cache = new File(Environment.getExternalStorageDirectory(), "iGem/cache");

        if (!cache.exists()) {
            cache.mkdirs();
        }
        File file = new File(cache, fileName);

        // 如果图片存在本地缓存目录，则不去服务器下载
//        if (file.exists()) {
//            return file.getAbsolutePath();//Uri.fromFile(path)这个方法能得到文件的URI
//        } else {
        // 从网络上获取图片
        URL url = null;
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return null;
    }


    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static boolean mResolvingError = false;

    private static class huaWeiCallBack implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener,
            HuaweiApiAvailability.OnUpdateListener {
        Context context;

        public huaWeiCallBack(Context context) {
            this.context = context;
        }

        @Override
        public void onUpdateFailed(@NonNull ConnectionResult connectionResult) {
            Log.i("PushMoa", "更新失败");
        }

        @Override
        public void onConnected() {
            getToken(client);
//            Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
            Log.i("PushMoa", "连接成功");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i("PushMoa", "连接暂停");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult result) {
//            Toast.makeText(context, "连接失败" + result.getErrorCode(), Toast.LENGTH_SHORT).show();

            Log.i("PushMoa", "onConnectionFailed, ErrorCode: " + result.getErrorCode());

            if (mResolvingError) {
                return;
            }

            int errorCode = result.getErrorCode();
            HuaweiApiAvailability availability = HuaweiApiAvailability.getInstance();

            if (availability.isUserResolvableError(errorCode)) {
                mResolvingError = true;
                availability.resolveError((Activity) context, errorCode, REQUEST_RESOLVE_ERROR, this);
            }
        }
    }

    private static void getToken(final HuaweiApiClient client) {
        if (client == null || !client.isConnected()) {
//            Toast.makeText(context, "连接失败，不请求token", Toast.LENGTH_SHORT).show();
            return;
        }
        // 异步调用方式
        new Thread() {
            @Override
            public void run() {
                super.run();
                PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
                tokenResult.await();
            }
        }.start();
    }

}
