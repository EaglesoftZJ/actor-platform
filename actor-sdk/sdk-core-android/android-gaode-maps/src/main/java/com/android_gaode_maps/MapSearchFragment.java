package com.android_gaode_maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.util.Screen;
import im.actor.sdk.view.adapters.HeaderViewRecyclerAdapter;

public class MapSearchFragment extends BaseFragment {

    public MenuItem searchMenu;
    private SearchView searchView;

    private boolean isSearchVisible = false;
    private ListView searchList;
    private View searchContainer;
    private TextView searchEmptyView;
    private TextView searchHintView;

    private PlacesAdapter searchAdapter;
    private ArrayList<MapItem> places;
    private String searchQuery;
    private boolean scrolledToEnd = true;

    public MapSearchFragment() {
        setHasOptionsMenu(true);
        setUnbindOnPause(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View res = inflater.inflate(R.layout.fragment_map_search, container, false);
        searchList = (ListView) res.findViewById(im.actor.sdk.R.id.searchList);

        searchContainer = res.findViewById(im.actor.sdk.R.id.searchCont);
        searchContainer.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());
        searchEmptyView = (TextView) res.findViewById(im.actor.sdk.R.id.empty);
        searchHintView = (TextView) res.findViewById(im.actor.sdk.R.id.searchHint);
        searchEmptyView.setTextColor(style.getTextSecondaryColor());
        searchHintView.setTextColor(style.getTextSecondaryColor());
        searchHintView.setVisibility(View.GONE);
        searchEmptyView.setVisibility(View.GONE);

        return res;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(im.actor.sdk.R.menu.fragment_global_search, menu);

        searchMenu = menu.findItem(im.actor.sdk.R.id.search);
//        if (messenger().getAppState().getIsAppEmpty().get() || !isShow) {
//            searchMenu.setVisible(false);
//        } else {
//            searchMenu.setVisible(true);
//        }
        searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showSearch();
                return true;
//                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

//        MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat().OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                showSearch();
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
////                hideSearch();
//                return true;
//            }
//        });
        searchView = (SearchView) searchMenu.getActionView();
        searchView.setIconifiedByDefault(true);


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
                    }
                }
                return false;
            }
        });
    }

    private void showSearch() {
        searchAdapter = new PlacesAdapter(getContext(), null);

        View header = new View(getActivity());
        header.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Screen.dp(0)));
        header.setBackgroundColor(ActorSDK.sharedActor().style.getMainBackgroundColor());

        searchList.setAdapter(searchAdapter);
        showView(searchHintView, false);
        goneView(searchEmptyView, false);

        showView(searchContainer, false);
    }


}
