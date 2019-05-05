package com.bond.oncache.gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bond.oncache.R;
import com.bond.oncache.TestPresenter;
import com.bond.oncache.i.IParamEditor;
import com.bond.oncache.objs.TestParam;


public class WEditNumber extends FrameLayout  implements IParamEditor {

    ImageArrow leftArrow;
    ImageArrow rightArrow;
    TestParam param;

    TextView captionView;
    EditText numberEdit;
    int curValue;

    int curHeight = 0;
    int numberEditWidth = 0;

    public WEditNumber(final TestParam param, Context context, int  rColor) {
        super(context);
        this.param  = param;
        curValue  =  Integer.parseInt(param.value);

        Resources res = context.getResources();

        captionView = new TextView(context);
        //textView.setSingleLine(true);
        //textView.setMaxLines(1);
        captionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.STextSize);
        //textView.setEllipsize(TextUtils.TruncateAt.END);
        captionView.setText(param.caption);
        captionView.setTextColor(SpecTheme.PTextColor);
        //textView.setOnTouchListener(this);
        addView(captionView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        leftArrow = new ImageArrow(context, -1);
        leftArrow.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_thumb_down_black_24dp));
        leftArrow.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setOnTouchListener(this);
        addView(leftArrow, new LayoutParams(SpecTheme.dpButtonImgSize, SpecTheme.dpButtonImgSize));

        rightArrow = new ImageArrow(context, 1);
        rightArrow.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_thumb_up_black_24dp));
        rightArrow.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setOnTouchListener(this);
        addView(rightArrow, new LayoutParams(SpecTheme.dpButtonImgSize, SpecTheme.dpButtonImgSize));

        if (-1  !=  rColor) {
            leftArrow.setColorFilter(new LightingColorFilter( 0, rColor));
            rightArrow.setColorFilter(new LightingColorFilter( 0, rColor));
        }

        numberEdit = new EditText(context);
        numberEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        numberEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.STextSize);
        numberEdit.setSingleLine(true);
        numberEdit.setMaxLines(1);
        //textView.setEllipsize(TextUtils.TruncateAt.END);
        numberEdit.setTextColor(SpecTheme.PTextColor);
        addView(numberEdit, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        numberEdit.setText(String.valueOf(param.min_value));
        numberEdit.measure(MeasureSpec.makeMeasureSpec(300, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(300, MeasureSpec.AT_MOST));
        numberEditWidth = numberEdit.getMeasuredWidth() + SpecTheme.dpButtonPadding;
        //numberEdit.setText(String.valueOf(param.max_value));
        setWillNotDraw(false);
        //setClickable(true);
        //setOnTouchListener(this);
        //setOnClickListener(this);
        numberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int tmp = Integer.parseInt(s.toString());
                    if (tmp != curValue) {
                        if (tmp < param.min_value) {
                            tmp = param.min_value;
                        } else if (tmp > param.max_value) {
                            tmp = param.max_value;
                        }
                        setNumberEdit(tmp);
                        //numberEdit.setText(String.valueOf(tmp));
                    }
                } catch (Exception e) {
                    curValue = param.min_value;
                }
            }
        });
        numberEdit.setText(param.value);
    }

    public void setNumberEdit(int num) {
        curValue = num;
        numberEdit.setText(String.valueOf(num));
        numberEdit.setSelection(numberEdit.getText().length());
    }

    @Override
    public String get_current_value() {
        return String.valueOf(curValue);
    }

    @Override
    public String get_param_name() {
        return param.caption;
    }

    private void doNumChange(int dN) {
        int tmp = curValue;
        if (dN < 0)  {
            if (param.type  == TestParam.TYPE_ITEMS)  {
                tmp /= 10;
            }  else  {
                tmp -= 1;
            }
        }  else {
            if (param.type  == TestParam.TYPE_ITEMS)  {
                tmp *= 10;
            }  else  {
                tmp += 1;
            }
        }
        if (param.min_value <= tmp
              &&  tmp <= param.max_value)  {
            setNumberEdit(tmp);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int buttonWidht  = MeasureSpec.getSize(widthMeasureSpec);
//            int buttonHeight  = MeasureSpec.getSize(heightMeasureSpec);
        int widht = MeasureSpec.getSize(widthMeasureSpec);
        int text_width = widht
                - SpecTheme.dpButton2Padding //left border
                - SpecTheme.dpButton2Padding // right border
                - SpecTheme.dpButtonImgSize //leftArrow
                - SpecTheme.dpButtonImgSize //rightArrow
                - numberEditWidth;
                //- SpecTheme.dpButton2Padding; //spaces around number

        int text_width_widthSpec = MeasureSpec.makeMeasureSpec(text_width, MeasureSpec.AT_MOST);

        measureChildWithMargins(captionView, text_width_widthSpec, 0,
                heightMeasureSpec, 0);
        int height = captionView.getMeasuredHeight() + SpecTheme.dpButton2Padding;
        curHeight = height < SpecTheme.dpButtonTouchSize?
                SpecTheme.dpButtonTouchSize : height;
        /* Скажем наверх насколько мы большие */
        setMeasuredDimension(widht, curHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int r = SpecTheme.dpButton2Padding + captionView.getMeasuredWidth();
        captionView.layout(SpecTheme.dpButton2Padding,
                curHeight - SpecTheme.dpButtonPadding - captionView.getMeasuredHeight(),
                r,
                curHeight - SpecTheme.dpButtonPadding);
        int l = r +  SpecTheme.dpButtonPadding;
        r = l +  SpecTheme.dpButtonImgSize;
        int halfY = curHeight >> 1;
        int t = halfY - SpecTheme.dpButtonImgSizeHalf;
        int b = halfY + SpecTheme.dpButtonImgSizeHalf;
        leftArrow.layout(l, t, r, b);
        l = r ;//+  SpecTheme.dpButtonPadding;
        r = l + numberEdit.getMeasuredWidth();
        int half = numberEdit.getMeasuredHeight() >> 1;
        numberEdit.layout(l, halfY - half, r, halfY + half);
        l = r;// +  SpecTheme.dpButtonPadding;
        r = l +  SpecTheme.dpButtonImgSize;
        rightArrow.layout(l, t, r, b);
    }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
                    curHeight,
                    getWidth(),
                    curHeight, SpecTheme.paintLine );
        }


        private class ImageArrow extends AppCompatImageView implements View.OnTouchListener, OnClickListener {
            int dN; // onClick add to value
            long nextAddTime = 0L;
            public ImageArrow(Context context, int dN) {
                super(context);
                this.dN = dN;
                setOnTouchListener(this);
                setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                doNumChange(dN);
            }

            private void setHighlighted(int alpha) {
                if (alpha>0) {
                    int res = (SpecTheme.HiLightColor & 0x00ffffff) | (alpha << 24);
                    setBackgroundColor(res);
                } else {
                    setBackground(null);
                    //setBackgroundColor(backColor);
                }
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
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
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
        }//ImageArrow
}
