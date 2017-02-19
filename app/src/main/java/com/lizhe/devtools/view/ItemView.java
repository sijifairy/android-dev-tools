package com.lizhe.devtools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lizhe.devtools.R;
import com.lizhe.devtools.utils.CommonUtils;

public class ItemView extends LinearLayout {

    private Drawable mIconDrawable;

    private String mIconText;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ItemStyle, defStyle, 0);
        mIconDrawable = a.getDrawable(R.styleable.ItemStyle_iconSrc);
        mIconText = a.getString(R.styleable.ItemStyle_iconText);

        ImageView icon = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(CommonUtils.pxFromDp(56), CommonUtils.pxFromDp(56));
        icon.setLayoutParams(params);
        icon.setImageDrawable(mIconDrawable);
        icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(icon);

        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.topMargin=CommonUtils.pxFromDp(4);
        tv.setLayoutParams(params1);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTextColor(Color.BLACK);
        tv.setText(mIconText);
        tv.setSingleLine();
        tv.setEllipsize(TextUtils.TruncateAt.END);
        addView(tv);
    }
}
