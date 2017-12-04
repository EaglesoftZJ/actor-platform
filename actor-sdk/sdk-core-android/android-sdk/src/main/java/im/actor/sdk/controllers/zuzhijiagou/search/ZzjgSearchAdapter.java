package im.actor.sdk.controllers.zuzhijiagou.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import im.actor.core.entity.Message;
import im.actor.core.entity.SearchEntity;
import im.actor.runtime.android.view.BindedListAdapter;
import im.actor.runtime.generic.mvvm.BindedDisplayList;
import im.actor.sdk.ActorSDK;
import im.actor.sdk.R;
import im.actor.sdk.controllers.conversation.messages.content.preprocessor.PreprocessedList;
import im.actor.sdk.controllers.search.GlobalSearchBaseFragment;
import im.actor.sdk.util.Screen;
import im.actor.sdk.view.adapters.OnItemClickedListener;

public class ZzjgSearchAdapter extends  RecyclerView.Adapter<ZzjgSearchHolder> {

    private Context context;
    private String query;
    private OnItemClickedListener<ZzjgSearchEntity> onItemClickedListener;
    private List<ZzjgSearchEntity> list;

    public ZzjgSearchAdapter(Context context, List<ZzjgSearchEntity> list,
                             OnItemClickedListener<ZzjgSearchEntity> onItemClickedListener) {
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
        this.list = list;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ZzjgSearchHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 1:
                return new SearchHolderEx(context, onItemClickedListener);
            default:
            case 0:
                return new ZzjgSearchHolder(context, onItemClickedListener);
        }
    }

    private ZzjgSearchEntity getItem(int pos){
        return this.list.get(pos);
    }

    @Override
    public void onBindViewHolder(ZzjgSearchHolder holder, int position) {
        holder.bind(getItem(position),query,position ==  getItemCount() - 1);
    }


//    @Override
//    public void onBindViewHolder(ZzjgSearchHolder dialogHolder, int index, SearchEntity item) {
//        dialogHolder.bind(item, query, index == getItemCount() - 1);
//    }

    public class SearchHolderEx extends ZzjgSearchHolder {
        public SearchHolderEx(Context context, OnItemClickedListener<ZzjgSearchEntity> clickedListener) {
            super(context, clickedListener);
        }

        @Override
        protected void init(Context context, OnItemClickedListener<ZzjgSearchEntity> clickedListener) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            itemView.setBackgroundColor(ActorSDK.sharedActor().style.getBackyardBackgroundColor());
            TextView globalSearchTitle = new TextView(context);
            globalSearchTitle.setText(R.string.main_search_global_header);
            globalSearchTitle.setTextSize(16);
            globalSearchTitle.setPadding(Screen.dp(12), Screen.dp(8), 0, Screen.dp(8));
            globalSearchTitle.setBackgroundColor(ActorSDK.sharedActor().style.getBackyardBackgroundColor());
            globalSearchTitle.setTextColor(ActorSDK.sharedActor().style.getTextSecondaryColor());
            ((ViewGroup) itemView).addView(globalSearchTitle);
        }

        @Override
        public void bind(ZzjgSearchEntity entity, String query, boolean isLast) {
        }
    }

    public List<ZzjgSearchEntity> getList() {
        return list;
    }

    public void setList(List<ZzjgSearchEntity> list) {
        this.list = list;
    }
}
