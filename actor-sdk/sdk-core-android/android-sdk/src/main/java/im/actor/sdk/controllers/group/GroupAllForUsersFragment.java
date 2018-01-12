package im.actor.sdk.controllers.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import im.actor.core.AndroidMessenger;
import im.actor.core.entity.Avatar;
import im.actor.core.entity.Dialog;
import im.actor.core.entity.Group;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PeerSearchEntity;
import im.actor.core.entity.PeerSearchType;
import im.actor.core.entity.PeerType;
import im.actor.core.entity.SearchEntity;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;

import static im.actor.sdk.util.ActorSDKMessenger.groups;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.users;

public class GroupAllForUsersFragment extends BaseFragment {
    List<Group> groups;
    View emptyView;

    public static GroupAllForUsersFragment create() {
        Bundle bundle = new Bundle();
        GroupAllForUsersFragment editFragment = new GroupAllForUsersFragment();
        editFragment.setArguments(bundle);
        return editFragment;
    }


    public GroupAllForUsersFragment() {
        setRootFragment(true);
        setHomeAsUp(true);
        setShowHome(true);

        HashMap<Long, Group> groupsMap = groups().getMaps();

        groups = new ArrayList<>(groupsMap.values());

        for (Group group : groups) {
            System.out.println("iGem:name1=" + group.getTitle() + ",id=" + group.getGroupId());
        }

//        BindedDisplayList<SearchEntity> searchDisplay = messenger().buildSearchDisplayList();
//        for (int i = 0; i < searchDisplay.getSize(); i++) {
//            Peer peer = searchDisplay.getItem(i).getPeer();
//            if (peer.getPeerType() == PeerType.GROUP) {
//                System.out.println("iGem:peer=" + peer.getPeerId());
//                GroupVM groupVM = groups().get(peer.getPeerId());
//                System.out.println("iGem:peerName=" + groupVM.getName().get());
//
//            }
//        }
//        groups = new ArrayList<>();
        messenger().findPeers(PeerSearchType.GROUPS).start(new CommandCallback<List<PeerSearchEntity>>() {
            @Override
            public void onResult(List<PeerSearchEntity> res) {
                outer:
                for (PeerSearchEntity pse : res) {
                    Avatar avatar;
                    Peer peer = pse.getPeer();
                    String name;
                    if (peer.getPeerType() == PeerType.GROUP) {
                        System.out.println("iGem:name=" + peer.getPeerId());
                        GroupVM groupVM = groups().get(peer.getPeerId());
//                        name = groupVM.getName().get();
//                        avatar = groupVM.getAvatar().get();
//                        groups.add(groupVM);
                        System.out.println("iGem:name=" + groupVM.getName().get());

                    } else {
                        continue;
                    }
                }

            }


            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setTitle("群组");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_group_all, container, false);
        ListView listView = (ListView) res.findViewById(R.id.group_all_list);
        emptyView = res.findViewById(R.id.empty_group_text);
        if (groups == null || groups.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return res;
        }

        return res;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.next, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next) {
        }
        return super.onOptionsItemSelected(item);
    }


    class adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return groups.size();
        }

        @Override
        public Object getItem(int position) {
            return groups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return null;
        }
    }
}
