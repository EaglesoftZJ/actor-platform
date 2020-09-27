package im.actor.sdk.controllers.zuzhijiagou;

import android.os.Bundle;
import android.view.KeyEvent;

import im.actor.sdk.controllers.activity.BaseFragmentActivity;

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
