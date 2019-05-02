package com.bond.oncache.testers;

import com.bond.oncache.i.IKeyInt3;
import com.bond.oncache.i.ITester;
//import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;

/**
 * Caffeine
 * https://github.com/ben-manes/caffeine
 * Gradle:
 * api 'com.github.ben-manes.caffeine:caffeine:2.7.0'
 * Error:
 * Invoke-customs are only supported starting with Android O (--min-api 26)
 * But i have Android N only ..  so will not test caffeine
 */
public class TCaffeineKeyInt3 implements ITester {
  //com.github.benmanes.caffeine.cache.Cache<IKeyInt3,  IKeyInt3>  cache  =  null;

  @Override
  public void onStart(Map<String, String>  cfg) {
    int capacity  =  100;
    String str_capacity  =  cfg.get("capacity");
    if (null  !=  str_capacity) {
      capacity  =  Integer.getInteger(str_capacity);
    }
//    cache  =  Caffeine.newBuilder()
//        .maximumSize(capacity)
//        .build();
  }

  @Override
  public void onStop() {
    //cache  =  null;
  }

  @Override
  public void insert(Object elem) {
    if (!(elem instanceof IKeyInt3)) return;
    IKeyInt3 key = (IKeyInt3) elem;
    //cache.put(key,  key);
  }

  @Override
  public boolean exist(Object elem) {
    return false;
//    if (!(elem instanceof IKeyInt3)) return  false;
//    return  null  !=  cache.getIfPresent((IKeyInt3) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.TCaffeine";
  }

  @Override
  public int get_key_type() {
    return 0;
  }

  @Override
  public boolean amJavaTester() {
    return  true;
  }
}
