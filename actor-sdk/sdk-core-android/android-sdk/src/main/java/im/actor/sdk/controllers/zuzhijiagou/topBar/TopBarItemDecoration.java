package im.actor.sdk.controllers.zuzhijiagou.topBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import im.actor.sdk.R;

/**
 * Created by huchengjie on 2017/11/9.
 */

public class TopBarItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    private int mOrientation;

    public TopBarItemDecoration(Context context) {
//        final TypedArray a = context.obtainStyledAttributes(ATTRS);
//        mDivider = a.getDrawable(0);
//        a.recycle();

        mDivider = ContextCompat.getDrawable(context, R.drawable.topbar_icon_back);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizontal(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop() + 40;
        final int bottom = parent.getHeight() - parent.getPaddingBottom() - 40;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i == childCount - 1) {
                return;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }

}
