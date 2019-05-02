package com.bond.oncache.i;

import java.util.Map;

public interface ITestCase  {
  boolean  prepareTestCase(Map<String, String>  cfg);
  boolean  startTester(ITester  tester);
  void  stop();

  String  get_case_name();

  /*
   *  0 == IKeyInt3
   *  1 == IKeyString
   *  ...
   * */
  int  get_key_type();
}
