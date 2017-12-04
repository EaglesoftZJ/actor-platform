package im.actor.sdk.util;

import android.content.Context;
import android.content.res.Resources;

import im.actor.runtime.android.AndroidContext;

public class Screen {

    private static float density;
    private static float scaledDensity;

    public static int dp(float dp) {
        if (density == 0f)
            density = AndroidContext.getContext().getResources().getDisplayMetrics().density;

        return (int) (dp * density + .5f);
    }

    public static int sp(float sp) {
        if (scaledDensity == 0f)
            scaledDensity = AndroidContext.getContext().getResources().getDisplayMetrics().scaledDensity;

        return (int) (sp * scaledDensity + .5f);
    }

    public static int getWidth() {
        return AndroidContext.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight() {
        return AndroidContext.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getStatusBarHeight() {

        int result = 0;
        int resourceId = AndroidContext.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = AndroidContext.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavbarHeight() {
        if (hasNavigationBar()) {
            int resourceId = AndroidContext.getContext().getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return AndroidContext.getContext().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }

    public static boolean hasNavigationBar() {
        Resources resources = AndroidContext.getContext().getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return (id > 0) && resources.getBoolean(id);
    }

    public static float getDensity() {
        return density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}