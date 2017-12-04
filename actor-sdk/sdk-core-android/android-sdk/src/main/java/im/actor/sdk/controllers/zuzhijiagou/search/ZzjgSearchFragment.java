package im.actor.sdk.controllers.zuzhijiagou.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ChatLinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import im.actor.core.entity.Avatar;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PeerSearchEntity;
import im.actor.core.entity.PeerType;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.UserVM;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.root.Node;
import im.actor.sdk.controllers.search.GlobalSearchBaseFragment;
import im.actor.sdk.controllers.search.GlobalSearchDelegate;
import im.actor.sdk.controllers.zuzhijiagou.ZzjgFragment;

import static im.actor.sdk.util.ActorSDKMessenger.groups;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.users;

public class ZzjgSearchFragment extends ZuZhiJiaGouSearchBaseFragment {

    RecyclerView searchList;
    private SearchView searchView;
    public MenuItem searchMenu;

    private View searchContainer;
    private TextView searchEmptyView;
    private TextView searchHintView;


    @Override
    protected void onPeerPicked(Peer peer) {
        Activity activity = getActivity();
        if (activity != null) {
            startActivity(Intents.openDialog(peer, false, activity));
        }
    }

    @Override
    public void finishActivity() {
        super.finishActivity();
        searchMenu.setVisible(false);
    }
}
