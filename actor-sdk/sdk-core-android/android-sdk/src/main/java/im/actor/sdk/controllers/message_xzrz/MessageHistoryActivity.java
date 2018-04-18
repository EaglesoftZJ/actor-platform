package im.actor.sdk.controllers.message_xzrz;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import im.actor.sdk.controllers.activity.BaseFragmentActivity;


public class MessageHistoryActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = new Bundle();
        args.putLong("messageid", getIntent().getLongExtra("messageid", 0));
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(args);
        showFragment(messageFragment, false);
    }
}
