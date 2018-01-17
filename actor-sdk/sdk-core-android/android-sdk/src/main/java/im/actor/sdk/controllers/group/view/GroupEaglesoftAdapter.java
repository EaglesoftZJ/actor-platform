package im.actor.sdk.controllers.group.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

import java.util.HashSet;
import java.util.List;

import im.actor.core.entity.Contact;
import im.actor.core.entity.Group;
import im.actor.core.viewmodel.GroupVM;
import im.actor.sdk.view.adapters.OnItemClickedListener;


public class GroupEaglesoftAdapter extends RecyclerView.Adapter  {

    private final HashSet<Integer> selectedUsers = new HashSet<Integer>();

    private final OnItemClickedListener<GroupVM> onItemClickedListener;

    private boolean selectable;

    private final Context context;

    private String query = "";
    List<GroupVM> displayList;

    public GroupEaglesoftAdapter(List<GroupVM> displayList, Context context, boolean selectable,
                                 OnItemClickedListener<GroupVM> onItemClickedListener) {
        this.displayList = displayList;
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

    public void onBindViewHolder(GroupEaglesoftHolder contactHolder, int index, GroupVM item) {
        String fastName = item.getName().get();

        contactHolder.bind(item, fastName, query, selectedUsers.contains(item.getName().get()), index == getItemCount() - 1);
    }

    @Override
    public GroupEaglesoftHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new GroupEaglesoftHolder(new FrameLayout(context), selectable, context, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((GroupEaglesoftHolder) holder, position, getItem(position));
    }

    public GroupVM getItem(int index) {
        return displayList.get(index);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }



}
