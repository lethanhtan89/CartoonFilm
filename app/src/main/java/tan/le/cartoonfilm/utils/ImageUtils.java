package tan.le.cartoonfilm.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;

import tan.le.cartoonfilm.R;

public class ImageUtils {
    public static void loadImage(String url, ImageView imageView, Context context) {
        CircularProgressDrawable drawable = new CircularProgressDrawable(context);
        drawable.setStrokeWidth(5f);
        drawable.setCenterRadius(30f);
        drawable.setColorFilter(context.getResources().getColor(R.color.colorWhite1), PorterDuff.Mode.SRC_IN);
        drawable.start();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(Constant.URL_IMG.concat(url))
                    .apply(RequestOptions.placeholderOf(drawable).error(R.drawable.img_null))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(imageView);
        }
    }
}
