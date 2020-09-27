package im.actor.sdk.controllers.zuzhijiagou.search;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import im.actor.core.entity.Avatar;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PeerSearchEntity;
import im.actor.core.entity.PeerType;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.UserVM;
import im.actor.sdk.controllers.Intents;

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
