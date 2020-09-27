package im.actor.sdk.intents;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public class ActorIntentFragmentActivity extends ActorIntentActivity {
    Fragment fragment;

    public ActorIntentFragmentActivity(Intent intent) {
        super(intent);
    }

    public ActorIntentFragmentActivity(Intent intent, Fragment fragment) {
        super(intent);
        this.fragment = fragment;
    }

    public ActorIntentFragmentActivity() {
        super(null);
    }

    public Fragment getFragment() {
        return fragment;
    }
}
