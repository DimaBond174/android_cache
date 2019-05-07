package com.bond.oncache.gui;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;
import com.bond.oncache.objs.TestParam;
import com.bond.oncache.testers.RegistryTesters;
import java.util.ArrayList;


public class UiSettingsFrag extends UiFragment   {
  ScrollView scrollView;
  Papirus main_papirus;
  Papirus papirus;
  Spinner spinner_cases;
  String[] cases_names;
  ArrayList<IParamEditor>  wparams  =  new ArrayList<>();
  ITestCase cur_test_case = null;
  TestParam[]  cur_test_params  =  null;
  RecyclerView recycler_all;
  RecyclerView recycler_selected;
  RVAdapter rv_adapter_all;
  RVAdapter rv_adapter_selected;
  Papirus2Recyclers papirus2Recyclers;

  @Override
  public void onDestroy() {
    removeAllViews();
    rv_adapter_all.onDestroy();
    rv_adapter_selected.onDestroy();
    papirus2Recyclers.onDestroy();
    main_papirus = null;
    wparams = null;
    scrollView = null;
    cases_names = null;
    cur_test_case = null;
    cur_test_params = null;
    papirus  =  null;
    recycler_all =  null;
    recycler_selected =  null;
    rv_adapter_all =  null;
    rv_adapter_selected =  null;
    papirus2Recyclers =  null;

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

    main_papirus  =  new Papirus(context);
    scrollView.addView(main_papirus, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));

    papirus  =  new Papirus(context);
    main_papirus.addView(papirus);

    //papirus2Recyclers
    rv_adapter_all = new RVAdapter();
    recycler_all = new RecyclerView(context);
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    recycler_all.setAdapter(rv_adapter_all);
    recycler_all.setLayoutManager(layoutManager);
    recycler_all.setVerticalScrollBarEnabled(true);

    rv_adapter_selected = new RVAdapter();
    recycler_selected = new RecyclerView(context);
    layoutManager = new LinearLayoutManager(context);
    recycler_selected.setAdapter(rv_adapter_selected);
    recycler_selected.setLayoutManager(layoutManager);
    recycler_selected.setVerticalScrollBarEnabled(true);

    rv_adapter_selected.setOther(rv_adapter_all);
    rv_adapter_all.setOther(rv_adapter_selected);

    papirus2Recyclers = new Papirus2Recyclers(context,
        recycler_all, recycler_selected,
        TestPresenter.getRstring(R.string.strAllTesters),
        TestPresenter.getRstring(R.string.strSelectedTesters));

    main_papirus.addView(papirus2Recyclers);

    addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT));

    spinner_cases.setSelection(0);
  }


  void  setTestCase(int  position)  {
    try {
      papirus.removeAllViews();
      wparams.clear();
      ITestCase test_case = RegistryCases.getCase(cases_names[position]);
      if (null == cur_test_case || (null != cur_test_case
          && test_case.get_key_type() != cur_test_case.get_key_type())) {
        rv_adapter_all.clearTesters();
        rv_adapter_selected.clearTesters();
        rv_adapter_selected.addTesters(RegistryTesters.getListTesters(test_case.get_key_type()));
      }
      cur_test_case = test_case;

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
      ArrayList<String> tlist  = rv_adapter_selected.getTesters();
      for (String s :  tlist) {
        if (not_first) {  sb.append(",");  }
        not_first  =  true;
        sb.append("{\"tester\":\"")
            .append(s).append("\"")
            .append(",\"results\":[]}");
      }
      sb.append("]}"); //testers
      TJsonToCfg cfg = new TJsonToCfg();
      cfg.setJSON(sb.toString());
      if (cfg.is_valid) {
        TestPresenter.setConfig(cfg);
        TestPresenter.getGUInterface().goBack();
      }  else {
        TestPresenter.getGUInterface().showMessage(
            TestPresenter.getRstring(R.string.strERRNoTesters)
        );
      }
    } catch (Exception e) {
      Log.e(getTAG(), "onFABclick()  error:", e);
    }


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


  public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
  {
    public void addTester (String tester) {
      testers.add(tester);
      notifyDataSetChanged();
    }

    public ArrayList<String> getTesters() {
      return testers;
    }

    public  void clearTesters() {
      testers.clear();
      notifyDataSetChanged();
    }

    public void addTesters(ArrayList<String> in_testers) {
      testers.clear();
      testers.addAll(in_testers);
      notifyDataSetChanged();
    }

    public  void setOther(RVAdapter in_other) {
      other = in_other;
    }

    public void onItemClick(int  id) {
      String str = testers.get(id);
      testers.remove(id);
      other.addTester(str);
      notifyDataSetChanged();
    }

    public void onDestroy() {
      other = null;
      testers = null;
    }

    RVAdapter other  =  null;
    ArrayList<String> testers  =  new ArrayList<>();

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ViewHolder hldr = ((ViewHolder) holder);
      hldr.my_view.cur_pos  =  position;
      hldr.my_view.setText(testers.get(position));
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
      public TextViewForRecycler my_view;
      public ViewHolder(TextViewForRecycler  v) {
        super(v);
        my_view = v;
        v.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            TextViewForRecycler my_view = (TextViewForRecycler)v;
            onItemClick(my_view.cur_pos);
          }
        });
      }
    }//viewHolder


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      TextViewForRecycler view = new TextViewForRecycler(SpecTheme.context);

      RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
          RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
      view.setLayoutParams(params);
      return new ViewHolder(view);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
      return testers.size();
    }
  }

  class TextViewForRecycler extends AppCompatTextView {
    public int cur_pos = 0;
    public TextViewForRecycler(Context  context) {
      super(context);
      setSingleLine(true);
      setMaxLines(1);
      setEllipsize(TextUtils.TruncateAt.END);
      setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.InfoTextSize);
      setPadding(SpecTheme.dpButtonPadding3, 0,
          SpecTheme.dpButtonPadding3, SpecTheme.dpButtonPadding);
      setTextColor(SpecTheme.PTextColor);
    }
  }


  private class Papirus2Recyclers extends FrameLayout {
    public Papirus2Recyclers(Context context, RecyclerView left, RecyclerView right,
          String left_caption,  String right_caption) {
      super(context);
      left_recycler  = left;
      right_recycler = right;
      left.setBackgroundColor(SpecTheme.PLightGrayColor);
      right.setBackgroundColor(SpecTheme.PLightGrayColor);
      addView(left,
          new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
      addView(right,
          new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

      txt_left = new TextView(context);
      txt_left.setSingleLine(true);
      txt_left.setMaxLines(1);
      txt_left.setEllipsize(TextUtils.TruncateAt.END);
      txt_left.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.PTextSize);
      txt_left.setTextColor(SpecTheme.PTextColor);
      txt_left.setText(left_caption);
      addView(txt_left, new LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT));

      txt_right = new TextView(context);
      txt_right.setSingleLine(true);
      txt_right.setMaxLines(1);
      txt_right.setEllipsize(TextUtils.TruncateAt.END);
      txt_right.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.PTextSize);
      txt_right.setTextColor(SpecTheme.PTextColor);
      txt_right.setText(right_caption);
      addView(txt_right, new LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT));
    }

    public void onDestroy() {
      left_recycler  = null;
      right_recycler = null;
      txt_left = null;
      txt_right = null;
    }

    RecyclerView left_recycler;
    RecyclerView right_recycler;
    TextView txt_left;
    TextView txt_right;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int heightSpec = MeasureSpec.makeMeasureSpec(SpecTheme.dpMaxEmojiKeyboard, MeasureSpec.EXACTLY);
      int widht = MeasureSpec.getSize(widthMeasureSpec);
      int widhtSpec = (widht >> 1) - SpecTheme.dpButton2Padding ;
      widhtSpec = MeasureSpec.makeMeasureSpec(widhtSpec, MeasureSpec.EXACTLY);
      measureChildWithMargins(left_recycler, widhtSpec, 0,
          heightSpec, 0);
      measureChildWithMargins(right_recycler, widhtSpec, 0,
          heightSpec, 0);
      measureChildWithMargins(txt_left, widhtSpec, 0,
          heightMeasureSpec, 0);
      measureChildWithMargins(txt_right, widhtSpec, 0,
          heightMeasureSpec, 0);

      setMeasuredDimension(widht,
          SpecTheme.dpMaxEmojiKeyboard + SpecTheme.dpTextPadding
              + txt_left.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      int widht = right - left;
      int w2 = (widht >> 1);
      int cut_top = txt_left.getMeasuredHeight();
      txt_left.layout(SpecTheme.dpButtonPadding, 0,
          SpecTheme.dpButtonPadding + txt_left.getMeasuredWidth(), cut_top);
      txt_right.layout(w2, 0, w2 + txt_right.getMeasuredWidth(), cut_top);
      ++cut_top;
      left_recycler.layout(0, cut_top, left_recycler.getMeasuredWidth(), SpecTheme.dpMaxEmojiKeyboard + cut_top);
      right_recycler.layout(w2, cut_top,
          w2 + right_recycler.getMeasuredWidth(), SpecTheme.dpMaxEmojiKeyboard + cut_top);
    }
  }  // Papirus2Recyclers



}
