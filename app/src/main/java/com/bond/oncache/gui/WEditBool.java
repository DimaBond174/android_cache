package com.bond.oncache.gui;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import com.bond.oncache.R;
import com.bond.oncache.i.IParamEditor;
import com.bond.oncache.objs.TestParam;


public class WEditBool extends FrameLayout  implements IParamEditor {

    CheckBox chkBox;
    TestParam param;

    //public WEditNumber(int in_minNumber, int in_maxNumber, Context context, int rCaption, float rStringSize, int rColor, UiFragment parentFrag) {
    public WEditBool(final TestParam param, Context context) {
        super(context);
        this.param  = param;
        boolean curValue  =  1 == Integer.parseInt(param.value);
        LayoutInflater inflater = LayoutInflater.from(context);
        chkBox = (CheckBox)inflater.inflate(R.layout.template_checkbox, null);
        chkBox.setChecked(curValue);
        chkBox.setTextColor(SpecTheme.PTextColor);
        chkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SpecTheme.STextSize);
        chkBox.setText(param.caption);
        addView(chkBox, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT));
    }

    @Override
    public String get_current_value() {
        if  (chkBox.isChecked()) {
            return "1";
        }
        return "0";
    }

    @Override
    public String get_param_name() {
        return param.caption;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(chkBox, widthMeasureSpec, 0,
            heightMeasureSpec, 0);
        int width = chkBox.getMeasuredWidth() + SpecTheme.dpButton2Padding;
        int height = chkBox.getMeasuredHeight() + SpecTheme.dpButton2Padding;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = chkBox.getMeasuredWidth();
        int height = chkBox.getMeasuredHeight();
        chkBox.layout(SpecTheme.dpButtonPadding, SpecTheme.dpButtonPadding,
            SpecTheme.dpButtonPadding + width, SpecTheme.dpButtonPadding + height);
    }

//        @Override
//        protected void onDraw(Canvas canvas) {
//            canvas.drawLine(SpecTheme.dpButton2Padding + SpecTheme.dpButtonImgSize,
//                    curHeight,
//                    getWidth(),
//                    curHeight, SpecTheme.paintLine );
//        }

}
