package im.actor.fragments;



import im.actor.develop.R;
import im.actor.sdk.controllers.root.RootFragment;
import androidx.appcompat.app.ActionBar;

public class RootFragmentEx extends RootFragment {
    @Override
    public void onConfigureActionBar(ActionBar actionBar) {
        super.onConfigureActionBar(actionBar);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_app_notify);
    }
}
