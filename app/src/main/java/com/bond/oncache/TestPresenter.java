package com.bond.oncache;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.util.Log;
import com.bond.oncache.cases.*;
import com.bond.oncache.gui.SpecTheme;
import com.bond.oncache.i.ITestCase;
import com.bond.oncache.i.ITester;
import com.bond.oncache.i.IView;
import com.bond.oncache.objs.FileAdapter;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;
import com.bond.oncache.testers.*;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 *  MVP presenter
 */
public class TestPresenter {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  // public for GUI single thread, thread unsafe:
  public static void  setGUInterface(IView  iView) {
    cur_activity  =  iView;
    gui_on_screen  =  true;
//    if  (null == getConfig())  {
//      TJsonToCfg cfg  =  new TJsonToCfg();
//      cfg.setDefaultTestCase();
//      setConfig(cfg);
//    }
  }

  public static IView getGUInterface()  {
    return  cur_activity;
  }

  public static void onGUIstop()  {
    gui_on_screen  =  false;
  }

  public static String getRstring(int  id)  {
    IView gui = getGUInterface();
    if (null  !=  gui)  {
      return  gui.getForDialogCtx().getString(id);
    }
    return "";
  }

  public static void onFirstStart()  {
    if  (null == getConfig()) {
      String json = FileAdapter.loadAssetString(SpecTheme.context, StaticConsts.DefConfig);
      if (!json.isEmpty()) {
        TJsonToCfg  cfg  =  new TJsonToCfg();
        cfg.setJSON(json);
        setConfig(cfg);
      }
    }
  }

  /////////////////////////////////////////////////////////////////
  // public for multi thread, thread safe:
  public static volatile boolean  gui_on_screen  =  false;

  public  static void  setSettings(String  json) {
    stopProgress();
  }

  public static int  getProgress() {  return progress; }

  public static void  stopProgress()  {
    try {
      if (null != cur_test_case) {
        cur_test_case.stop();
      }
      progress  =  0;
      cur_activity.onPresenterChange();
    } catch (Exception e) {
      Log.e(TAG, "stopProgress  error:", e);
    }
    progress  =  0;

  }

  public static void  setProgress(int  progress_)  {
    progress  =  progress_;
    runOnGUIthread(new Runnable() {
      @Override
      public void run() {
        try {
          cur_activity.onPresenterChange();
        } catch (Exception e) {}
      }
    });
  }

  public static void saveResultsToHistory(TJsonToCfg cfg) {
    try {
      String json  =  cfg.getJSON();
      if (null != json) {
        FileAdapter.saveJsonHistory(StaticConsts.HistoryFolder,
            json, SpecTheme.context);
      }
    } catch ( Exception e) {
      Log.e(TAG, "On save history error : ", e);
    }
  }

  public static void  startProgress()  {
    if  (0 > progress  &&  progress < 100)  {   return;  }
    TJsonToCfg cfg  =  getConfig();
    if  (null  ==  cfg ||  !cfg.is_valid)  {
      try {
        cur_activity.showMessage(getRstring(R.string.strConfig_error));
      } catch (Exception e) {}
      return;
    }
    w_Cfg_Lock.lock();
    try {
      cur_test_case  =  cfg.getCase();
      if  (cur_test_case.startTestCase(cfg))  {
        progress  =  1;
        cur_activity.onPresenterChange();
      }  else {
        cur_activity.showMessage(getRstring(R.string.strTestCase_fail));
      }
    } catch (Exception e) {}
    w_Cfg_Lock.unlock();
  }

  public static TJsonToCfg getConfig() {
    r_Cfg_Lock.lock();
    TJsonToCfg  re  =  cfg;
    r_Cfg_Lock.unlock();
    return  re;
  }

  public  static void setConfig(TJsonToCfg  jsonToCfg) {
    w_Cfg_Lock.lock();
    cfg  =  jsonToCfg;
    w_Cfg_Lock.unlock();
    if (jsonToCfg.is_valid)  {
      setProgress(100);
    }
  }

  public static void runOnGUIthread (Runnable r) {
    if (gui_on_screen) {
      try {
        IView gui = getGUInterface();
        if (null  !=  gui)  {
          gui.getGuiHandler().post(r);
        }
      } catch (Exception e) {
        Log.e(TAG, "runOnGUIthread error:", e );
      }
    }
  }

  public static void runOnGUIthreadDelay (Runnable r,  long delay) {
    if (gui_on_screen) {
      try {
        IView gui = getGUInterface();
        if (null  !=  gui)  {
          gui.getGuiHandler().postDelayed(r, delay);
        }
      } catch (Exception e) {
        Log.e(TAG, "runOnGUIthread error:", e );
      }
    }
  }

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  // private for GUI single thread, thread unsafe:
  static IView cur_activity  =  null;


  /////////////////////////////////////////////////////////////////
  // private for multi thread, thread safe:
  private static final String TAG = "TestPresenter";
  static volatile int progress  =  0;  // !=0 if test is process


  ////////////////
  static final ReentrantReadWriteLock rw_Cfg_Lock  =  new ReentrantReadWriteLock();
  static final Lock r_Cfg_Lock = rw_Cfg_Lock.readLock();
  static final Lock w_Cfg_Lock = rw_Cfg_Lock.writeLock();
  static volatile  TJsonToCfg cfg  =  null;
  static volatile  ITestCase cur_test_case  =  null;
  ////////////////

  /*
  *   NDK staff
  * */
  // Used to load the 'native-lib' library on application startup.
  static {
    System.loadLibrary("native-lib");
  }

  /**
   * A native method that is implemented by the 'native-lib' native library,
   * which is packaged with this application.
   */
  //public static native String stringFromJNI();
  public static native void setNDKtestCaseInt3(int  rawData[],  int  rawDataLen);
  public static native void setNDKtestCaseKeyString(String  strData,  int  maxItems);
  public static native void warmUP(int cppTesterID,  int  capacity);
  public static native void runCppTest(int  insert_threads,
    int  search_threads,  int  max_items);
  public static native void stopCppTest();
}
