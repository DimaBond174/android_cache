package com.bond.oncache.i;

import com.bond.oncache.objs.TJsonToCfg;


public interface ITestCase  {
  boolean  startTestCase(TJsonToCfg  cfg);
  void  stop();

  String  get_case_name();


  /*
   *  0 == IKeyInt3
   *  1 == IKeyString
   *  ...
   * */
  int  get_key_type();
}
