package com.bond.oncache.testers;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */


import com.bond.oncache.caches.OnCacheMMRU;
import com.bond.oncache.i.IKeyInt3;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.TJsonToCfg;


public class TOnCacheMMRUKeyInt3 implements ITester {
  OnCacheMMRU<IKeyInt3,  IKeyInt3> cache  =  null;

  @Override
  public boolean amThreadSafe() {
    return true;
  }

  @Override
  public void onStart(int capacity, TJsonToCfg cfg) {
    cache  =  new OnCacheMMRU<IKeyInt3,  IKeyInt3>(capacity, 10);
  }

  @Override
  public void onStop() {
    cache  =  null;
  }

  @Override
  public void insert(Object elem) {
    if (!(elem instanceof IKeyInt3)) return;
    IKeyInt3 key = (IKeyInt3) elem;
    cache.insertNode(key,  key);
  }

  @Override
  public boolean exist(Object elem) {
    if (!(elem instanceof IKeyInt3)) return  false;
    return  null  !=  cache.getData((IKeyInt3) elem);
  }

  @Override
  public String get_algorithm_name() {
    return "Java.OnCacheMMRU";
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
