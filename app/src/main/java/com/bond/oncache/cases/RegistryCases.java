package com.bond.oncache.cases;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

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

  public static String[] get_names() {
    String re[] = new String[cases.length];
    for (int  i = 0;  i < cases.length;  ++i) {
      re[i]  =  cases[i].get_case_name();
    }
    return  re;
  }

  private static final ITestCase[] cases = new ITestCase[] {
      new CaseKeyInt3(),
      new CaseKeyString()
  };
}
