package im.actor.sdk.controllers.zuzhijiagou.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ChatLinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.SimpleItemAnimator;
import im.actor.core.entity.Avatar;
import im.actor.core.entity.Peer;
import im.actor.core.entity.PeerSearchEntity;
import im.actor.core.entity.PeerType;
import im.actor.core.entity.SearchEntity;
import im.actor.core.viewmodel.CommandCallback;
import im.actor.core.viewmodel.GroupVM;
import im.actor.core.viewmodel.UserVM;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.runtime.generic.mvvm.DisplayList;
import im.actor.runtime.generic.mvvm.alg.Modifications;
import im.actor.runtime.json.JSONArray;
import im.actor.runtime.json.JSONObject;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.root.Node;
import im.actor.sdk.controllers.search.GlobalSearchStateDelegate;
import im.actor.sdk.controllers.zuzhijiagou.ZzjgFragment;
import im.actor.sdk.util.Screen;
import im.actor.sdk.view.adapters.HeaderViewRecyclerAdapter;
import im.actor.sdk.view.adapters.OnItemClickedListener;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;
import static im.actor.sdk.util.ActorSDKMessenger.users;

public abstract class ZuZhiJiaGouSearchBaseFragment extends BaseFragment {

    public MenuItem searchMenu;
    private SearchView searchView;

    private boolean isSearchVisible = false;
    private RecyclerView searchList;
    private View searchContainer;
    private TextView searchEmptyView;
    private TextView searchHintView;

    private ZzjgSearchAdapter searchAdapter;
    private BindedDisplayList<SearchEntity> searchDisplay;
    private final DisplayList.Listener searchListener = () -> onSearchChanged();

    private String searchQuery;
    private boolean scrolledToEnd = true;
    private ArrayList<ZzjgSearchEntity> globalSearchResults = new ArrayList<>();

    public ZuZhiJiaGouSearchBaseFragment() {
        setHasOptionsMenu(true);
        setUnbindOnPause(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_global_search, container, false);
        res.setVisibility(View.GONE);

        searchList = (RecyclerView) res.findViewById(R.id.searchList);
        searchList.setLayoutManager(new ChatLinearLayoutManager(getActivity()));
        searchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && isSearchVisible) {
                    if (searchView != null) {
                        searchView.clearFocus();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });

        searchContainer = res.findViewById(R.id.searchCont);
        searchContainer.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        searchEmptyView = (TextView) res.findViewById(R.id.empty);
        searchHintView = (TextView) res.findViewById(R.id.searchHint);
        searchEmptyView.setTextColor(style.getTextSecondaryColor());
        searchHintView.setTextColor(style.getTextSecondaryColor());
        searchHintView.setVisibility(View.GONE);
        searchEmptyView.setVisibility(View.GONE);

        return res;
    }

    @Override
    public void onResume() {
        super.onResume();

        bind(messenger().getAppState().getIsAppLoaded(), messenger().getAppState().getIsAppEmpty(), (isAppLoaded, Value, isAppEmpty, Value2) -> {
            Activity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_global_search, menu);

        searchMenu = menu.findItem(R.id.search);
        if (messenger().getAppState().getIsAppEmpty().get() || !isShow) {
            searchMenu.setVisible(false);
        } else {
            searchMenu.setVisible(true);
        }

        searchView = (SearchView) searchMenu.getActionView();
        searchView.setIconifiedByDefault(true);

        MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showSearch();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                hideSearch();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchQuery = s.trim();

                if (isSearchVisible) {
                    if (s.trim().length() > 0) {
                        String activeSearchQuery = searchQuery;
//                        searchDisplay.initSearch(s.trim().toLowerCase(), false);
                        scrolledToEnd = false;
                        searchAdapter.setQuery(s.trim().toLowerCase());
                        globalSearchResults.clear();

                        org.json.JSONArray yh_array = null;
                        try {
                            yh_array = ActorSDK.getZjjgData().getJSONArray("yh_data");
                            int order = 0;
                            List<Node> yhList = ZzjgFragment.getListNode(yh_array, new String[]{"IGIMID", "xm", "wzh", "fid", "dwid", "bmid"});
                            List<ZzjgSearchEntity> ryList = new ArrayList<>();
                            for (Node node : yhList) {
                                if (node.getText().contains(searchQuery)) {
                                    try {
                                        int uid = Integer.parseInt(node.getValue());
                                        UserVM userVM = users().get(uid);
                                        String name = userVM.getName().get();
                                        Avatar avatar = userVM.getAvatar().get();
                                        ryList.add(new ZzjgSearchEntity(new Peer(PeerType.PRIVATE, uid), order++, avatar, name, node));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            searchAdapter.setList(new ArrayList<>());
                            searchAdapter.getList().addAll(ryList);
                            searchAdapter.notifyDataSetChanged();
                            if (globalSearchResults.size() > 0) {
                                globalSearchResults.add(new SearchEntityHeader(order++));
                            }

//                            Modifications.addLoadMore(globalSearchResults);
//                            checkGlobalSearch();
                            onSearchChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        searchAdapter.setList(new ArrayList<>());
//                        searchAdapter.getList().addAll(ryList);
                        searchAdapter.notifyDataSetChanged();
                        initEmpty();
//                        searchDisplay.initEmpty();
                    }
                }
                return false;
            }
        });
    }

    private void initEmpty(){
        goneView(searchEmptyView);
        showView(searchHintView);
    }

    private void onSearchChanged() {
        if(searchAdapter.getItemCount()==0){
            showView(searchEmptyView);
            goneView(searchHintView);
        }else{
            goneView(searchHintView);
            goneView(searchEmptyView);
        }



//        if (searchDisplay == null) {
//            return;
//        }
//        if (!searchDisplay.isInSearchState()) {
//            showView(searchHintView);
//            goneView(searchEmptyView);
//        } else {
//            goneView(searchHintView);
//            if (searchDisplay.getSize() == 0) {
//                showView(searchEmptyView);
//            } else {
//                goneView(searchEmptyView);
//            }
//        }
    }

    private void showSearch() {
        if (isSearchVisible) {
            return;
        }
        isSearchVisible = true;
        scrolledToEnd = true;

//        searchDisplay = messenger().buildSearchDisplayList();
//        searchDisplay.setBindHook(new BindedDisplayList.BindHook<SearchEntity>() {
//            @Override
//            public void onScrolledToEnd() {
//                scrolledToEnd = true;
//                checkGlobalSearch();
//            }
//
//            @Override
//            public void onItemTouched(SearchEntity item) {
//
//            }
//        });
        searchAdapter = new ZzjgSearchAdapter(getActivity(), new ArrayList<ZzjgSearchEntity>(), new OnItemClickedListener<ZzjgSearchEntity>() {
            @Override
            public void onClicked(ZzjgSearchEntity item) {
                onPeerPicked(item.getPeer());
                searchMenu.collapseActionView();
            }

            @Override
            public boolean onLongClicked(ZzjgSearchEntity item) {
                return false;
            }
        });
        HeaderViewRecyclerAdapter recyclerAdapter = new HeaderViewRecyclerAdapter(searchAdapter);

        View header = new View(getActivity());
        header.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(0)));
        header.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        recyclerAdapter.addHeaderView(header);

        searchList.setAdapter(recyclerAdapter);

        RecyclerView.ItemAnimator animator = searchList.getItemAnimator();

        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }


//        searchDisplay.addListener(searchListener);
        showView(searchHintView, false);
        goneView(searchEmptyView, false);

        showView(searchContainer, false);

        Fragment parent = getParentFragment();
        if (parent != null && parent instanceof GlobalSearchStateDelegate) {
            ((GlobalSearchStateDelegate) parent).onGlobalSearchStarted();
        }
    }

    private void checkGlobalSearch() {
        if ((scrolledToEnd || searchDisplay.getSize() == 0) && globalSearchResults.size() > 0) {
//            searchDisplay.editList(Modifications.addLoadMore(globalSearchResults));
        }
    }

    //是否在页面上显示menu
    boolean isShow = true;

    public void pageActivityHide(boolean isShow) {
        searchMenu.setVisible(isShow);
        this.isShow = isShow;
        hideSearch();
    }

    private void hideSearch() {
        if (!isSearchVisible) {
            return;
        }
        isSearchVisible = false;

        if (searchDisplay != null) {
            searchDisplay.dispose();
            searchDisplay = null;
        }
        searchAdapter = null;
        searchList.setAdapter(null);
        searchQuery = null;

        goneView(searchContainer, false);
        if (searchMenu != null) {
            if (searchMenu.isActionViewExpanded()) {
                searchMenu.collapseActionView();
            }
        }

        Fragment parent = getParentFragment();
        if (parent != null && parent instanceof GlobalSearchStateDelegate) {
            ((GlobalSearchStateDelegate) parent).onGlobalSearchEnded();
        }
    }

    public class SearchEntityHeader extends ZzjgSearchEntity {

        public SearchEntityHeader(int order) {
            super(Peer.group(0), order, null, "", null);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        hideSearch();
    }


    protected abstract void onPeerPicked(Peer peer);

    @Override
    public void finishActivity() {
        super.finishActivity();
        searchMenu.setVisible(false);
    }
}
