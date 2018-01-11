package im.actor.sdk.controllers.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;

public class GroupAllActivity extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Fragment fragment = GroupAllForUsersFragment.create();
            showFragment(fragment, false);
        }
    }
}
