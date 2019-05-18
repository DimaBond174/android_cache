package com.bond.oncache.gui;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.oncache.R;
import com.bond.oncache.TestPresenter;
import com.bond.oncache.i.IParamEditor;
import com.bond.oncache.objs.FileAdapter;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TestParam;


public class WChooseFile extends FrameLayout implements IParamEditor {

  TextView captionView;
  ImageView imageView;
  TextView textView;
  TestParam param;
  int curHeight  =  0;
  AlertDialog  curAlertDialog  =  null;


    public WChooseFile(final TestParam param, Context context) {
        super(context);
        this.param  = param;
        captionView = new TextView(context);
        captionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.SmallTextSize);
        captionView.setText(param.caption);
        captionView.setTextColor(SpecTheme.PTextColor);
        addView(captionView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        imageView = new ImageView(context);
        //imageView.setImageDrawable(res.getDrawable(rIcon));
        imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.folder_btn));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setOnTouchListener(this);
        addView(imageView, new LayoutParams(SpecTheme.dpButtonImgSize, SpecTheme.dpButtonImgSize));

        textView = new TextView(context);
        textView.setSingleLine(true);
        textView.setMaxLines(1);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.STextSize);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(param.value);
        textView.setTextColor(SpecTheme.SDarkColor);
        //textView.setOnTouchListener(this);
        addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            if (FileAdapter.checkStoragePermissionGranted(
                (AppCompatActivity) SpecTheme.context))  {
              openLocalFileChooser();
            }
          }
        });

        setWillNotDraw(false);
        //setClickable(true);
        //setOnTouchListener(this);
        //setOnClickListener(this);
        //setBackgroundColor(backColor);
    }

    @Override
    public String get_current_value() {
        return param.value;
    }

    @Override
    public String get_param_name() {
        return param.caption;
    }

  @Override
  public void set_current_value(String val) {
    param.value  =  val;
    textView.setText(val);
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

    private void openLocalFileChooser() {
      String dir  = SpecTheme.context.getFilesDir().getPath()  +  "/specnet";

//      if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//        File storageDir = null;
//
//        storageDir =Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOCUMENTS
//        );
//        dir=storageDir.toString();
//      } else {
//        dir=UiRoot.settings.getDsPublic();
//      }

      FileChooserDialog fl = new FileChooserDialog(SpecTheme.context, dir);
      curAlertDialog  =  fl.setFileChooserDialogListener(
          new FileChooserDialog.FileChooserDialogListener(){
             @Override
             public void onSelectedFile(String fileName) {
               set_current_value(FileAdapter.getFileName(fileName));
             }
          }
      ) //setFileChooserDialogListener
      .setOnFolderUpListener(new FileChooserDialog.OnFolderUpListener() {
        @Override
        public boolean onFolderUP(String curPath) {
          if (null != curAlertDialog)  {
            curAlertDialog.dismiss();
            curAlertDialog = null;
          }
          openAndroidFileChooser();
          return false;
        }
      }).show();
    }


    private void openAndroidFileChooser() {
      Intent intent = new Intent();
      intent.setAction(Intent.ACTION_GET_CONTENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("*/*");
      AppCompatActivity activity = (AppCompatActivity) SpecTheme.context;
      activity.startActivityForResult(intent, StaticConsts.RQS_GET_CONTENT);
    }



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
            setBackground(null);
            //setBackgroundColor(backColor);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        int t = widht - SpecTheme.dpButton2Padding - SpecTheme.dpButtonImgSize;
        int text_width_widthSpec = MeasureSpec.makeMeasureSpec(t, MeasureSpec.AT_MOST);

        measureChildWithMargins(captionView, text_width_widthSpec, 0,
                heightMeasureSpec, 0);
        measureChildWithMargins(textView, text_width_widthSpec, 0,
            heightMeasureSpec, 0);
        measureChildWithMargins(imageView, SpecTheme.dpButtonImgSizeSpec, 0,
            SpecTheme.dpButtonImgSizeSpec, 0);
        t = captionView.getMeasuredHeight() + textView.getMeasuredHeight() + SpecTheme.dpButtonPadding;
        curHeight = SpecTheme.dpButtonTouchSize > t ? SpecTheme.dpButtonTouchSize : t;

        setMeasuredDimension(widht,  curHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)  {
        int h =  captionView.getMeasuredHeight();
        captionView.layout(SpecTheme.dpButtonTouchSize,  0,
            SpecTheme.dpButtonTouchSize + captionView.getMeasuredWidth(),
           h);
        int w = SpecTheme.dpButtonPadding + SpecTheme.dpButtonImgSize;
        imageView.layout(SpecTheme.dpButtonPadding,
            SpecTheme.dpButtonPadding,  w,  w);
        w += SpecTheme.dpButtonPadding;

        textView.layout(w,
                curHeight - SpecTheme.dpButtonPadding - textView.getMeasuredHeight(),
                w + textView.getMeasuredWidth(),
                h + curHeight - SpecTheme.dpButtonPadding);
    }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
                    curHeight,
                    getWidth(),
                    curHeight, SpecTheme.paintLine );
        }

}
