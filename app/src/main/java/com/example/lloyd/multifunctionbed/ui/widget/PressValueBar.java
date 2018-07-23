package com.example.lloyd.multifunctionbed.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.lloyd.multifunctionbed.R;

public class PressValueBar extends LinearLayout {
    private int mValue;
    private LinearLayout llContainer;

    public PressValueBar(Context context) {
        super(context);
    }

    public PressValueBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        View view = View.inflate(context, R.layout.layout_press_bar, this);
        llContainer = view.findViewById(R.id.ll_container);
    }

    public void setValue(int value) {
        mValue = value;
        for (int i = llContainer.getChildCount() - 1; i >= 0; i--) {
            if (llContainer.getChildCount() - i <= value) {
                llContainer.getChildAt(i).setBackgroundColor(Color.parseColor("#E8973C"));
            } else {
                llContainer.getChildAt(i).setBackgroundColor(Color.parseColor("#F6E39E"));
            }
        }
    }

    public int getValue() {
        return mValue;
    }
}
