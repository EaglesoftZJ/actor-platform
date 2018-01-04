package im.actor.sdk.controllers.contacts.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

import java.util.HashSet;

import im.actor.core.entity.Contact;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.view.adapters.OnItemClickedListener;
import im.actor.runtime.android.view.BindedListAdapter;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

public class ContactsAdapter extends BindedListAdapter<Contact, ContactHolder> implements SectionIndexer {

    private final HashSet<Integer> selectedUsers = new HashSet<Integer>();

    private final OnItemClickedListener<Contact> onItemClickedListener;

    private boolean selectable;

    private final Context context;

    private String query = "";

    public ContactsAdapter(BindedDisplayList<Contact> displayList, Context context, boolean selectable,
                           OnItemClickedListener<Contact> onItemClickedListener) {
        super(displayList);
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

    @Override
    public void onBindViewHolder(ContactHolder contactHolder, int index, Contact item) {
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
    public ContactHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ContactHolder(new FrameLayout(context), selectable, context, onItemClickedListener);
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = displayList.getList().get(i).getPyShort();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return displayList.getList().get(position).getPyShort().charAt(0);
    }
}
