package com.bond.oncache.testers;

import com.bond.oncache.i.IKeyInt3;
import com.bond.oncache.i.ITester;

import java.util.Map;
import com.google.common.cache.CacheBuilder;

/**
 * Google Guava
 * https://github.com/google/guava
 */
public class TGuavaCacheKeyInt3 implements ITester {
  com.google.common.cache.Cache<IKeyInt3,  IKeyInt3>  cache  =  null;

  @Override
  public void onStart(Map<String, String>  cfg) {
    int capacity  =  100;
    String str_capacity  =  cfg.get("capacity");
    if (null  !=  str_capacity) {
      capacity  =  Integer.getInteger(str_capacity);
    }
    cache  =  CacheBuilder.newBuilder()
        .maximumSize(capacity)
        .build();
  }

  @Override
  public void onStop() {
    cache  =  null;
  }

  @Override
  public void insert(Object elem) {
    if (!(elem instanceof IKeyInt3)) return;
    IKeyInt3 key = (IKeyInt3) elem;
    cache.put(key,  key);
  }

  @Override
  public boolean exist(Object elem) {
    if (!(elem instanceof IKeyInt3)) return  false;
    return  null  !=  cache.getIfPresent((IKeyInt3) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.TGuava";
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