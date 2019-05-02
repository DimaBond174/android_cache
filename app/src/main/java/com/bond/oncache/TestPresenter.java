package com.bond.oncache;

import android.content.Context;
import android.util.Log;

import com.bond.oncache.cases.*;
import com.bond.oncache.i.ITestCase;
import com.bond.oncache.i.ITester;
import com.bond.oncache.i.IView;
import com.bond.oncache.testers.*;

/*
 *  MVP presenter
 */
public class TestPresenter {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  // public for GUI single thread, thread unsafe:
  public static void  setGUInterface(IView  iView) {
    curActivity  =  iView;
    guiOnScreen  =  true;
  }

  public static IView getGUInterface()  {
    return  curActivity;
  }

  public static void onGUIstop()  {
    guiOnScreen  =  false;
  }

  public static String getRstring(int  id)  {
    IView gui = getGUInterface();
    if (null  !=  gui)  {
      return  gui.getForDialogCtx().getString(id);
    }
    return "";
  }

  /////////////////////////////////////////////////////////////////
  // public for multi thread, thread safe:
  public static volatile boolean  guiOnScreen  =  false;
  public static void runOnGUIthread (Runnable r) {
    if (guiOnScreen) {
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

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  // private for GUI single thread, thread unsafe:
  static IView curActivity  =  null;


  /////////////////////////////////////////////////////////////////
  // private for multi thread, thread safe:
  private static final String TAG = "TestPresenter";
  private static final ITester[] testers = new ITester[]{
      //new TCaffeineKeyInt3(),
      new TGuavaCacheKeyInt3()
  };

  private static final ITestCase[] cases = new ITestCase[]{
      new CaseKeyInt3()
  };



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
  public static native String stringFromJNI();
}
