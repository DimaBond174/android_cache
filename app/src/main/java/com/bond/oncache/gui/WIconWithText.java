package com.bond.oncache.gui;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.oncache.TestPresenter;


public class WIconWithText extends FrameLayout  {

    public ImageView imageView;
    public TextView textView;
    public Object dataPtr = null; // Any Data

    int curHeight = 0;
    Drawable defDrawable;
    int backColor;

    public WIconWithText(Context context, String text, float rStringSize, Drawable defDrawable, int backColor) {
        super(context);
        this.defDrawable=defDrawable;
        this.backColor  = backColor;

        imageView = new ImageView(context);
        //imageView.setImageDrawable(res.getDrawable(rIcon));
        imageView.setImageDrawable(defDrawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setOnTouchListener(this);
        addView(imageView, new LayoutParams(SpecTheme.dpButtonImgSize, SpecTheme.dpButtonImgSize));

        textView = new TextView(context);
        textView.setSingleLine(true);
        textView.setMaxLines(1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, rStringSize);
        //textView.setEllipsize(TextUtils.TruncateAt.END);
        if (null!=text) {
            textView.setText(text);
        }
        textView.setTextColor(SpecTheme.PTextColor);
        //textView.setOnTouchListener(this);
        addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


        setWillNotDraw(false);
        //setClickable(true);
        //setOnTouchListener(this);
        //setOnClickListener(this);
        setBackgroundColor(backColor);
    }


    public void setInfo(String text)  {
        if (null  ==  text) {
            textView.setText("");
        } else {
            textView.setText(text);
        }

        invalidate();
    }


    public void setInfoD(String text, Drawable drawable) {
        if (null==text) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
        imageView.setImageDrawable(drawable);
        invalidate();
    }

  volatile int  hilight_lvl  =  100;
  final Runnable apply_hilight = new Runnable() {
    @Override
    public void run() {
        try {
            if (hilight_lvl > 0) {
                --hilight_lvl;
                setHighlighted(hilight_lvl);
            }
            if (hilight_lvl > 0) {
                TestPresenter.runOnGUIthreadDelay(apply_hilight, 30);
            }
        } catch (Exception e) {}
      }
    };




    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hilight_lvl = 100;
                apply_hilight.run();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                hilight_lvl = 0;
                setHighlighted(0);
                break;
        }
        return false;
    }

    public void setHighlighted(int alpha) {
        if (alpha>0) {
            int res = (SpecTheme.HiLightColor & 0x00ffffff) | (alpha << 24);
            setBackgroundColor(res);
        } else {
            //setBackground(null);
            setBackgroundColor(backColor);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int buttonWidht  = MeasureSpec.getSize(widthMeasureSpec);
//            int buttonHeight  = MeasureSpec.getSize(heightMeasureSpec);
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        int text_width = widht - SpecTheme.dpButton2Padding - SpecTheme.dpButtonImgSize;
        int text_width_widthSpec = MeasureSpec.makeMeasureSpec(text_width, MeasureSpec.AT_MOST);

        measureChildWithMargins(textView, text_width_widthSpec, 0,
                heightMeasureSpec, 0);
        int height = textView.getMeasuredHeight();
        curHeight = height < SpecTheme.dpButtonTouchSize?
                SpecTheme.dpButtonTouchSize : height;
        /* Скажем наверх насколько мы большие */
        setMeasuredDimension(widht, curHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int buttonWidht = right-left;
        imageView.layout(SpecTheme.dpButtonPadding,
                curHeight - SpecTheme.dpButtonPadding - SpecTheme.dpButtonImgSize,
                SpecTheme.dpButtonPadding + SpecTheme.dpButtonImgSize,
                curHeight - SpecTheme.dpButtonPadding);

        textView.layout(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
                curHeight - SpecTheme.dpButtonPadding - textView.getMeasuredHeight(),
                buttonWidht,
                curHeight - SpecTheme.dpButtonPadding);
    }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
                    curHeight,
                    getWidth(),
                    curHeight, SpecTheme.paintLine );
        }

}
