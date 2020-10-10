package im.actor.sdk.controllers.compose;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.actor.core.entity.Avatar;
import im.actor.core.entity.Contact;
import im.actor.core.entity.SearchEntity;
import im.actor.core.viewmodel.Command;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.contacts.view.ContactsEaglesoftAdapter;
import im.actor.sdk.controllers.contacts.view.MemberSideBar;
import im.actor.sdk.controllers.zuzhijiagou.ZuzhijiagouActivity;
import im.actor.sdk.util.Fonts;
import im.actor.sdk.util.Screen;
import im.actor.sdk.view.TintImageView;
import im.actor.sdk.view.adapters.HeaderViewRecyclerAdapter;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.users;

/**
 * Created by huchengjie on 2018/1/2.
 */

public class ComposeEaglesoftFragment extends BaseFragment {
    private RecyclerView collection;
    private RecyclerView.Adapter adapter;
    View emptyView;
    List<Contact> contactList;
    RelativeLayout contactReLay;

    //    MemberHander hander;
    public ComposeEaglesoftFragment() {
//        setRootFragment(true);
//        setHomeAsUp(true);
        super();
    }

    Handler handler;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
//        hander = new MemberHander();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    System.out.println("iGem:size=" + contactList.size());
                    adapter.notifyDataSetChanged();
                    collection.setVisibility(View.VISIBLE);
                    contactReLay.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("iGem:" + e.toString());
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_base_contacts_eaglesoft, container, false);
        collection = (RecyclerView) res.findViewById(R.id.eaglesoft_collection);
        collection.setVisibility(View.INVISIBLE);
        contactReLay = (RelativeLayout) res.findViewById(R.id.eaglesoft_collection_relay);
        contactReLay.setVisibility(View.GONE);
        setAnimationsEnabled(true);
        configureRecyclerView(collection);
        contactList = new ArrayList<Contact>();
        if (ActorSDK.getZjjgData() != null) {
            changeAdapter();
        }

        adapter = new ContactsEaglesoftAdapter(contactList, getActivity(), false, new OnItemClickedListener<Contact>() {
            @Override
            public void onClicked(Contact item) {
                getActivity().startActivity(Intents.openPrivateDialog(item.getUid(), true, getActivity()));
                getActivity().finish();
            }

            @Override
            public boolean onLongClicked(Contact item) {
                return false;
            }
        });
        collection.setAdapter(adapter);

        collection.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        emptyView = res.findViewById(R.id.emptyCollection);
        if (emptyView != null) {
            emptyView.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
            emptyView.findViewById(R.id.empty_collection_bg).setBackgroundColor(ActorSDK.sharedActor().style.getMainColor());
            ((TextView) emptyView.findViewById(R.id.empty_collection_text)).setTextColor(ActorSDK.sharedActor().style.getMainColor());
        } else {
            emptyView = res.findViewById(R.id.empty_collection_text);
            if (emptyView != null && emptyView instanceof TextView) {
                ((TextView) emptyView.findViewById(R.id.empty_collection_text)).setTextColor(ActorSDK.sharedActor().style.getMainColor());
            }
        }

        setAnimationsEnabled(false);

        View headerPadding = new View(getActivity());
        headerPadding.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        headerPadding.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, true ? 0 : ActorSDK.sharedActor().style.getContactsMainPaddingTop()));
        addHeaderView(headerPadding);

        addFootersAndHeaders();

        if (emptyView != null) {
            if (messenger().getAppState().getIsContactsEmpty().get()) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
        res.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());


        //右边的side
        MemberSideBar sideBar = (MemberSideBar) res.findViewById(R.id.side_bar);
        sideBar.setOnStrSelectCallBack(new MemberSideBar.ISideBarSelectCallBack() {
            @Override
            public void onSelectStr(int index, String selectStr) {
                if (selectStr.equals("↑")) {
                    collection.scrollToPosition(0);
                    return;
                }
//                Contact contactMatch = contactList.stream().filter(contact ->
//                        selectStr.equalsIgnoreCase(contact.getPyShort())
//                ).findFirst().get();
                for (int i = 0; i < contactList.size(); i++) {
//                    if (selectStr.equalsIgnoreCase(contactList.get(i).getPyShort())) {
////                        collection.scrollToPosition(i+4); // 选择到首字母出现的位置
//                        LinearLayoutManager manager = (LinearLayoutManager) collection.getLayoutManager();
//                        int firstItem = manager.findFirstVisibleItemPosition();
//                        int lastItem = manager.findLastVisibleItemPosition();
//                        int n = i + 5;
//                        if (n <= firstItem) {
//                            collection.scrollToPosition(n);
//                        } else if (n <= lastItem) {
//                            int top = collection.getChildAt(n - firstItem).getTop();
//                            collection.scrollBy(0, top);
//                        } else {
//                            collection.scrollToPosition(n);
//                        }
//                        return;
//                    }
                }
            }
        });

        return res;
    }


    public void changeAdapter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ActorSDK.getZjjgData() != null) {
//                        List<Contact> list = new ArrayList<>();
//                        contactList.clear();
                        JSONArray yh_array = ActorSDK.getZjjgData().getJSONArray("yh_data");
                        int index = -1;
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis());
                        System.out.println("iGem:1=" + formatter.format(curDate));
                        if (yh_array != null) {
                            for (int i = 0; i < yh_array.length(); i++) {
                                JSONObject json = yh_array.getJSONObject(i);
                                int uid = json.getInt("iGIMID");
                                try {
                                    UserVM userVM = users().get(uid);
                                    String name = userVM.getName().get();
                                    Avatar avatar = userVM.getAvatar().get();
                                    Contact contact = new Contact(uid, (long) index--, avatar, name);
//                                    String shortPY = PinyinHelper.getShortPinyin(name.substring(0, 1)).toUpperCase();
//                                    if (shortPY.matches("[A-Z]")) {
//                                        contact.setPyShort(shortPY);
//                                    } else {
//                                        contact.setPyShort("#");
//                                    }
                                    contactList.add(contact);
                                } catch (Exception e) {
                                    System.out.println("iGem:id=" + uid + ",name=" + json.getString("xm") + "没有这个人");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();

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

    protected void addFootersAndHeaders() {
        View header = new View(getActivity());
        header.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(8)));
        header.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        addHeaderView(header);

        View footer = new View(getActivity());
        footer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(4)));
        footer.setBackgroundColor(ActorSDK.sharedActor().style.getBackyardBackgroundColor());
        addFooterView(footer);


        addFooterOrHeaderAction(ActorSDK.sharedActor().style.getActionShareColor(),
                R.drawable.ic_megaphone_18dp_black, R.string.main_bar_organizational, false, () -> {
                    startActivity(new Intent(getActivity(), ZuzhijiagouActivity.class));
//                    getActivity().finish();
                }, true);


        addFooterOrHeaderAction(ActorSDK.sharedActor().style.getActionShareColor(),
                R.drawable.ic_group_white_24dp, R.string.main_fab_group, false, () -> {
                    startActivity(new Intent(getActivity(), CreateGroupActivity.class)
                            .putExtra(CreateGroupActivity.EXTRA_IS_CHANNEL, false));
//                    getActivity().finish();
                }, true);


        addFooterOrHeaderAction(ActorSDK.sharedActor().style.getActionShareColor(),
                R.drawable.ic_group_white_24dp, R.string.main_fab_new_group, false, () -> {
                    startActivity(new Intent(getActivity(), CreateGroupActivity.class)
                            .putExtra(CreateGroupActivity.EXTRA_IS_CHANNEL, false));
//                    getActivity().finish();
                }, true);
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

    protected void addFooterOrHeaderAction(int color, int icon, int text, boolean isLast, final Runnable action, boolean isHeader) {
        FrameLayout container = new FrameLayout(getActivity());
        container.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        {
            container.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        FrameLayout invitePanel = new FrameLayout(getActivity());
        invitePanel.setBackgroundResource(R.drawable.selector_fill);
        invitePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });
        {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(64));
//            params.leftMargin = Screen.dp(40);
            params.leftMargin = Screen.dp(2);
            invitePanel.setLayoutParams(params);
            container.addView(invitePanel);
        }

        TintImageView inviteIcon = new TintImageView(getActivity());
        inviteIcon.setTint(color);
        inviteIcon.setResource(icon);
        {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Screen.dp(52), Screen.dp(52));
            layoutParams.leftMargin = Screen.dp(6);
            layoutParams.topMargin = Screen.dp(6);
            layoutParams.bottomMargin = Screen.dp(6);
            layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            invitePanel.addView(inviteIcon, layoutParams);
        }

        TextView inviteText = new TextView(getActivity());
        inviteText.setText(getString(text).replace("{appName}", ActorSDK.sharedActor().getAppName()));
        inviteText.setTextColor(color);
//        inviteText.setPadding(Screen.dp(72), 0, Screen.dp(8), 0);
        inviteText.setPadding(Screen.dp(72), 0, Screen.dp(8), 0);
        inviteText.setTextSize(16);
        inviteText.setSingleLine(true);
        inviteText.setEllipsize(TextUtils.TruncateAt.END);
        inviteText.setTypeface(Fonts.medium());
        {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            layoutParams.topMargin = Screen.dp(16);
            layoutParams.bottomMargin = Screen.dp(16);
            invitePanel.addView(inviteText, layoutParams);
        }

        if (!isLast) {
            View div = new View(getActivity());
            div.setBackgroundColor(ActorSDK.sharedActor().style.getContactDividerColor());
            {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        getResources().getDimensionPixelSize(R.dimen.div_size));
                layoutParams.gravity = Gravity.BOTTOM;
                layoutParams.leftMargin = Screen.dp(72);
                invitePanel.addView(div, layoutParams);
            }
        }
        if (isHeader) {
            addHeaderView(container);
        } else {
            addFooterView(container);
        }
    }
}
