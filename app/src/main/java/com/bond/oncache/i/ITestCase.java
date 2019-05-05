package com.bond.oncache.i;

import com.bond.oncache.objs.TJsonToCfg;
import com.bond.oncache.objs.TestParam;


public interface ITestCase  {
  boolean  startTestCase(TJsonToCfg  cfg);
  void  stop();
  String  get_case_name();
  String  get_settings_for_JSON();
  TestParam[]  get_required_params();

  /*
   *  0 == IKeyInt3
   *  1 == IKeyString
   *  ...
   * */
  int  get_key_type();
}
