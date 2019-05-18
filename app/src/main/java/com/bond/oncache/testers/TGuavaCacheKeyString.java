package com.bond.oncache.testers;

import com.bond.oncache.i.IKeyString;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.TJsonToCfg;
import com.google.common.cache.CacheBuilder;

/**
 * Google Guava
 * https://github.com/google/guava
 */
public class TGuavaCacheKeyString implements ITester {
  com.google.common.cache.Cache<IKeyString,  IKeyString>  cache  =  null;

  @Override
  public boolean amThreadSafe() {
    return true;
  }

  @Override
  public void onStart(int capacity, TJsonToCfg cfg) {
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
    if (!(elem instanceof IKeyString)) return;
    IKeyString key = (IKeyString) elem;
    cache.put(key,  key);
  }

  @Override
  public boolean exist(Object elem) {
    if (!(elem instanceof IKeyString)) return  false;
    return  null  !=  cache.getIfPresent((IKeyString) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.TGuava.S";
  }

  @Override
  public int get_key_type() {
    return 1;
  }

  @Override
  public boolean amJavaTester() {
    return  true;
  }


}
