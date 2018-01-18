package im.actor.sdk.controllers.group;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

import im.actor.core.api.ApiGroup;
import im.actor.core.api.ApiGroupType;
import im.actor.core.entity.Avatar;
import im.actor.core.entity.Group;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PeerSearchEntity;
import im.actor.core.entity.PeerType;
import im.actor.core.entity.SearchEntity;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.GroupAllGetCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.json.JSONArray;
import im.actor.runtime.json.JSONObject;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.group.view.GroupEaglesoftAdapter;
import im.actor.sdk.controllers.search.GlobalSearchBaseFragment;
import im.actor.sdk.intents.WebServiceLogionUtil;
import im.actor.sdk.intents.WebServiceUtil;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.groups;
import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.myUid;
import static im.actor.sdk.util.ActorSDKMessenger.users;

public class GroupAllForUsersFragment extends BaseFragment {
    List<GroupVM> groups;
    View emptyView;
    RecyclerView groupRec;
    private RecyclerView.Adapter adapter;

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

        groups = new ArrayList<>();
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
        groupRec = (RecyclerView) res.findViewById(R.id.group_all_rec);
        emptyView = res.findViewById(R.id.empty_group_text);
        setAnimationsEnabled(true);
        configureRecyclerView(groupRec);

        adapter = new GroupEaglesoftAdapter(groups, getActivity(), false, new OnItemClickedListener<GroupVM>() {
            @Override
            public void onClicked(GroupVM item) {
                getActivity().startActivity(Intents.openGroupDialog(item.getId(), true, getActivity()));
            }

            @Override
            public boolean onLongClicked(GroupVM item) {
                return false;
            }
        });
        groupRec.setAdapter(adapter);

//        HashMap<Long, Group> groupsMap = groups().getMaps();
//
//        List<Group> groups = new ArrayList<>(groupsMap.values());
//
//        for (Group group : groups) {
//            System.out.println("iGem:name1=" + group.getTitle() + ",id=" + group.getGroupId());
//        }
        emptyView.setVisibility(View.VISIBLE);
        groupRec.setVisibility(View.GONE);
        setAnimationsEnabled(false);
        execute(new Command<String>() {
            @Override
            public void start(CommandCallback<String> callback) {
                getGroup(callback);
            }
        });


        return res;
    }


    public void setAnimationsEnabled(boolean isEnabled) {
        if (isEnabled) {
            DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
            // CustomItemAnimator itemAnimator = new CustomItemAnimator();
            itemAnimator.setSupportsChangeAnimations(false);
            itemAnimator.setMoveDuration(200);
            itemAnimator.setAddDuration(150);
            itemAnimator.setRemoveDuration(200);
            groupRec.setItemAnimator(itemAnimator);
        } else {
            groupRec.setItemAnimator(null);
        }
    }

    protected void configureRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setRecycleChildrenOnDetach(false);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.setVerticalScrollBarEnabled(true);
    }
    Handler callBackHandler;
    private void getGroup(final CommandCallback<String> callback) {
        HashMap<String, Object> par = new HashMap<>();
        par.put("id", myUid() + "");
        callBackHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                emptyView.setVisibility(View.GONE);
                groupRec.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                callback.onResult("");
                return false;
            }
        });
        messenger().getGroupAll(ActorSDK.getWebServiceUri(getContext()) + ":8012/ActorServices-Maven/services/ActorService",myUid(), new GroupAllGetCallback() {
            @Override
            public void responseCallBack(List<GroupVM> groupVMS) {
                groups.clear();
                groups.addAll(groupVMS);
                callBackHandler.sendEmptyMessage(0);
            }
        });
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


}
