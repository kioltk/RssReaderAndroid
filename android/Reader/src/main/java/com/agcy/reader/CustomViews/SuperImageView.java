package com.agcy.reader.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.agcy.reader.core.Imager;

/**
 * Created by kiolt_000 on 26.11.13.
 */
public class SuperImageView extends ImageView {
    public SuperImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SuperImageView(Context context) {
        super(context);
    }

    public void setImageUrl(String url){
        Imager.setImageUrl(url, this);
    }
}
