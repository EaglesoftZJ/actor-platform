package im.actor.sdk.controllers.root;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;
import im.actor.sdk.controllers.compose.ComposeEaglesoftFragment;
import im.actor.sdk.controllers.compose.ComposeFabFragment;
import im.actor.sdk.controllers.compose.ComposeFragment;
import im.actor.sdk.controllers.contacts.ContactsActivity;
import im.actor.sdk.controllers.contacts.ContactsFragment;
import im.actor.sdk.controllers.dialogs.DialogsDefaultFragment;
import im.actor.sdk.controllers.dialogs.DialogsFragment;
import im.actor.sdk.controllers.fragment.help.HelpActivity;
import im.actor.sdk.controllers.placeholder.GlobalPlaceholderFragment;
import im.actor.sdk.controllers.search.GlobalSearchDefaultFragment;
import im.actor.sdk.controllers.settings.ActorSettingsFragment;
import im.actor.sdk.controllers.settings.BaseActorSettingsActivity;
import im.actor.sdk.controllers.settings.BaseActorSettingsFragment;
import im.actor.sdk.intents.ActorIntent;
import im.actor.sdk.intents.WebServiceUtil;
import im.actor.sdk.view.ActorViewPager;
import im.actor.sdk.view.PagerSlidingTabStrip;
import im.actor.sdk.view.adapters.FragmentNoMenuStatePagerAdapter;

public class RootPageFragment extends BaseFragment {

    ActorViewPager pager;
    int pageSize = 3;
    private HomePagerAdapter homePagerAdapter;

    RootFragment rootFragment;//消息页面
    RootComposeFragment contactsFram;//通讯录页面

    RootZzjgFragment zzjgFragment;//组织结构

    TextView text1;
    TextView text2;
    TextView text3;
    TextView text4;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;

    private int prevPage = 0;

    public RootPageFragment() {
        setRootFragment(true);
        setUnbindOnPause(true);
        setTitle(ActorSDK.sharedActor().getAppName());
    }

    private boolean isInited = false;

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        if (saveInstance != null) {
            isInited = saveInstance.getBoolean("is_inited");
        }
        boolean isMoa = false;
        if (isMoa) {
            pageSize = 4;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_root_page, container, false);
        pager = (ActorViewPager) view.findViewById(R.id.vp_pager);
        pager.setScanScroll(false);//禁止滑动
        pager.setOffscreenPageLimit(pageSize);
        homePagerAdapter = getHomePagerAdapter();
        pager.setAdapter(homePagerAdapter);
        pager.setCurrentItem(0);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageChange(position, view);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        text1 = (TextView) ((View) view).
                findViewById(R.id.root_text1);
        text2 = (TextView) ((View) view).
                findViewById(R.id.root_text2);
        text3 = (TextView) ((View) view).
                findViewById(R.id.root_text3);

        image1 = (ImageView) ((View) view).findViewById(R.id.root_Image1);
        image2 = (ImageView) ((View) view).findViewById(R.id.root_Image2);
        image3 = (ImageView) ((View) view).findViewById(R.id.root_Image3);

        if (pageSize == 4) {
            text4 = (TextView) ((View) view).
                    findViewById(R.id.root_text4);
            image4 = (ImageView) ((View) view).findViewById(R.id.root_Image4);
        }

        LinearLayout layPage1 = (LinearLayout) view.findViewById(R.id.root_page1);
        layPage1.setOnClickListener(new pageOclickListener(0));

        LinearLayout layPage2 = (LinearLayout) view.findViewById(R.id.root_page2);
        if (pageSize == 4) {
            layPage2.setOnClickListener(new pageOclickListener(2));
        } else {
            layPage2.setOnClickListener(new pageOclickListener(1));
        }

        LinearLayout layPage3 = (LinearLayout) view.findViewById(R.id.root_page3);
        if (pageSize == 4) {
            layPage3.setOnClickListener(new pageOclickListener(3));
        } else {
            layPage3.setOnClickListener(new pageOclickListener(2));
        }

        if (pageSize == 4) {
            LinearLayout layPage4 = (LinearLayout) view.findViewById(R.id.root_page4);
            layPage4.setVisibility(View.VISIBLE);
            layPage4.setOnClickListener(new pageOclickListener(1));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (prevPage == 0) {
            rootFragment.searchFram.createMenu(menu, inflater);
        } else if (prevPage == 1 && pageSize == 3) {
//            contactsFram.createMainMenu(menu, inflater);
            contactsFram.searchFram.createMenu(menu, inflater);
        } else if (prevPage == 2 && pageSize == 4) {
//            contactsFram.createMainMenu(menu, inflater);
            contactsFram.searchFram.createMenu(menu, inflater);
        }
//        inflater.inflate(R.menu.main, menu);
    }


    class pageOclickListener implements View.OnClickListener {
        int pagePos;

        pageOclickListener(int pagePos) {
            this.pagePos = pagePos;
        }

        @Override
        public void onClick(View view) {
            pager.setCurrentItem(pagePos);
            pageChange(pagePos, view);

        }
    }

    private void pageChange(int pagePos, View view) {
        prevPage = pagePos;
        getActivity().supportInvalidateOptionsMenu();
        ActionBar actionBar = getSupportActionBar();
        if (rootFragment != null) {
//            rootFragment.removeSearchFram();
            if (actionBar != null) {
                actionBar.show();
            }
        }
        switch (pagePos) {
            case 0:
                text1.setTextColor(getResources().getColor(R.color.action));
                text2.setTextColor(getResources().getColor(R.color.action_no));
                text3.setTextColor(getResources().getColor(R.color.action_no));
//                text4.setTextColor(getResources().getColor(R.color.selector_ripple));

                image1.setImageResource(R.drawable.root_page_duble_bubble_check);
                image2.setImageResource(R.drawable.root_page_contact_uncheck);
                image3.setImageResource(R.drawable.root_page_setting_uncheck);

                if (pageSize == 4) {
                    text4.setTextColor(getResources().getColor(R.color.action_no));
                    image4.setImageResource(R.drawable.root_page_work_uncheck);
                }
//                if (rootFragment != null) {
//                    rootFragment.addSearchFram();
//                }
                break;
            case 1:
                if (pageSize == 4) {
                    text1.setTextColor(getResources().getColor(R.color.action_no));
                    text2.setTextColor(getResources().getColor(R.color.action_no));
                    text3.setTextColor(getResources().getColor(R.color.action_no));

                    image1.setImageResource(R.drawable.root_page_duble_bubble_uncheck);
                    image2.setImageResource(R.drawable.root_page_contact_uncheck);
                    image3.setImageResource(R.drawable.root_page_setting_uncheck);

                    if (pageSize == 4) {
                        text4.setTextColor(getResources().getColor(R.color.action));
                        image4.setImageResource(R.drawable.root_page_work_check);
                    }
                    if (actionBar != null) {
                        actionBar.hide();
                    }

                } else {
                    text1.setTextColor(getResources().getColor(R.color.action_no));
                    text2.setTextColor(getResources().getColor(R.color.action));
                    text3.setTextColor(getResources().getColor(R.color.action_no));
                    image1.setImageResource(R.drawable.root_page_duble_bubble_uncheck);
                    image2.setImageResource(R.drawable.root_page_contact_check);
                    image3.setImageResource(R.drawable.root_page_setting_uncheck);
                }

                break;
            case 2:
                if (pageSize == 4) {
                    text4.setTextColor(getResources().getColor(R.color.action_no));
                    image4.setImageResource(R.drawable.root_page_work_uncheck);

                    text1.setTextColor(getResources().getColor(R.color.action_no));
                    text2.setTextColor(getResources().getColor(R.color.action));
                    text3.setTextColor(getResources().getColor(R.color.action_no));

                    image1.setImageResource(R.drawable.root_page_duble_bubble_uncheck);
                    image2.setImageResource(R.drawable.root_page_contact_check);
                    image3.setImageResource(R.drawable.root_page_setting_uncheck);
                } else {
                    text1.setTextColor(getResources().getColor(R.color.action_no));
                    text2.setTextColor(getResources().getColor(R.color.action_no));
                    text3.setTextColor(getResources().getColor(R.color.action));

                    image1.setImageResource(R.drawable.root_page_duble_bubble_uncheck);
                    image2.setImageResource(R.drawable.root_page_contact_uncheck);
                    image3.setImageResource(R.drawable.root_page_setting_check);
                }

                break;
            case 3:
                text4.setTextColor(getResources().getColor(R.color.action_no));
                image4.setImageResource(R.drawable.root_page_work_uncheck);

                text1.setTextColor(getResources().getColor(R.color.action_no));
                text2.setTextColor(getResources().getColor(R.color.action_no));
                text3.setTextColor(getResources().getColor(R.color.action));

                image1.setImageResource(R.drawable.root_page_duble_bubble_uncheck);
                image2.setImageResource(R.drawable.root_page_contact_uncheck);
                image3.setImageResource(R.drawable.root_page_setting_check);

                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.help) {
            startActivity(new Intent(getActivity(), HelpActivity.class));
            return true;
        } else if (i == R.id.profile) {
            ActorSDK.sharedActor().startSettingActivity(getActivity());
            return true;
        } else if (i == R.id.contacts) {
            startActivity(new Intent(getActivity(), ContactsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_inited", isInited);
    }

    @NonNull
    public HomePagerAdapter getHomePagerAdapter() {
        return new HomePagerAdapter(getFragmentManager());
    }


    public class HomePagerAdapter extends FragmentNoMenuStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {


        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return pageSize;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0:
                    return getDialogsFragment();
                case 1:
                    if (pageSize == 4) {
                        Fragment fragment = new MainMoaFragment();
                        return fragment;
                    } else {
                        return getContactsFragment();
                    }
                case 2:
                    if (pageSize == 4) {
                        return getContactsFragment();
                    } else {
                        BaseActorSettingsFragment fragment2 = new ActorSettingsFragment();
                        return fragment2;
                    }

                case 3:
                    BaseActorSettingsFragment fragment3 = new ActorSettingsFragment();
                    return fragment3;
            }
        }

        @NonNull
        public Fragment getContactsFragment() {
            contactsFram = new RootComposeFragment();
//            if(contactsFram == null){
//                contactsFram= new ComposeEaglesoftFragment();
//            }
//            res2.setHasOptionsMenu(false);
            return contactsFram;
        }

        @NonNull
        public Fragment getDialogsFragment() {
            Fragment fragment = ActorSDK.sharedActor().getDelegate().fragmentForRoot();
            if (fragment == null) {
                fragment = new RootFragment();
            }
            rootFragment = (RootFragment) fragment;
            return rootFragment;
//            DialogsDefaultFragment dialogsDefaultFragment = ActorSDK.sharedActor().getDelegate().fragmentForDialogs();
//            return dialogsDefaultFragment != null ? dialogsDefaultFragment : new DialogsDefaultFragment();
        }

        public Fragment getZzjg() {
            if (zzjgFragment == null)
                zzjgFragment = new RootZzjgFragment();
            return zzjgFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return getActivity().getString(R.string.main_bar_contacts);

                case 1:
                    if (pageSize == 4) {
                        return getActivity().getString(R.string.main_bar_work);
                    } else {
                        return getActivity().getString(R.string.main_bar_chats);
                    }
                case 2:
                    if (pageSize == 4) {
                        return getActivity().getString(R.string.main_bar_chats);
                    } else {
                        return getActivity().getString(R.string.profile_title);
                    }
                case 3:
                    return getActivity().getString(R.string.profile_title);

            }
        }

        @Override
        public int getPageIconResId(int position, Context context) {
            return -1;
        }
    }


    public ViewPager getViewPage() {
        return this.pager;
    }


}
