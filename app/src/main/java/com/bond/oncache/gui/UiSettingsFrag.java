package com.bond.oncache.gui;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bond.oncache.R;
import com.bond.oncache.TestPresenter;
import com.bond.oncache.cases.RegistryCases;
import com.bond.oncache.i.IParamEditor;
import com.bond.oncache.i.ITestCase;
import com.bond.oncache.objs.FileAdapter;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;
import com.bond.oncache.objs.TestParam;
import com.bond.oncache.testers.RegistryTesters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class UiSettingsFrag extends UiFragment   {
  ScrollView scrollView;
  Papirus papirus;
  Spinner spinner_cases;
  String[] cases_names;
  ArrayList<IParamEditor>  wparams  =  new ArrayList<>();
  ITestCase cur_test_case = null;
  TestParam[]  cur_test_params  =  null;

  @Override
  public void onDestroy() {
    removeAllViews();
    wparams = null;
    scrollView = null;
    cases_names = null;
    cur_test_case = null;
    cur_test_params = null;
    papirus  =  null;
  }


  @Override
  public String getTitle() {
    return TestPresenter.getRstring(R.string.strUiSettingsFrag);
  }

  public UiSettingsFrag(Context context, FragmentKey fragmentKey) {
    super(context, fragmentKey);

    cases_names = RegistryCases.get_names();
    LayoutInflater inflater = LayoutInflater.from(context);
    spinner_cases = (Spinner)inflater.inflate(R.layout.template_spinner, null);
    ArrayAdapter adapter = new ArrayAdapter<String>(context,
        android.R.layout.simple_spinner_dropdown_item, cases_names);
    spinner_cases.setAdapter(adapter);
    addView(spinner_cases, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));

    spinner_cases.setSelection(0);
    spinner_cases.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view,
                                 int position, long id) {
        setTestCase(position);
      }
      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });

    scrollView  =  new ScrollView(context);
    scrollView.setScrollbarFadingEnabled(false);

    papirus  =  new Papirus(context);
    scrollView.addView(papirus, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));

    addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT));
  }


  void  setTestCase(int  position)  {
    try {
      papirus.removeAllViews();
      wparams.clear();
      cur_test_case = RegistryCases.getCase(cases_names[position]);
      cur_test_params = cur_test_case.get_required_params();
      for (TestParam p : cur_test_params) {
        switch (p.type) {
          case TestParam.TYPE_BOOL:
            WEditBool wbool = new WEditBool(p, SpecTheme.context);
            papirus.addView(wbool, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
            wparams.add(wbool);
            break;
          case TestParam.TYPE_NUM:
          case TestParam.TYPE_ITEMS:
            WEditNumber wnum = new WEditNumber(p, SpecTheme.context, SpecTheme.SColor);
            papirus.addView(wnum, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
            wparams.add(wnum);
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      Log.e(getTAG(), "setTestCase error:", e);
    }
  }


  @Override
  public void prepareLocalMenu(Menu menu) {

  }

  @Override
  public boolean onSelectLocalMenu(int menu_item_id) {
    return false;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width  =  MeasureSpec.getSize(widthMeasureSpec);
    int height  =  MeasureSpec.getSize(heightMeasureSpec);
    measureChildWithMargins(spinner_cases, MeasureSpec.makeMeasureSpec(
        width - SpecTheme.dpButton2Padding, MeasureSpec.EXACTLY), 0,
        heightMeasureSpec, 0);

    int h = spinner_cases.getMeasuredHeight();
    if (width < height) {
      h += SpecTheme.dpButton2Padding
          + SpecTheme.dpButtonTouchSize;
    }

    measureChildWithMargins(scrollView, widthMeasureSpec, 0,
        MeasureSpec.makeMeasureSpec(
             height - h, MeasureSpec.EXACTLY), 0);

    setMeasuredDimension(width, height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int height  =  bottom - top;
    int width  =  right - left;
    int h = spinner_cases.getMeasuredHeight();
    //int w = spinner_cases.getMeasuredWidth();
    spinner_cases.layout(SpecTheme.dpButtonPadding, 0,
        width - SpecTheme.dpButtonPadding ,  h);
    //h += SpecTheme.dpButton2Padding;
    if (width < height) {
      height = height - SpecTheme.dpButtonTouchSize - SpecTheme.dpButton2Padding;
    }
    scrollView.layout(0, h,  width,  height);
  }


  @Override
  public void onClick(View view) {
    Log.w(getTAG(), "onClick();");
  }

  @Override
  public Drawable getFABicon() {
    return SpecTheme.ok_icon;
  }

  @Override
  public void onFABclick() {
    try {
      StringBuilder sb  =  new StringBuilder(1024);
      sb.append("{\"").append(StaticConsts.PARM_test_case).append("\":\"")
          .append(cur_test_case.get_case_name()).append("\"");
      for (IParamEditor w : wparams) {
        sb.append(",\"").append(w.get_param_name()).append("\":\"")
            .append(w.get_current_value()).append("\"");
      }
      sb.append(",\"testers\":[");
      boolean not_first  =  false;
      ArrayList<String> tlist  =  RegistryTesters.getListTesters(cur_test_case.get_key_type());
      for (String s :  tlist) {
        if (not_first) {  sb.append(",");  }
        not_first  =  true;
        sb.append("{\"tester\":\"")
            .append(s).append("\"")
            .append(",\"results\":[]}");
      }
      sb.append("]}"); //testers
    } catch (Exception e) {
      Log.e(getTAG(), "onFABclick()  error:", e);
    }
    TestPresenter.getGUInterface().goBack();

  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();

  }


  @Override
  public void onStop() {
    super.onStop();
  }


  @Override
  public void onPresenterChange() {

  }


  private class Papirus extends FrameLayout {
    public Papirus(Context context) {
      super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int height = 0;
      int widht = MeasureSpec.getSize(widthMeasureSpec);
      int widhtSpec = MeasureSpec.makeMeasureSpec(widht - SpecTheme.dpButton2Padding, MeasureSpec.AT_MOST);
      int count = getChildCount();
      for (int i = 0; i < count; ++i) {
        View child = getChildAt(i);
        measureChildWithMargins(child, widhtSpec, 0,
            heightMeasureSpec, 0);
        height += child.getMeasuredHeight() + SpecTheme.dpButtonPadding;
      }
      setMeasuredDimension(widht, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      int widht = right - left - SpecTheme.dpButtonPadding;
      int count = getChildCount();
      int curTop = SpecTheme.dpButtonPadding;
      for (int i = 0; i < count; ++i) {
        View child = getChildAt(i);
        int h = child.getMeasuredHeight();
        child.layout(SpecTheme.dpButtonPadding, curTop, widht, curTop + h);
        curTop += h + SpecTheme.dpButtonPadding;
      }
    }
  }  // OutrePapirus

}
