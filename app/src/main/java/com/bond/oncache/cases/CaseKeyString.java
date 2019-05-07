package com.bond.oncache.cases;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.util.Log;

import com.bond.oncache.R;
import com.bond.oncache.TestPresenter;
import com.bond.oncache.gui.SpecTheme;
import com.bond.oncache.i.ITestCase;
import com.bond.oncache.objs.FileAdapter;
import com.bond.oncache.objs.TJsonToCfg;
import com.bond.oncache.objs.TestParam;

public class CaseKeyString implements ITestCase {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  @Override
  public String get_case_name() {
    return "Case Key = Int[3]";
  }

  @Override
  public int get_key_type() {
    return 0;
  }

  @Override
  public TestParam[] get_required_params() {
    return new TestParam[0];
  }

  @Override
  public boolean startTestCase(TJsonToCfg cfg) {
    boolean  re  =  false;
    try {
      //faux loop
      do  {
        String str  =  cfg.getParam("strings dataset");
        if (null == str)  break;
        if (!FileAdapter.existsFile(str, SpecTheme.context)) {
          TestPresenter.getGUInterface().showMessage(
              TestPresenter.getRstring(R.string.strFileNotExist) + str
          );
          break;
        }
        re = true;
      }  while(false);
    } catch (Exception e) {
      Log.e(TAG, "startTestCase  error:", e);
    }
    return re;
  }


  @Override
  public void stop() {

  }

  @Override
  public String get_settings_for_JSON() {
    return null;
  }

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  private static final String TAG = "CaseKeyString";
}
