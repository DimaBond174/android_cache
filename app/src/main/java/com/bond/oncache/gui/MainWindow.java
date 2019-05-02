package com.bond.oncache.gui;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class MainWindow extends FrameLayout {
    boolean needRecreate = true;
    UiFragment curActiveFrag = null;

    public MainWindow(Context context) {
        super(context);
        //createViews(context);
    }

    public MainWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        //createViews(context);
    }

    public MainWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //createViews(context);
    }

    /* Проверка удаляется ли текущий фрагмент */
    public void checkDelCurFrag(UiFragment frag){
        if (curActiveFrag==frag) {
            removeCurFrag();
        }
    }

    private void removeCurFrag() {
        if (null!=curActiveFrag) {
            curActiveFrag.onPause();
            curActiveFrag.onStop();
            removeView(curActiveFrag);
            curActiveFrag = null;
        }
    }

    public void setCurActiveFrag(UiFragment frag){
        removeCurFrag();
        curActiveFrag=frag;
        addView(frag, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private void createViews(Context context)  {
        needRecreate = false;

    }

    public void init(Context context, DrawerLayout drawer)  {
        if (needRecreate) {
            createViews(context);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if  (null  !=  curActiveFrag)  {
            curActiveFrag.measure(widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        int widht = right-left;
        /* Внимание:  тут разные высоты для выкладывания виджета и для остальных: */
        int height = bottom-top;

        if (null  !=  curActiveFrag) {
            curActiveFrag.layout(0, 0,  widht,  height);
        }
    }

    public void onDestroy(){
        needRecreate = true;
        removeAllViews();
        curActiveFrag = null;
    }
}
