package im.actor.sdk.controllers.zuzhijiagou;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.zuzhijiagou.search.ZzjgSearchFragment;

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
