package com.bond.oncache.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

public class WSimpleTable extends FrameLayout {
  int back_color;
  int line_color;
  //ScrollView  scrollView;
  HorizontalScrollView scrollView;
  Papirus  papirus;
  //String  table_data[][]  =  null;
  //int  table_colors[]  =  null;
  TextView  table[][]  =  null;
  int  cols  =  0;
  int  rows  =  0;
  int  row_height  =  0;
  int  cols_width[]  =  null;
  int  table_width  =  SpecTheme.dpMaxEmojiKeyboard;
  int  table_height  =  SpecTheme.dpMaxEmojiKeyboard;

  public WSimpleTable(Context context, int back_color, int line_color)  {
    super(context);
    this.back_color  =  back_color;
    this.line_color  =  line_color;
    setBackgroundColor(back_color);
    setWillNotDraw(false);
    papirus  =  new Papirus(context);
    //scrollView  =  new ScrollView(context);
    scrollView  =  new HorizontalScrollView(context);
    scrollView.setScrollbarFadingEnabled(false);
    scrollView.addView(papirus, new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));
    addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT));
  }  // constructor


  void  setTable_data(String  table_data[][] ,  int  table_colors[] ) {
    papirus.removeAllViews();
    table  =  null;
    cols_width  =  null;
    rows  =  table_data.length;
    if  (rows  >  0  &&  rows == table_colors.length)  {
      cols  =  table_data[0].length;
      if (cols  >  0)  {
        table  =  new TextView[rows][cols];
        int  widthMeasureSpec  =  MeasureSpec.makeMeasureSpec(SpecTheme.dpMaxEmojiKeyboard, MeasureSpec.AT_MOST);
        int  heightMeasureSpec  =  MeasureSpec.makeMeasureSpec(SpecTheme.dpMaxEmojiKeyboard, MeasureSpec.AT_MOST);
        row_height  =  0;
        cols_width  =  new int[cols];
        for  (int  j  =  0;  j  <  cols;  ++j)  {  cols_width[j]  =  0;  }
        for  (int  i  =  0;  i  <  rows;  ++i)  {
          for  (int  j  =  0;  j  <  cols;  ++j)  {
            TextView txt  =  new TextView(SpecTheme.context);
            txt.setSingleLine(true);
            txt.setMaxLines(1);
            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.InfoTextSize);
            txt.setTextColor(table_colors[i]);
            txt.setText(table_data[i][j]);
            papirus.addView(txt);
            table[i][j]  =  txt;
            measureChildWithMargins(txt, widthMeasureSpec, 0,
                heightMeasureSpec, 0);
            int  w  =  txt.getMeasuredWidth() + SpecTheme.dpButtonPadding;
            int  h  =  txt.getMeasuredHeight() + SpecTheme.dpButtonPadding;
            if  (w  >  cols_width[j])  {  cols_width[j]  =  w;  }
            if  (h  >  row_height)  {  row_height  =  h;  }
          }  // for j
        }  // for i
        table_height  =  rows  *  row_height;
        table_width  =  0;
        for  (int  j  =  0;  j  <  cols;  ++j)  {  table_width  += cols_width[j]; }
      }
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event)  {
    return scrollView.onTouchEvent(event);
    //return super.onTouchEvent(event);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    scrollView.measure(widthMeasureSpec, heightMeasureSpec);
    int widht  =  scrollView.getMeasuredWidth();
    int height  =  scrollView.getMeasuredHeight();
    setMeasuredDimension(widht, height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int height = bottom - top;
    int widht = right - left;
    scrollView.layout(0, 0, widht, height);
  }

  private class Papirus extends FrameLayout {
    public Papirus(Context context) {
      super(context);
      setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int widht = MeasureSpec.getSize(widthMeasureSpec);

      if  (null  ==  table) {
        table_width = widht;
        table_height = SpecTheme.dpAvaIconSize;
      }

      setMeasuredDimension(table_width,  table_height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      if  (null  !=  table) {
        int  cur_top  =  0;
        for  (int  i  =  0;  i  <  rows;  ++i)  {
          int  next_top  =  cur_top  + row_height;
          int  cur_left  =  0;
          for  (int  j  =  0;  j  <  cols;  ++j)  {
            int  next_left  =  cur_left  + cols_width[j];
            table[i][j].layout(cur_left + SpecTheme.dpButtonPadding3,
                cur_top + SpecTheme.dpButtonPadding3, next_left,  next_top);
            cur_left  =  next_left;
          }  // for j
          cur_top  =  next_top;
        }  // for i
      } // if
    }

    @Override
    protected void onDraw(Canvas canvas) {
      //  Table out border:
      SpecTheme.paintLine.setColor(line_color);
      canvas.drawLine(0,0,  table_width, 0,  SpecTheme.paintLine );
      canvas.drawLine(0,table_height,  table_width, table_height,  SpecTheme.paintLine );
      canvas.drawLine(0,0,  0, table_height,  SpecTheme.paintLine );
      canvas.drawLine(table_width,0,  table_width, table_height,  SpecTheme.paintLine );
      if  (null  !=  table) {
        int  cur_top  =  0;
        for  (int  i  =  0;  i  <  rows;  ++i)  {
          cur_top  +=  row_height;
          canvas.drawLine(0,  cur_top,  table_width,  cur_top,  SpecTheme.paintLine );
        }  // for i
        int  cur_left  =  0;
        for  (int  j  =  0;  j  <  cols;  ++j)  {
          cur_left  += cols_width[j];
          canvas.drawLine(cur_left,  0,  cur_left,  table_height,  SpecTheme.paintLine );
        }  // for j
      }
    }  //  draw
  }  //  Papirus

}
