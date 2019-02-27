package tan.le.cartoonfilm.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class AppUtils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
