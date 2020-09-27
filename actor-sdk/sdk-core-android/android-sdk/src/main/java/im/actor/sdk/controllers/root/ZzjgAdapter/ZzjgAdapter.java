package im.actor.sdk.controllers.root.ZzjgAdapter;

/**
 * Created by huchengjie on 2017/9/22.
 */


import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashSet;

import androidx.recyclerview.widget.RecyclerView;
import im.actor.core.entity.Contact;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.controllers.root.Node;
import im.actor.sdk.view.adapters.OnItemClickedListener;
import im.actor.runtime.android.view.BindedListAdapter;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class ZzjgAdapter extends RecyclerView.Adapter {

    private final HashSet<Integer> selectedUsers = new HashSet<Integer>();

    private final OnItemClickedListener<Contact> onItemClickedListener;

    private boolean selectable;

    private final Context context;

    private String query = "";

    public ZzjgAdapter(Node node, Context context, boolean selectable,
                       OnItemClickedListener<Contact> onItemClickedListener) {
        this.context = context;
        this.selectable = selectable;
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setQuery(String query) {
        this.query = query;
        notifyDataSetChanged();
    }

    public void select(int uid) {
        selectedUsers.add(uid);
    }

    public void unselect(int uid) {
        selectedUsers.remove(uid);
    }

    public Integer[] getSelected() {
        return selectedUsers.toArray(new Integer[selectedUsers.size()]);
    }

    public boolean isSelected(int uid) {
        return selectedUsers.contains(uid);
    }

    public int getSelectedCount() {
        return selectedUsers.size();
    }

//    @Override
//    public void onBindViewHolder(ZzjgHolder contactHolder, int index, Contact item) {
//        String fastName = null;
//        if (index == 0) {
//            fastName = messenger().getFormatter().formatFastName(item.getName());
//        } else {
//            String prevName = messenger().getFormatter().formatFastName(getItem(index - 1).getName());
//            String currentFastName = messenger().getFormatter().formatFastName(item.getName());
//            if (!prevName.equals(currentFastName)) {
//                fastName = currentFastName;
//            }
//        }
//        contactHolder.bind(item, fastName, query, selectedUsers.contains(item.getUid()), index == getItemCount() - 1);
//    }

    @Override
    public ZzjgHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ZzjgHolder(new FrameLayout(context), selectable, context, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        String fastName = null;
//        if (position == 0) {
//            fastName = messenger().getFormatter().formatFastName(item.getName());
//        } else {
//            String prevName = messenger().getFormatter().formatFastName(getItem(index - 1).getName());
//            String currentFastName = messenger().getFormatter().formatFastName(item.getName());
//            if (!prevName.equals(currentFastName)) {
//                fastName = currentFastName;
//            }
//        }
//        contactHolder.bind(item, fastName, query, selectedUsers.contains(item.getUid()), index == getItemCount() - 1);

    }


    @Override
    public int getItemCount() {
        return 0;
    }
}

