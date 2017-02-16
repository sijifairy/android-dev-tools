package com.lizhe.devtools.view;

import android.content.Context;
import android.util.AttributeSet;

public class BottomNavigationBar extends ColorTransitionView {

    public BottomNavigationBar(Context context) {
        this(context, null);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {

    }
}
