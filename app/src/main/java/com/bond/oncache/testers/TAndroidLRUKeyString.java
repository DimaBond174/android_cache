package com.bond.oncache.testers;

import android.support.v4.util.LruCache;

import com.bond.oncache.i.IKeyString;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.TJsonToCfg;

/**
 * Android LruCache
 * https://developer.android.com/reference/android/util/LruCache
 */
public class TAndroidLRUKeyString implements ITester {
  LruCache<IKeyString,  IKeyString> cache  =  null;


  @Override
  public boolean amThreadSafe() {
    return true;
  }

  @Override
  public void onStart(int capacity, TJsonToCfg cfg) {
    cache  =  new LruCache<IKeyString,  IKeyString>(capacity);
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
    return  null  !=  cache.get((IKeyString) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.TAndroidLRU.S";
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
