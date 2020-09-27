package im.actor.sdk.controllers.message_xzrz;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.actor.core.viewmodel.MessageXzrz;
import im.actor.core.viewmodel.MessageXzrzCallBack;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.BaseFragment;

import static im.actor.sdk.util.ActorSDKMessenger.messenger;

/**
 * Created by huchengjie on 2017/9/21.
 */

public class MessageFragment extends BaseFragment {

    RecyclerView recyclerView;
    List<MessageXzrz> xzrzsMes;
    TextView emptyView;
    MessageHandler handler = new MessageHandler();
    long messageid;

    public MessageFragment() {
        setRootFragment(true);
        setHomeAsUp(true);
        setTitle("下载日志");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.activity_message_history, container, false);
        messageid = getArguments().getLong("messageid");
        xzrzsMes = new ArrayList<>();
        recyclerView = (RecyclerView) res.findViewById(R.id.messageRecView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new XzrzAdapter(xzrzsMes, getContext()));

        emptyView = (TextView) res.findViewById(R.id.empty);

        // 设置item增加和删除时的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.sendAccessibilityEventUnchecked();

        messenger().getXzrz(ActorSDK.getWebServiceUri(getContext()) +
                ":8012/ActorServices-Maven/services/ActorService", messageid, new MessageXzrzCallBack() {

            @Override
            public void queryResponseCallBack(List<MessageXzrz> xzrzs) {
                xzrzsMes = xzrzs;
                handler.sendEmptyMessage(0);
            }
        });

        return res;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void setMargin(View view){
        int height = getSupportActionBar().getHeight();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(view.getLayoutParams());
        lp.setMargins(10, height, 10, 0);
        view.setLayoutParams(lp);
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setMargin(emptyView);
            setMargin(recyclerView);
            if (xzrzsMes.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                XzrzAdapter adapter = (XzrzAdapter) recyclerView.getAdapter();
                adapter.getmList().clear();
                adapter.setmList(xzrzsMes);
                adapter.notifyDataSetChanged();
            }

        }
    }

    class XzrzAdapter extends RecyclerView.Adapter {
        List<MessageXzrz> mList;
        Context context;

        public XzrzAdapter(List<MessageXzrz> mList, Context context) {
            this.mList = mList;
            this.context = context;
        }

        @Override
        public XzrzAdapter.MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            XzrzAdapter.MessageHolder holder = new XzrzAdapter.MessageHolder(LayoutInflater.from(
                    context).inflate(R.layout.message_holder_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            XzrzAdapter.MessageHolder holderMsg = (XzrzAdapter.MessageHolder) holder;
            holderMsg.name.setText(mList.get(position).getUserName());
            holderMsg.name.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryColor());

            holderMsg.time.setText("于" + mList.get(position).getTime() + "下载");
            holderMsg.time.setTextColor(ActorSDK.sharedActor().style.getTextPrimaryColor());


            View separator = new View(context);
            separator.setBackgroundColor(ActorSDK.sharedActor().style.getContactDividerColor());
            {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        context.getResources().getDimensionPixelSize(R.dimen.div_size));
                layoutParams.gravity = Gravity.BOTTOM;
//                layoutParams.leftMargin = Screen.dp(0);
                holderMsg.message_endFray.addView(separator, layoutParams);
            }

            if (position == getItemCount() - 1) {
                separator.setVisibility(View.GONE);
            } else {
                separator.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        class MessageHolder extends RecyclerView.ViewHolder {

            TextView name;
            TextView time;
            FrameLayout message_endFray;

            public MessageHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.message_name);
                time = (TextView) view.findViewById(R.id.message_time);
                message_endFray = (FrameLayout) view.findViewById(R.id.message_endFray);
            }
        }

        public List<MessageXzrz> getmList() {
            return mList;
        }

        public void setmList(List<MessageXzrz> mList) {
            this.mList = mList;
        }
    }


}
