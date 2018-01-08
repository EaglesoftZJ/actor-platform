/*
 * Copyright (C) 2015 Actor LLC. <https://actor.im>
 */

package im.actor.runtime.android.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import im.actor.core.Messenger;
import im.actor.core.entity.Contact;
import im.actor.runtime.generic.mvvm.AndroidListUpdate;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.ChangeDescription;
import im.actor.runtime.generic.mvvm.DisplayList;
import im.actor.runtime.bser.BserObject;
import im.actor.runtime.storage.ListEngineItem;

public abstract class BindedListAdapter<V extends BserObject & ListEngineItem,
        T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    public BindedDisplayList<V> displayList;

    private DisplayList.AndroidChangeListener<V> listener;
    // private DisplayList.Listener listener;

    private AndroidListUpdate<V> currentUpdate = null;

    public BindedListAdapter(BindedDisplayList<V> displayList) {
        this(displayList, true);
    }

    public BindedListAdapter(BindedDisplayList<V> displayList, boolean autoConnect) {
        this.displayList = displayList;
        setHasStableIds(true);

        listener = new DisplayList.AndroidChangeListener<V>() {
            @Override
            public void onCollectionChanged(AndroidListUpdate<V> modification) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss SSS");
//                if (displayList != null && displayList.getSize() > 0
//                        && displayList.getItem(0) instanceof Contact) {
//                    System.out.println("iGem: 0 timne1 = " + Messenger.pyTime);
//                    System.out.println("iGem: 0 timne2 = " + Messenger.pyTime2);
//                    Messenger.pyTime = 0;
//                    Messenger.pyTime2 = 0;
//                    Date curDate = new Date(System.currentTimeMillis());
//                    System.out.println("iGem: 1=" + format.format(curDate));
//                    Collections.sort(displayList.getList(), (vo1, vo2) -> {
//                        String l = null;
//                        try {
//                            Contact lhs = (Contact) vo1;
//                            Contact rhs = (Contact) vo2;
////                            l = lhs.getPyShort();
////                            String r = rhs.getPyShort();
//                            if (r.equals("#")) {
//                                return -1;
//                            } else if (l.equals("#")) {
//                                return 1;
//                            }
//                            int result = 0;
//                            int i = 0;
//                            if (l.charAt(i) < r.charAt(i)) {
//                                result = -1;
//                            } else if (l.charAt(i) > r.charAt(i)) {
//                                result = 1;
//                            } else {
//                                result = 0;
//                            }
//
//                            if (result == 0) {
//                                return lhs.getName().compareTo(rhs.getName());
//                            }
//                            return result;
//                        } catch (Exception e) {
////                            System.out.println("iGem:" + e.toString());
////                            e.printStackTrace();
//                        }
//                        return 0;
//                    });
//                    curDate = new Date(System.currentTimeMillis());
//                    System.out.println("iGem: 2=" + format.format(curDate));
//                }
                currentUpdate = modification;
                ChangeDescription<V> currentChange;
                Date curDate = new Date(System.currentTimeMillis());
                System.out.println("iGem: 3=" + format.format(curDate));
                while ((currentChange = modification.next()) != null) {
                    switch (currentChange.getOperationType()) {
                        case ADD:
                            notifyItemRangeInserted(currentChange.getIndex(), currentChange.getLength());
                            break;
                        case UPDATE:
                            notifyItemRangeChanged(currentChange.getIndex(), currentChange.getLength());
                            break;
                        case MOVE:
                            notifyItemMoved(currentChange.getIndex(), currentChange.getDestIndex());
                            break;
                        case REMOVE:
                            notifyItemRangeRemoved(currentChange.getIndex(), currentChange.getLength());
                            break;
                    }
                }
                curDate = new Date(System.currentTimeMillis());
                System.out.println("iGem: 4=" + format.format(curDate));
                currentUpdate = null;
            }
        };
        if (autoConnect) {
            resume();
        }
    }

    public boolean isGlobalList() {
        return displayList.isGlobalList();
    }

    public Object getPreprocessedList() {
        return displayList.getProcessedList();
    }

    @Override
    public int getItemCount() {
        if (currentUpdate != null) {
            return currentUpdate.getSize();
        }
        return displayList.getSize();
    }

    protected V getItem(int position) {
        if (currentUpdate != null) {
            return currentUpdate.getItem(position);
        }
        return displayList.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getEngineId();
    }

    @Override
    public abstract T onCreateViewHolder(ViewGroup viewGroup, int viewType);

    @Override
    public final void onBindViewHolder(T dialogHolder, int i) {
        displayList.touch(i);
        onBindViewHolder(dialogHolder, i, getItem(i));
    }

    public abstract void onBindViewHolder(T dialogHolder, int index, V item);


    public void resume() {
        displayList.addAndroidListener(listener);
        notifyDataSetChanged();
    }

    public void pause() {
        displayList.removeAndroidListener(listener);
    }

    public void dispose() {
        pause();
    }
}
