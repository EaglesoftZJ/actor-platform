package im.actor.sdk.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CustomItemAnimator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import im.actor.core.entity.Contact;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.DisplayList;
import im.actor.runtime.json.JSONArray;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.view.adapters.HeaderViewRecyclerAdapter;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.storage.ListEngineItem;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public abstract class DisplayListFragment<T extends BserObject & ListEngineItem,
        V extends RecyclerView.ViewHolder> extends BaseFragment implements DisplayList.Listener {

    private RecyclerView collection;
    // private View emptyCollection;

    public BindedDisplayList<T> displayList;
    private BindedListAdapter<T, V> adapter;

    protected View inflate(LayoutInflater inflater, ViewGroup container, int resource, BindedDisplayList<T> displayList) {
        View res = inflater.inflate(resource, container, false);
        afterViewInflate(res, displayList);
        return res;
    }

    protected void afterViewInflate(View view, BindedDisplayList<T> displayList) {
        collection = (RecyclerView) view.findViewById(R.id.collection);
        if (displayList.getSize() == 0) {
            collection.setVisibility(View.INVISIBLE);
        } else {
            collection.setVisibility(View.VISIBLE);
        }
        setAnimationsEnabled(true);

        this.displayList = displayList;
        configureRecyclerView(collection);

        // emptyCollection = res.findViewById(R.id.emptyCollection);
        adapter = onCreateAdapter(displayList, getActivity());

        collection.setAdapter(adapter);

//        if (emptyCollection != null) {
//            emptyCollection.setVisibility(View.GONE);
//        }
    }

    public void setAnimationsEnabled(boolean isEnabled) {
        if (isEnabled) {
            DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
            // CustomItemAnimator itemAnimator = new CustomItemAnimator();
            itemAnimator.setSupportsChangeAnimations(false);
            itemAnimator.setMoveDuration(200);
            itemAnimator.setAddDuration(150);
            itemAnimator.setRemoveDuration(200);
            collection.setItemAnimator(itemAnimator);
        } else {
            collection.setItemAnimator(null);
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

    protected void addHeaderView(View header) {
        if (collection.getAdapter() instanceof HeaderViewRecyclerAdapter) {
            HeaderViewRecyclerAdapter h = (HeaderViewRecyclerAdapter) collection.getAdapter();
            h.addHeaderView(header);
        } else {
            HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
            headerViewRecyclerAdapter.addHeaderView(header);
            collection.setAdapter(headerViewRecyclerAdapter);
        }
    }

    protected void addFooterView(View header) {
        if (collection.getAdapter() instanceof HeaderViewRecyclerAdapter) {
            HeaderViewRecyclerAdapter h = (HeaderViewRecyclerAdapter) collection.getAdapter();
            h.addFooterView(header);
        } else {
            HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
            headerViewRecyclerAdapter.addFooterView(header);
            collection.setAdapter(headerViewRecyclerAdapter);
        }
    }

    protected abstract BindedListAdapter<T, V> onCreateAdapter(BindedDisplayList<T> displayList, Activity activity);

    public BindedListAdapter<T, V> getAdapter() {
        return adapter;
    }

    public BindedDisplayList<T> getDisplayList() {
        return displayList;
    }

    public RecyclerView getCollection() {
        return collection;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.resume();
        displayList.addListener(this);
        if (displayList.getSize() == 0) {
            hideView(collection, false);
        } else {
            showView(collection, false);
        }
    }

    @Override
    public void onCollectionChanged() {
        if (displayList.getSize() == 0) {
            hideView(collection, false);
        } else {
            ResultHandler resultHandler = new ResultHandler();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (displayList.getItem(0) instanceof Contact) {
                        for (int i = 0; i < displayList.getSize(); i++) {
                            Contact contact = (Contact) displayList.getItem(i);
                            if ("#".equalsIgnoreCase(contact.getPyShort())) {
                                try {
                                    org.json.JSONArray yh_array = ActorSDK.getZjjgData().getJSONArray("yh_data");
//                                    Toast.makeText(getActivity(),"开始修改:长度"+yh_array.length(),Toast.LENGTH_SHORT).show();
                                    for (int j = 0; j < yh_array.length(); j++) {
                                        JSONObject json = yh_array.getJSONObject(j);
                                        String igImid = json.getString("IGIMID");
                                        if (contact.getUid() == Integer.valueOf(igImid).intValue()) {
                                            String xm = json.getString("xm");
//                                            Toast.makeText(getActivity(),xm+":修改",Toast.LENGTH_SHORT).show();
                                            Message message = new Message();
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("uid",contact.getUid());
                                            bundle.putString("xm",xm);
                                            message.setData(bundle);
                                            resultHandler.sendMessage(message);
//                                            execute(messenger().editName(contact.getUid(), xm), R.string.edit_name_process, new CommandCallback<Boolean>() {
//                                                @Override
//                                                public void onResult(Boolean res) {
//                                                    Toast.makeText(getActivity(),"修改成功",Toast.LENGTH_SHORT).show();
//                                                }
//
//                                                @Override
//                                                public void onError(Exception e) {
//                                                    Toast.makeText(getActivity(), R.string.toast_unable_change, Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
                                            break;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
//                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }
                }
            }.start();
            showView(collection, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.pause();
        displayList.removeListener(this);
    }

    class ResultHandler extends Handler {
        public ResultHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            execute(messenger().editName(msg.getData().getInt("uid"), msg.getData().getString("xm")));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (adapter != null) {
            if (!adapter.isGlobalList()) {
                adapter.dispose();
            }
            adapter = null;
        }

        // emptyCollection = null;
        collection = null;
    }
}
