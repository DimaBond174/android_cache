package com.bond.oncache.cases;

import com.bond.oncache.i.ITestCase;
import com.bond.oncache.i.ITester;

import java.util.Map;

public class CaseKeyInt3  implements ITestCase {
  @Override
  public String get_case_name() {
    return "Case Key = Int[3]";
  }

  @Override
  public int get_key_type() {
    return 0;
  }

  @Override
  public boolean prepareTestCase(Map<String, String> cfg) {
    return false;
  }

  @Override
  public boolean startTester(ITester tester) {
    return false;
  }

  @Override
  public void stop() {

  }
}
