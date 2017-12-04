package im.actor.sdk.controllers.root;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import im.actor.core.AuthState;
import im.actor.core.viewmodel.AppStateVM;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.runtime.android.AndroidContext;
import im.actor.runtime.promise.Promise;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;
import im.actor.sdk.controllers.compose.ComposeFabFragment;
import im.actor.sdk.controllers.contacts.ContactsFragment;
import im.actor.sdk.controllers.dialogs.DialogsDefaultFragment;
import im.actor.sdk.controllers.placeholder.GlobalPlaceholderFragment;
import im.actor.sdk.controllers.search.GlobalSearchDefaultFragment;
import im.actor.sdk.controllers.tools.InviteHandler;
import im.actor.sdk.intents.WebServiceUtil;
import im.actor.sdk.view.PagerSlidingTabStrip;
import im.actor.sdk.view.adapters.FragmentNoMenuStatePagerAdapter;

/**
 * Root Activity of Application
 */
public class RootActivity extends BaseFragmentActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private RootPageFragment rootPageFragment;

    //    ViewPager pager;
//    private HomePagerAdapter homePagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }


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
        execute(new Command<String>() {
            @Override
            public void start(CommandCallback<String> callback) {
                zjjgData(callback);
            }
        });
    }

    private void zjjgData(final CommandCallback<String> callback) {
        if (ActorSDK.getZjjgData() == null) {
            WebServiceUtil.webServiceRun(ActorSDK.webServiceUri, new HashMap<String, String>(),
                    "GetAllUserFullData", AndroidContext.getContext(), new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            Bundle b = msg.getData();
                            String datasource = b.getString("datasource");
                            try {
                                JSONObject json = new JSONObject(datasource);
                                ActorSDK.setZjjgData(json);
//                                zzjgFragment.ZzjgData(json);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    AppStateVM appStateVM = ActorSDK.sharedActor().getMessenger().getAppStateVM();
                    if (appStateVM.isDialogsLoaded() && appStateVM.isContactsLoaded() && appStateVM.isSettingsLoaded()) {
                        ActorSDK.sharedActor().getMessenger().startImport();
                    }

                }
            }

        }
    }



}
