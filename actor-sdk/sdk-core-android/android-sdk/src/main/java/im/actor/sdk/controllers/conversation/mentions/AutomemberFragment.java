package im.actor.sdk.controllers.conversation.mentions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.SearchView;

import im.actor.core.entity.BotCommand;
import im.actor.core.entity.MentionFilterResult;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PeerType;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.util.Screen;
import im.actor.sdk.view.adapters.BottomSheetListView;
import im.actor.sdk.view.adapters.HolderAdapter;

import static im.actor.sdk.util.ActorSDKMessenger.users;

public class AutomemberFragment extends BaseFragment {

    private Peer peer;
    private boolean isBot;
    private boolean isGroup;

    private HolderAdapter autocompleteAdapter;
    private BottomSheetListView autocompleteList;
    private View underlyingView;

    public static AutomemberFragment create(Peer peer) {
        AutomemberFragment res = new AutomemberFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("peer", peer.getUnuqueId());
        res.setArguments(bundle);
        return res;
    }

    public AutomemberFragment() {
//        setShowTitle(true);
        setHomeAsUp(true);
        setHasOptionsMenu(true);
        setRootFragment(true);
        setTitle("选择提醒的人");
    }
    SearchView searchView;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contacts_search, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(true); // 缺省值就是true，可能不专门进行设置，false和true的效果图如下，true的输入框更大
//        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("搜索");
        searchView.onActionViewExpanded();
//        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchView, InputMethodManager.HIDE_NOT_ALWAYS);


        // 设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                int oldCount = autocompleteList.getCount();
                ((MemberEaglesoftAdapter) autocompleteAdapter).setQuery(query);
                expandMentions(autocompleteList, oldCount, autocompleteList.getCount());
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                int oldCount = autocompleteList.getCount();
                ((MemberEaglesoftAdapter) autocompleteAdapter).setQuery(newText);
                expandMentions(autocompleteList, oldCount, autocompleteList.getCount());
                return false;
            }
        });
        searchView.clearFocus();
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        peer = Peer.fromUniqueId(getArguments().getLong("peer"));
        if (peer.getPeerType() == PeerType.PRIVATE) {
            isBot = users().get(peer.getPeerId()).isBot();
            autocompleteAdapter = new CommandsAdapter(peer.getPeerId(), getContext());
        } else if (peer.getPeerType() == PeerType.GROUP) {
            isGroup = true;
            autocompleteAdapter = new MemberEaglesoftAdapter(peer.getPeerId(), getContext());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finishActivity();
            return true;
        }else if (i == R.id.search) {
            searchView.setFocusable(true);
            searchView.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);               }
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        autocompleteList = new BottomSheetListView(getContext());
        autocompleteList.setVisibility(View.INVISIBLE);
        autocompleteList.setUnderlyingView(underlyingView);
        autocompleteList.setDivider(null);
        autocompleteList.setDividerHeight(0);
        autocompleteList.setBackgroundColor(Color.TRANSPARENT);

        if (autocompleteAdapter != null) {
            autocompleteList.setAdapter(autocompleteAdapter);
        }
        autocompleteList.setOnItemClickListener((adapterView, view, i, l) -> {
            Object item = autocompleteAdapter.getItem(i);
            if (item instanceof MentionFilterResult) {
                String mention = ((MentionFilterResult) item).getMentionString();
                Activity activity = getActivity();
                if (activity instanceof AutocompleteCallback) {
                    ((AutocompleteCallback) activity).onMentionPicked(mention + ":");
                }

            } else if (item instanceof BotCommand) {
                String command = ((BotCommand) item).getSlashCommand();
//                Fragment parent = getParentFragment();
//                if (parent instanceof AutocompleteCallback) {
//                    ((AutocompleteCallback) parent).onCommandPicked(command);
//                }
                Activity activity = getActivity();
                if (activity instanceof AutocompleteCallback) {
                    ((AutocompleteCallback) activity).onCommandPicked(command);
                }
            }
        });

        // Initial zero height
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP;
        autocompleteList.setLayoutParams(params);

        int oldCount = autocompleteList.getCount();
        ((MemberEaglesoftAdapter) autocompleteAdapter).setQuery("");
        expandMentions(autocompleteList, oldCount, autocompleteList.getCount());


        return autocompleteList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autocompleteAdapter != null) {
            autocompleteAdapter.dispose();
        }
        autocompleteList = null;
    }


    //
    // Expand Animations
    //

    private void expandMentions(final BottomSheetListView list, final int oldRowsCount, final int newRowsCount) {
        list.post(() -> {
            if (newRowsCount == oldRowsCount) {
                return;
            }
            list.setMinHeight(Screen.dp(Screen.getHeight()));
//            list.setMinHeight(newRowsCount == 0 ? 0 : newRowsCount == 1 ? Screen.dp(48) + 1 : newRowsCount == 2 ? Screen.dp(96) + 2 : Screen.dp(122));
            list.setVisibility(View.VISIBLE);
//        Animation a = new ExpandAnimation(list, targetHeight, initialHeight);
//
//        a.setDuration((newRowsCount > oldRowsCount ? targetHeight : initialHeight / Screen.dp(1)));
//        a.setInterpolator(MaterialInterpolator.getInstance());
//        list.startAnimation(a);
        });

    }

    public void setUnderlyingView(View underlyingView) {
        this.underlyingView = underlyingView;
    }
}
