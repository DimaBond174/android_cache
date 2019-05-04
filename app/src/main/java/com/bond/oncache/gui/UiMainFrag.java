package com.bond.oncache.gui;

import android.content.Context;
import android.graphics.Canvas;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bond.oncache.R;
import com.bond.oncache.TestPresenter;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Map;

public class UiMainFrag extends UiFragment   {
  public static final String TAG = "UiMainFrag";

  ScrollView scrollView;
  ScrollView scrollViewTable;
  InnerPapirus innerPapirus;
  OuterPapirus outerPapirus;
  TextView txt_case_caption;
  ProgressBar progressBar;
  WJsonConfig wJsonConfig;
  TextView txt_result_caption;

  com.github.mikephil.charting.charts.LineChart chart;
  WSimpleTable  simple_table;

  @Override
  public void onDestroy() {
    removeAllViews();
    wJsonConfig = null;
    scrollView = null;
    scrollViewTable = null;
    innerPapirus = null;
    outerPapirus = null;
    txt_case_caption = null;
    txt_result_caption = null;
    progressBar = null;
  }


  @Override
  public String getTitle() {
    return TestPresenter.getRstring(R.string.strUiMainFrag);
  }

  public UiMainFrag(Context context, FragmentKey fragmentKey) {
    super(context, fragmentKey);

    setWillNotDraw(false);
    outerPapirus  =  new OuterPapirus(context);
    scrollView  =  new ScrollView(context);
    scrollView.setScrollbarFadingEnabled(false);
    scrollView.addView(outerPapirus, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));

    txt_case_caption = new TextView(context);
    txt_case_caption.setSingleLine(true);
    txt_case_caption.setMaxLines(1);
    txt_case_caption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.PTextSize);
    txt_case_caption.setTextColor(SpecTheme.PTextColor);
    txt_case_caption.setText(TestPresenter.getRstring(R.string.strCaseCaption));
    outerPapirus.addView(txt_case_caption);

    wJsonConfig = new WJsonConfig(context);
    outerPapirus.addView(wJsonConfig);

    txt_result_caption = new TextView(context);
    txt_result_caption.setSingleLine(true);
    txt_result_caption.setMaxLines(1);
    txt_result_caption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.PTextSize);
    txt_result_caption.setTextColor(SpecTheme.PTextColor);
    txt_result_caption.setText(TestPresenter.getRstring(R.string.strResultCaption));
    outerPapirus.addView(txt_result_caption);


    simple_table  =  new WSimpleTable(context,  SpecTheme.KeyBoardColor, SpecTheme.PTextColor);
    outerPapirus.addView(simple_table);

    innerPapirus  =  new InnerPapirus(context);
    outerPapirus.addView(innerPapirus);

    chart = new LineChart(context);
    chart.setDrawGridBackground(false);
    chart.getDescription().setEnabled(false);
    chart.setDrawBorders(false);

    chart.getAxisLeft().setEnabled(false);
    chart.getAxisRight().setDrawAxisLine(false);
    chart.getAxisRight().setDrawGridLines(false);
    chart.getXAxis().setDrawAxisLine(false);
    chart.getXAxis().setDrawGridLines(false);

    // enable touch gestures
    chart.setTouchEnabled(true);

    // enable scaling and dragging
    chart.setDragEnabled(true);
    chart.setScaleEnabled(true);

    // if disabled, scaling can be done on x- and y-axis separately
    chart.setPinchZoom(false);
    chart.setBackgroundColor(SpecTheme.KeyBoardColor);
    innerPapirus.addView(chart);

    addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT));
    //add progress bar

    progressBar  =  new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
    // Define a shape with rounded corners
    final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
    ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners,     null, null));
    // Sets the progressBar color
    pgDrawable.getPaint().setColor(SpecTheme.PForestGreenColorA);

    // Adds the drawable to your progressBar
    ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
    progressBar.setProgressDrawable(progress);

    // Sets a background to have the 3D effect
    Drawable draw_back_progress = SpecTheme.context.getResources()
        .getDrawable(android.R.drawable.progress_horizontal);
    draw_back_progress.setAlpha(77);
    progressBar.setBackgroundDrawable(draw_back_progress);

    progressBar.setVisibility(GONE);
    addView(progressBar, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT));

    setChartData();
  }

  void setChartData() {

    chart.resetTracking();

    ArrayList<ILineDataSet> dataSets = new ArrayList<>();

    for (int z = 0; z < 3; z++) {

      ArrayList<Entry> values = new ArrayList<>();

      for (int i = 0; i < 10; i++) {
        double val = (Math.random() * 100) + 3;
        values.add(new Entry(i, (float) val));
      }

      LineDataSet d = new LineDataSet(values, "DataSet " + (z + 1));
      d.setLineWidth(2.5f);
      d.setCircleRadius(4f);

      int color = SpecTheme.color_array[z % SpecTheme.color_array.length];
      d.setColor(color);
      d.setCircleColor(color);
      dataSets.add(d);
    }

    // make the first DataSet dashed
    ((LineDataSet) dataSets.get(0)).enableDashedLine(10, 10, 0);
    ((LineDataSet) dataSets.get(0)).setColors(ColorTemplate.VORDIPLOM_COLORS);
    ((LineDataSet) dataSets.get(0)).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

    LineData data = new LineData(dataSets);
    chart.setData(data);
    chart.setBackgroundColor(SpecTheme.KeyBoardColor);
    chart.invalidate();

    String  table_data[][] = new String[4][5];
    table_data[0][0] = "Test name";
    table_data[0][1] = "100";
    table_data[0][2] = "1000/10";
    table_data[0][3] = "10000/10";
    table_data[0][4] = "100000/10";
    table_data[1][0] = "FacebookCache";
    table_data[2][0] = "OnCacheSMRU";
    table_data[3][0] = "IntelCache";
    table_data[1][1] = "2307";
    table_data[2][1] = "119";
    table_data[3][1] = "12307";
    table_data[1][2] = "2307";
    table_data[2][2] = "174";
    table_data[3][2] = "2307";
    table_data[1][3] = "2307";
    table_data[2][3] = "78";
    table_data[3][3] = "2307";
    table_data[1][4] = "2307";
    table_data[2][4] = "12356";
    table_data[3][4] = "14";
    int  table_colors[]  =  new int[4];
    table_colors[0]  =  SpecTheme.PBlackColor;
    table_colors[1]  =  SpecTheme.color_array[0];
    table_colors[2]  =  SpecTheme.color_array[1];
    table_colors[3]  =  SpecTheme.color_array[2];

    simple_table.setTable_data(table_data,  table_colors);

  }  //  setChartData

  @Override
  public boolean onTouchEvent(MotionEvent event)  {
    simple_table.onTouchEvent(event);
    scrollView.onTouchEvent(event);
    chart.onTouchEvent(event);
    return super.onTouchEvent(event);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {

    simple_table.onTouchEvent(ev);
    scrollView.onTouchEvent(ev);
    chart.onTouchEvent(ev);

    return false;
  }



  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width  =  MeasureSpec.getSize(widthMeasureSpec);
    int height  =  MeasureSpec.getSize(heightMeasureSpec);
    if (progressBar.getVisibility() == VISIBLE) {
      progressBar.measure(
          MeasureSpec.makeMeasureSpec(
              width  -  SpecTheme.dpButtonImgSize, MeasureSpec.EXACTLY),
          MeasureSpec.makeMeasureSpec(
                SpecTheme.dpButtonImgSize, MeasureSpec.EXACTLY)
      );
    }
    scrollView.measure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(width, height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int height = bottom - top;
    int widht = right - left;
    scrollView.layout(0, 0, widht, height);
    if (progressBar.getVisibility() == VISIBLE) {
      progressBar.layout(SpecTheme.dpButtonImgSizeHalf,
          SpecTheme.dpButtonImgSizeHalf,
          SpecTheme.dpButtonImgSizeHalf + progressBar.getMeasuredWidth(),
          SpecTheme.dpButtonImgSizeHalf + progressBar.getMeasuredHeight());
    }
  }


  @Override
  public void onClick(View view) {

  }


  @Override
  public void onPause() {
    super.onPause();

  }

  @Override
  public void onResume() {
    super.onResume();
    wJsonConfig.setConfig();

  }

  @Override
  public void onStop() {
    super.onStop();
  }


  @Override
  public void onPresenterChange() {
    //TODO progress bar
    int progress  =  TestPresenter.getProgress();
    if (100 == progress) {
      //Data ready , draw
      progressBar.setVisibility(GONE);
      prepareChart();
    }  else if (0 == progress) {
      //No Data - hide table
      clearData();
      progressBar.setVisibility(GONE);
    }  else  {
      //Update progress bar
      updateProgress();
    }
  }

  void  prepareChart() {
    clearData();
    TJsonToCfg  cfg  =  TestPresenter.getConfig();
    if (null ==  cfg  ||  !cfg.is_valid)  return;
    try {
      ArrayList<ITester> testers = cfg.getTesters();
      Map<ITester, ArrayList<Entry>> results  =  cfg.getResults();
      int len_testers  =  testers.size();
      ArrayList<ILineDataSet> dataSets = new ArrayList<>();
      int len_results = 0;
      for (int  i  =  0;  i <  len_testers;  ++i)  {
        ITester cur_tester  =  testers.get(i);
        if (null == cur_tester)  continue;
        ArrayList<Entry> values  =  results.get(cur_tester);
        if (null == values)  continue;
        if (values.size()  >  len_results)  {  len_results =  values.size();  }
        LineDataSet d = new LineDataSet(values, cur_tester.get_algorithm_name());
        d.setLineWidth(2.5f);
        d.setCircleRadius(4f);
        int color = SpecTheme.color_array[i % SpecTheme.color_array.length];
        d.setColor(color);
        d.setCircleColor(color);
        dataSets.add(d);
      }
      LineData data = new LineData(dataSets);
      chart.setData(data);
      String  table_data[][] = new String[len_testers + 1][len_results + 1];
      int  table_colors[]  =  new int[len_testers + 1];
      table_colors[0]  = SpecTheme.PBlackColor;
      table_data[0][0] = "Test in micro sec";
      int  cur_items_mult  =  1;
      StringBuilder sb = new StringBuilder(32);
      for (int  j  =  0;  j <  len_results;  ++j)  {
        int  cur_max_items = StaticConsts.START_ITEMS  *  cur_items_mult;
        sb.append(cur_max_items);
        if  (cur_items_mult  >  1) {
          sb.append('/').append(cur_items_mult);
        }
        table_data[0][j + 1] = sb.toString();
        cur_items_mult *= 10;
        sb.setLength(0);
      }

      for (int  i  =  0;  i <  len_testers;  ++i)  {
        table_colors[i + 1]  = SpecTheme.color_array[i % SpecTheme.color_array.length];
        ITester cur_tester  =  testers.get(i);
        if (null == cur_tester)  continue;
        table_data[i + 1][0] = cur_tester.get_algorithm_name();
        ArrayList<Entry> values  =  results.get(cur_tester);
        if (null == values)  continue;
        for (int  j  =  0;  j <  len_results;  ++j)  {
          int  val  =  (int)values.get(j).getY();
          sb.setLength(0);
          sb.append(val);
          table_data[i + 1][j + 1] = sb.toString();
        }
      }
      simple_table.setTable_data(table_data,  table_colors);
      chart.invalidate();

    } catch (Exception e) {
      Log.e(TAG, "prepareChart() error: ", e);
    }
  }

  void  clearData() {
    chart.clear();
    simple_table.clear();
    chart.invalidate();
  }

  void  updateProgress() {
    progressBar.setVisibility(VISIBLE);
    progressBar.setProgress(TestPresenter.getProgress());
    progressBar.requestLayout();
  }

  private class OuterPapirus extends FrameLayout {
    public OuterPapirus(Context context) {
      super(context);
    }

    int chart_height = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int height = 0;
      int widht = MeasureSpec.getSize(widthMeasureSpec);
      int widhtSpec = MeasureSpec.makeMeasureSpec(widht - SpecTheme.dpButton2Padding, MeasureSpec.AT_MOST);
      int count = getChildCount();
      for (int i = 0; i < count; ++i) {
        View child = getChildAt(i);
        if (innerPapirus == child) continue;
        measureChildWithMargins(child, widhtSpec, 0,
            heightMeasureSpec, 0);
        height += child.getMeasuredHeight() + SpecTheme.dpButtonPadding;
      }
      chart_height  =  MeasureSpec.getSize(heightMeasureSpec) >> 2;
      if  (chart_height < SpecTheme.dpMaxEmojiKeyboard) {  chart_height = SpecTheme.dpMaxEmojiKeyboard;  }
      measureChildWithMargins(innerPapirus, widhtSpec, 0,
          MeasureSpec.makeMeasureSpec(chart_height, MeasureSpec.AT_MOST), 0);
      setMeasuredDimension(widht, height + chart_height + SpecTheme.dpButton2Padding);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      //super.onLayout(changed, left, top, right, bottom);
//      int centerX = (right - left)>>1;
//      View view = getChildAt(0);
//      int half = view.getMeasuredWidth()>>1;
//      view.layout(centerX-half,SpecTheme.dpButtonPadding,centerX+half,SpecTheme.dpButtonPadding+view.getMeasuredHeight());
//
      int widht = right - left - SpecTheme.dpButtonPadding;
      int count = getChildCount();
      int curTop = SpecTheme.dpButtonPadding;
      for (int i = 0; i < count; ++i) {
        View child = getChildAt(i);
        if (innerPapirus == child) continue;
        //Все один над другим ака Vertical Layout:
        int h = child.getMeasuredHeight();
        child.layout(SpecTheme.dpButtonPadding, curTop, widht, curTop + h);
        curTop += h + SpecTheme.dpButtonPadding;
      }
//      chart.mViewPortHandler.restrainViewPort(
//          SpecTheme.dpButtonPadding,
//          curTop,
//          widht,
//          curTop+100);
      innerPapirus.layout(SpecTheme.dpButtonPadding, curTop, widht, curTop + chart_height);

    }
  }  // OutrePapirus




  private class InnerPapirus extends FrameLayout {
    public InnerPapirus(Context context) {
      super(context);
      setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int widht = MeasureSpec.getSize(widthMeasureSpec);
      int height = MeasureSpec.getSize(heightMeasureSpec);
//      measureChildWithMargins(chart, MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY), 0,
//          MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY), 0);
      measureChildWithMargins(chart, widthMeasureSpec, 0,
          heightMeasureSpec, 0);
      setMeasuredDimension(widht, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      chart.layout(0, 0,
          right - left, bottom - top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      chart.draw(canvas);
    }
  }  //  InnerPapirus


}
