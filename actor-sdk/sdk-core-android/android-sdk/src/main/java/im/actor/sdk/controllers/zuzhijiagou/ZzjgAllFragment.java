package im.actor.sdk.controllers.zuzhijiagou;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.Intents;
import im.actor.sdk.controllers.dialogs.DialogsDefaultFragment;
import im.actor.sdk.controllers.placeholder.GlobalPlaceholderFragment;
import im.actor.sdk.controllers.root.Node;
import im.actor.sdk.controllers.search.GlobalSearchDefaultFragment;
import im.actor.sdk.controllers.zuzhijiagou.search.ZzjgSearchFragment;
import im.actor.sdk.controllers.zuzhijiagou.topBar.TopBarAdapter;
import im.actor.sdk.controllers.zuzhijiagou.topBar.TopBarItemDecoration;
import im.actor.sdk.controllers.zuzhijiagou.topBar.TreeBarBean;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by huchengjie on 2017/9/21.
 */

public class ZzjgAllFragment extends BaseFragment {


    public ZzjgAllFragment() {
        setRootFragment(true);
        setHomeAsUp(true);
        setTitle("组织架构");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout res = new FrameLayout(getContext());

        FrameLayout content = new FrameLayout(getContext());
        content.setId(R.id.content);
        res.addView(content, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout fab = new FrameLayout(getContext());
        fab.setId(R.id.search);
        res.addView(fab, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return res;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ZzjgFragment dialogsDefaultFragment = new ZzjgFragment();
        getChildFragmentManager().beginTransaction()
                .add(R.id.content, dialogsDefaultFragment )
                .add(R.id.search, new ZzjgSearchFragment())
                .commit();

    }


}
