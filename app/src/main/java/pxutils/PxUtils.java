package pxutils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by zht on 16/7/21.
 * <p/>
 * <p/>
 * dp  sp 转px的工具
 */
public class PxUtils {
    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(int sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
