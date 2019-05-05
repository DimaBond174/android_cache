package com.bond.oncache.gui;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import com.bond.oncache.TestPresenter;
import com.bond.oncache.i.IView;


public abstract class UiFragment extends FrameLayout implements View.OnClickListener
{
    public boolean  isDestroyed  =  false;
    protected volatile boolean guiOnScreen  =  false;

    protected FragmentKey fragmentKey;

    public UiFragment(Context context, FragmentKey fragmentKey) {
        super(context);
        this.fragmentKey = fragmentKey;
    }

    public String getTAG() {
      return fragmentKey.fragTAG;
    }

    public FragmentKey getFragmentKey() {
        return fragmentKey;
    }

    public abstract String getTitle();

    public abstract void onPresenterChange();

    public abstract void prepareLocalMenu(Menu menu);
    public abstract boolean onSelectLocalMenu(int menu_item_id);
    public abstract Drawable getFABicon();
    public abstract void onFABclick();

//    public abstract void setAnswerMessage(MsgTemplate msg);
//
//    /* Демонстрация сообщений об ошибках */
//    public void showErrorDlg(String title, String errMsg){
//        //UiRoot.getInstance().getForDialogCtx().getResources()
//        //        .getString(R.string.str_err_sns_id_off)
//        //UiRoot.getInstance().showErrorDlg(getTitle(), errMsg);
//        UiRoot.showErrorDlg(title, errMsg);
//    }


    public void onStop() {
        guiOnScreen=false;
        //MessageLogger.getPrefs(getActivity()).unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void onPause() {
        guiOnScreen=false;
        //MessageLogger.getPrefs(getActivity()).unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void onResume() {
        guiOnScreen=true;
        IView gui  =  TestPresenter.getGUInterface();
        if  (null  !=  gui) {
            gui.setToolbarTittle(getTitle());
        }
        requestLayout();
    }

    protected abstract void onDestroy();
    public void onDestroyCommon()  {
      onDestroy();
      isDestroyed  =  true;
    }
}
