package im.actor.sdk.controllers.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import im.actor.core.entity.Peer;
import im.actor.runtime.actors.Actor;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseActivity;
import im.actor.sdk.controllers.conversation.mentions.AutocompleteCallback;
import im.actor.sdk.controllers.conversation.mentions.AutomemberFragment;

public class GroupMemberActivity extends BaseActivity implements AutocompleteCallback {
    Peer peer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);

        long peerId = this.getIntent().getLongExtra("peer", 0);
        peer = Peer.fromUniqueId(peerId);

        AutomemberFragment fragment = AutomemberFragment.create(peer);

        getSupportFragmentManager().beginTransaction().
                add(R.id.members_fra, fragment).commit();

//        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
//                hideSoftInputFromWindow(GroupMemberActivity.this.getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS);


    }

    private void returnBack() {
        Intent intent = new Intent();
        intent.putExtra("resultAt", "notAt");
//        finishActivity(intent);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            returnBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMentionPicked(String name) {
        Intent intent = new Intent();
        intent.putExtra("resultAt", "at");
        intent.putExtra("mention", name);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCommandPicked(String command) {
        Intent intent = new Intent();
        intent.putExtra("resultAt", "at");
        intent.putExtra("command", command);
        setResult(RESULT_OK, intent);
        finish();
    }
}
