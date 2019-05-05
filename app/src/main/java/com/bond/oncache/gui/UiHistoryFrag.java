package com.bond.oncache.gui;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ScrollView;
import android.widget.TextView;
import com.bond.oncache.R;
import com.bond.oncache.TestPresenter;
import com.bond.oncache.objs.FileAdapter;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;
import java.io.File;
import java.util.Arrays;


public class UiHistoryFrag extends UiFragment   {

  RecyclerView recyclerView;
  RVAdapter rvAdapter;
  Drawable defFileIcon;

  ScrollView scrollView;
  TextView txt_cur_file;
  TextView txt_case_caption;
  WJsonConfig wJsonConfig;
  TJsonToCfg  cfg = null;
  File[]  files  =  null;

  @Override
  public void onDestroy() {
    removeAllViews();
    wJsonConfig = null;
    scrollView = null;
    defFileIcon = null;
    txt_case_caption = null;
    txt_cur_file = null;
    cfg = null;
  }


  @Override
  public String getTitle() {
    return TestPresenter.getRstring(R.string.strUiHistoryFrag);
  }

  public UiHistoryFrag(Context context, FragmentKey fragmentKey) {
    super(context, fragmentKey);

    setWillNotDraw(false);
    defFileIcon = ContextCompat.getDrawable(SpecTheme.context, R.drawable.ic_insert_chart_black_24dp);
    defFileIcon.setColorFilter(new LightingColorFilter( 0, SpecTheme.SColor));
    rvAdapter = new RVAdapter();
    recyclerView = new RecyclerView(context);
    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
    recyclerView.setAdapter(rvAdapter);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setVerticalScrollBarEnabled(true);
    addView(recyclerView,
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    wJsonConfig = new WJsonConfig(context);
    wJsonConfig.setBackgroundColor(SpecTheme.KeyBoardColor);

    scrollView  =  new ScrollView(context);
    scrollView.setScrollbarFadingEnabled(false);
    scrollView.addView(wJsonConfig, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.WRAP_CONTENT));

    addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT));

    txt_case_caption = new TextView(context);
    txt_case_caption.setSingleLine(true);
    txt_case_caption.setMaxLines(1);
    txt_case_caption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.PTextSize);
    txt_case_caption.setTextColor(SpecTheme.PTextColor);
    txt_case_caption.setText(TestPresenter.getRstring(R.string.strNoHistory));
    addView(txt_case_caption, new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));

    txt_cur_file = new TextView(context);
    txt_cur_file.setSingleLine(true);
    txt_cur_file.setMaxLines(1);
    txt_cur_file.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.PTextSize);
    txt_cur_file.setTextColor(SpecTheme.PTextColor);
    txt_cur_file.setText(TestPresenter.getRstring(R.string.strSelectAFile));
    addView(txt_cur_file, new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));
  }

  @Override
  public void prepareLocalMenu(Menu menu) {
    menu.add(1, StaticConsts.MENU_UiHistoryClear, Menu.NONE,
        R.string.strClearHistory);
  }

  @Override
  public boolean onSelectLocalMenu(int menu_item_id) {
    if (menu_item_id == StaticConsts.MENU_UiHistoryClear) {
      if (null != files) {
        for (File f : files) {
          f.delete();
        }
      }
      loadFileList();
      return true;
    }
    return false;
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width  =  MeasureSpec.getSize(widthMeasureSpec);
    int height  =  MeasureSpec.getSize(heightMeasureSpec);
    int txt_height = 0;
    if (txt_cur_file.getVisibility() == VISIBLE) {
      measureChildWithMargins(txt_cur_file, widthMeasureSpec, 0,
          heightMeasureSpec, 0);
      txt_height = txt_cur_file.getMeasuredHeight() + SpecTheme.dpButtonPadding;
    }

    int height3 = (height - txt_height) / 3;
    if (height3 > SpecTheme.dpMaxEmojiKeyboard) {
      height3 = SpecTheme.dpMaxEmojiKeyboard;
    }

    measureChildWithMargins(recyclerView, widthMeasureSpec, 0,
        MeasureSpec.makeMeasureSpec(
            height - height3 - txt_height, MeasureSpec.EXACTLY), 0);

    measureChildWithMargins(scrollView, widthMeasureSpec, 0,
        MeasureSpec.makeMeasureSpec(
             --height3, MeasureSpec.EXACTLY), 0);

    if (txt_case_caption.getVisibility() == VISIBLE) {
      measureChildWithMargins(txt_case_caption, widthMeasureSpec, 0,
          heightMeasureSpec, 0);
    }

    setMeasuredDimension(width, height);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int height  =  bottom - top;
    int width  =  right - left;
    int h_text  =  0;
    if (txt_cur_file.getVisibility() == VISIBLE) {
      h_text = txt_cur_file.getMeasuredHeight();
      txt_cur_file.layout(SpecTheme.dpButtonPadding,  0,  width,  h_text);
      h_text +=  SpecTheme.dpButtonPadding;
    }
    int h  =  scrollView.getMeasuredHeight();
    scrollView.layout(0, h_text,  width, h + h_text);
    recyclerView.layout(0, h_text + h + 1 ,  width,  height);

    if (txt_case_caption.getVisibility() == VISIBLE) {
      width  =  width >> 1;
      height = height >> 1;
      h = txt_case_caption.getMeasuredHeight() >> 1;
      int w = txt_case_caption.getMeasuredWidth() >> 1;
      txt_case_caption.layout(width - w,
          height - h,
          width + w,
          height + h);
    }
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
    if (null != cfg) {
      TestPresenter.setConfig(cfg);
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
    loadFileList();
  }

  void loadJSONtoWidget(File  file) {
    //faux loop
    do {
      if (null  ==  file)  break;
      txt_cur_file.setText(TestPresenter.getRstring(R.string.strFileName) +" "+file.getName());
      String json = FileAdapter.readFile(file);
      if (json.isEmpty())  break;
      cfg = new TJsonToCfg();
      cfg.setJSON(json);
      wJsonConfig.setConfig(cfg);
    } while (false);
  }

  @Override
  public void onStop() {
    super.onStop();
  }


  @Override
  public void onPresenterChange() {

  }


  public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
  {

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      //Log.d(TAG, "Element " + position + " set.");
      ViewHolder hldr = ((ViewHolder) holder);

      if (null==hldr.chatMsg) { return;}
      WIconWithText chatMsg = hldr.chatMsg;

      try {
        if (null != files) {
          File gInfo = files[position];
          chatMsg.dataPtr = gInfo;
          String str = gInfo.getName();
          long  date = Long.parseLong(str);
          chatMsg.setInfo(SpecTheme.formateDate(date));
        }
      } catch (Exception e) {}
    }


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
      public WIconWithText chatMsg;

      public ViewHolder(WIconWithText v) {
        super(v);
        chatMsg = v;
        v.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            WIconWithText WIconWithText = ((WIconWithText)v);
            //WIconWithText.setHighlighted(100);
            loadJSONtoWidget((File)WIconWithText.dataPtr);
          }
        });
      }
    }//viewHolder


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
      WIconWithText view =
          new WIconWithText(SpecTheme.context, null,
              SpecTheme.STextSize, defFileIcon, SpecTheme.PWhiteColor);
      RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
          RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
      view.setLayoutParams(params);
      return new ViewHolder(view);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
      int  re  =  0;
      if (null  !=  files) {
        re  =  files.length;
      }
      return re;
    }

  }

  void  loadFileList()  {
    files = FileAdapter.getFileList(StaticConsts.HistoryFolder, SpecTheme.context);
    if (null != files  &&  files.length  > 0) {
      Arrays.sort(files);
      txt_case_caption.setVisibility(GONE);
      txt_cur_file.setVisibility(VISIBLE);
      loadJSONtoWidget(files[0]);
    } else {
      txt_cur_file.setVisibility(GONE);
      txt_case_caption.setVisibility(VISIBLE);
      wJsonConfig.clear();
    }
    rvAdapter.notifyDataSetChanged();
    requestLayout();
  }

}
