package im.actor.sdk.controllers.zuzhijiagou.topBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import im.actor.sdk.R;

/**
 * Created by huchengjie on 2017/11/9.
 */

public class TopBarAdapter extends RecyclerView.Adapter<TopBarAdapter.TopBarViewHolder> {
    Context context;
    List<TreeBarBean> bars;

    public TopBarAdapter(Context context, List<TreeBarBean> bars) {
        this.bars = bars;
        this.context = context;
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
//        void onItemLongClick(View view , int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public TopBarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TopBarViewHolder holder = new TopBarViewHolder(LayoutInflater.from(context).
                inflate(R.layout.fragment_zuzhijiegou_topbar, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(TopBarViewHolder holder, int position) {
        holder.barTextView.setText(bars.get(position).getText());
        int pos = holder.getLayoutPosition();
//        System.out.println("iGem:" + position + ":" + pos);
        if (position == bars.size() - 1 && !bars.get(position).getText().equals("单位")) {
            holder.barTextView.setTextColor(Color.parseColor("#8E8E8E"));
        } else {
            holder.barTextView.setTextColor(Color.parseColor("#ff33b5e5"));
        }
        if (mOnItemClickLitener != null) {
            holder.barTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.barTextView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.bars.size();
    }

    class TopBarViewHolder extends RecyclerView.ViewHolder {
        TextView barTextView;

        public TopBarViewHolder(View view) {
            super(view);
            barTextView = (TextView) view.findViewById(R.id.zuzhijiagou_barTextView);
        }
    }

    public List<TreeBarBean> getBars() {
        return bars;
    }

    public void setBars(List<TreeBarBean> bars) {
        this.bars = bars;
    }
}
