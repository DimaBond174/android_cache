package com.bond.oncache.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.bond.oncache.TestPresenter;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;

import java.util.ArrayList;

public class WJsonConfig extends FrameLayout {
  public static final String TAG = "WJsonConfig";
  final ArrayList<TextView> json_params = new ArrayList<>();
  Papirus  papirus;
  int  papirus_height = 0;

  public WJsonConfig(Context context)  {
    super(context);
    papirus  =  new Papirus(context);
    addView(papirus, new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));
  }  // constructor

  void clear() {
    papirus.removeAllViews();
    json_params.clear();
    requestLayout();
  }

  void  setConfig() {
    TJsonToCfg cfg  = TestPresenter.getConfig();
    papirus.removeAllViews();
    json_params.clear();
    papirus_height = 0;
    if (null != cfg  && cfg.is_valid) {
      try {
        int  widthMeasureSpec  =  MeasureSpec.makeMeasureSpec(SpecTheme.dpMaxEmojiKeyboard, MeasureSpec.AT_MOST);
        int  heightMeasureSpec  =  MeasureSpec.makeMeasureSpec(SpecTheme.dpMaxEmojiKeyboard, MeasureSpec.AT_MOST);
        for (String param : StaticConsts.json_params) {
          String str = cfg.getParam(param);
          if  (null != str)  {
            TextView txt  =  new TextView(SpecTheme.context);
            txt.setSingleLine(true);
            txt.setMaxLines(1);
            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.InfoTextSize);
            txt.setTextColor(SpecTheme.PDimGrayColor);
            txt.setText(param  + " : " + str);
            papirus.addView(txt);
            json_params.add(txt);
            measureChildWithMargins(txt, widthMeasureSpec, 0,
                heightMeasureSpec, 0);
            papirus_height  +=  txt.getMeasuredHeight() + SpecTheme.dpButtonPadding;
          }
        }
      } catch ( Exception e) {
        Log.e(TAG, "setConfig() error:", e);
      }
    }
    requestLayout();
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widht  =  MeasureSpec.getSize(widthMeasureSpec);
    setMeasuredDimension(widht, papirus_height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    papirus.layout(0, 0, right - left, bottom - top);
  }

  private class Papirus extends FrameLayout {
    public Papirus(Context context) {
      super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//      int widht = MeasureSpec.getSize(widthMeasureSpec);
//
//      if  (null  ==  table) {
//        table_width = widht;
//        table_height = SpecTheme.dpAvaIconSize;
//      }
//
//      setMeasuredDimension(table_width,  table_height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      int widht = right - left - SpecTheme.dpButtonPadding;
      int count = getChildCount();
      int curTop = 0;
      for (int i = 0; i < count; ++i) {
        View child = getChildAt(i);
        int h = child.getMeasuredHeight();
        child.layout(SpecTheme.dpButtonPadding, curTop, widht, curTop + h);
        curTop += h + SpecTheme.dpButtonPadding;
      }
    }

  }  //  Papirus

}
