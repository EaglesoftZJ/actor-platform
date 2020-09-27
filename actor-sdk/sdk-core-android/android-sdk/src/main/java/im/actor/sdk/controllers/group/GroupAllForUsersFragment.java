package im.actor.sdk.controllers.group;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.GroupAllGetCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.MessageXzrz;
import im.actor.core.viewmodel.MessageXzrzCallBack;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.group.view.GroupEaglesoftAdapter;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.myUid;

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
        messenger().getGroupAll(ActorSDK.getWebServiceUri(getContext()) + ":8012/ActorServices-Maven/services/ActorService", myUid(), new GroupAllGetCallback() {
            @Override
            public void responseCallBack(List<GroupVM> groupVMS) {
                groups.clear();
                groups.addAll(groupVMS);
                callBackHandler.sendEmptyMessage(0);
            }
        });
//        JSONObject json = new JSONObject();
//        try {
//            json.put("messageId", -7275888453393723629L);
//            json.put("userId", 2092017244);
//            json.put("userName", "来啊");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        messenger().saveXzrz("http://192.168.1.182:9080/services/ActorService", json.toString(), new MessageXzrzCallBack() {
//            @Override
//            public void saveResponseCallBack(String str) {
//                super.saveResponseCallBack(str);
//            }
//        });
//
//        messenger().getXzrz(ActorSDK.getWebServiceUri(getContext()) + ":8012/ActorServices-Maven/services/ActorService", -12, new MessageXzrzCallBack() {
//
//            @Override
//            public void queryResponseCallBack(List<MessageXzrz> xzrzs) {
//                for (MessageXzrz xzrz : xzrzs) {
//                    System.out.println("iGem:" + xzrz.getUserName());
//                }
//            }
//        });
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
