package com.bond.oncache.cases;

import com.bond.oncache.i.ITestCase;

public class RegistryCases {

  public static ITestCase  getCase(String name) {
    for (ITestCase  c :  cases) {
      if (c.get_case_name().equals(name)) return  c;
    }
    return  null;
  }

  public static ITestCase  getDefault()  {
    return  cases[0];
  }

  private static final ITestCase[] cases = new ITestCase[] {
      new CaseKeyInt3()
  };
}
