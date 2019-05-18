package com.bond.oncache.testers;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */


import com.bond.oncache.caches.OnCacheMMRU;
import com.bond.oncache.i.IKeyString;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.TJsonToCfg;


public class TOnCacheMMRUKeyString implements ITester {
  OnCacheMMRU<IKeyString,  IKeyString> cache  =  null;

  @Override
  public boolean amThreadSafe() {
    return true;
  }

  @Override
  public void onStart(int capacity, TJsonToCfg cfg) {
    cache  =  new OnCacheMMRU<IKeyString,  IKeyString>(capacity, 10);
  }

  @Override
  public void onStop() {
    cache  =  null;
  }

  @Override
  public void insert(Object elem) {
    if (!(elem instanceof IKeyString)) return;
    IKeyString key = (IKeyString) elem;
    cache.insertNode(key,  key);
  }

  @Override
  public boolean exist(Object elem) {
    if (!(elem instanceof IKeyString)) return  false;
    return  null  !=  cache.getData((IKeyString) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.OnCacheMMRU.S";
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
