package com.bond.oncache.testers;

import android.support.v4.util.LruCache;

import com.bond.oncache.i.IKeyInt3;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.TJsonToCfg;
import com.google.common.cache.CacheBuilder;

/**
 * Android LruCache
 * https://developer.android.com/reference/android/util/LruCache
 */
public class TAndroidLRUKeyInt3 implements ITester {
  LruCache<IKeyInt3,  IKeyInt3> cache  =  null;


  @Override
  public boolean amThreadSafe() {
    return true;
  }

  @Override
  public void onStart(int capacity, TJsonToCfg cfg) {
    cache  =  new LruCache<IKeyInt3,  IKeyInt3>(capacity);
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
    return  null  !=  cache.get((IKeyInt3) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.TAndroidLRU";
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