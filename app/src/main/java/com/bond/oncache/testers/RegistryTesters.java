package com.bond.oncache.testers;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

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
      new TAndroidLRUKeyInt3(),
      new TCppOnCacheSMRUKeyInt3(),
      new TCppOnCacheMLRUKeyInt3(),
      new TCppOnCacheMMRUKeyInt3(),
      new TOnCacheMLRUKeyInt3(),
      new TOnCacheMMRUKeyInt3()
  };
}
