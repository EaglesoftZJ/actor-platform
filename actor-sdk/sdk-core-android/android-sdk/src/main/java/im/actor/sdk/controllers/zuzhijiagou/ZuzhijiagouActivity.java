package im.actor.sdk.controllers.zuzhijiagou;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;

import org.json.JSONObject;

import java.util.HashMap;

import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.runtime.android.AndroidContext;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;
import im.actor.sdk.controllers.compose.ComposeFragment;
import im.actor.sdk.controllers.root.RootPageFragment;
import im.actor.sdk.controllers.root.RootZzjgFragment;
import im.actor.sdk.intents.WebServiceUtil;

public class ZuzhijiagouActivity extends BaseFragmentActivity {
    ZzjgAllFragment zzjgFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_zuzhijiagou);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.zuzhijiegou_fram, new ZzjgFragment())
//                .commit();

        zzjgFragment = new ZzjgAllFragment();

        showFragment(zzjgFragment, false);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}
