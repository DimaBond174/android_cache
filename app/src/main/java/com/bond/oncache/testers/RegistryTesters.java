package com.bond.oncache.testers;

import com.bond.oncache.i.ITester;

import java.util.ArrayList;

public class RegistryTesters {
  public static ITester getTester(String name) {
    for (ITester  t :  testers) {
      if (t.get_algorithm_name().equals(name)) return t;
    }
    return  null;
  }

  public static ArrayList<String> getListTesters(int  key_type) {
    ArrayList<String>  re  = new ArrayList<>();
    for (ITester  t :  testers) {
      if  (t.get_key_type() != key_type)  continue;
      re.add(t.get_algorithm_name());
    }
    return  re;
  }

  private static final ITester[] testers = new ITester[] {
      //new TCaffeineKeyInt3(),
      new TOnCacheSMRUKeyInt3(),
      new TGuavaCacheKeyInt3(),
      new TAndroidLRUKeyInt3()
  };
}
