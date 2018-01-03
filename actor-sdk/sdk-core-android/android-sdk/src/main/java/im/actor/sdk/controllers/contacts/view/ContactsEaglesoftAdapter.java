package im.actor.sdk.controllers.contacts.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashSet;
import java.util.List;

import im.actor.core.entity.Contact;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class ContactsEaglesoftAdapter extends RecyclerView.Adapter {

    private final HashSet<Integer> selectedUsers = new HashSet<Integer>();

    private final OnItemClickedListener<Contact> onItemClickedListener;

    private boolean selectable;

    private final Context context;

    private String query = "";
    List<Contact> displayList;

    public ContactsEaglesoftAdapter(List<Contact> displayList, Context context, boolean selectable,
                                    OnItemClickedListener<Contact> onItemClickedListener) {
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

    public void onBindViewHolder(ContactEaglesoftHolder contactHolder, int index, Contact item) {
        String fastName = null;
        if (index == 0) {
            fastName = messenger().getFormatter().formatFastName(item.getName());
        } else {
            String prevName = messenger().getFormatter().formatFastName(getItem(index - 1).getName());
            String currentFastName = messenger().getFormatter().formatFastName(item.getName());
            if (!prevName.equals(currentFastName)) {
                fastName = currentFastName;
            }
        }
        contactHolder.bind(item, fastName, query, selectedUsers.contains(item.getUid()), index == getItemCount() - 1);
    }

    @Override
    public ContactEaglesoftHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ContactEaglesoftHolder(new FrameLayout(context), selectable, context, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((ContactEaglesoftHolder)holder, position, getItem(position));
    }

    public Contact getItem(int index){
        return displayList.get(index);
    }
    @Override
    public int getItemCount() {
        return displayList.size();
    }
}
