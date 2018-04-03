package im.actor.sdk.controllers.compose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.EditText;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.activity.BaseFragmentActivity;
import im.actor.sdk.util.KeyboardHelper;

public class SimpleCreateGroupActivity extends BaseFragmentActivity {

    public static String EXTRA_User_Name = "User_Name";
    public static String EXTRA_User_ID = "User_ID";
    private EditText nameFiled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_create_group);

        String name = getIntent().getStringExtra(EXTRA_User_Name);
        int id = getIntent().getIntExtra(EXTRA_User_ID, 0);

        nameFiled = (EditText) findViewById(R.id.group_name_edit);
        nameFiled.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryColor());
        nameFiled.setHintTextColor(ActorSDK.sharedActor().style.getTextHintColor());

        KeyboardHelper helper = new KeyboardHelper(SimpleCreateGroupActivity.this);
        helper.setImeVisibility(nameFiled, false);

        String myName = ActorSDK.sharedActor().getMessenger().getUser(ActorSDK.sharedActor().getMessenger().myUid()).getName().get();
        String title = name + "," + myName;
        nameFiled.setText(title);
        nameFiled.setSelection(title.length());
        if (savedInstanceState == null) {
            Fragment fragment = GroupUsersFragment.createGroup(title, null, id);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.create_group_fram, fragment)
                    .commit();

        }

    }


    public String getGroupTitle() {
        return nameFiled.getText().toString().trim();
    }
}
