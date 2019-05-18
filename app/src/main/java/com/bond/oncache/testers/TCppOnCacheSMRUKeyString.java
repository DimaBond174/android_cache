package com.bond.oncache.testers;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */


import com.bond.oncache.TestPresenter;
import com.bond.oncache.i.ITester;
import com.bond.oncache.objs.TJsonToCfg;


public class TCppOnCacheSMRUKeyString implements ITester {
  static final int MY_CPP_TESTER_ID  =  3;

  @Override
  public boolean amThreadSafe() {
    return false;
  }

  @Override
  public void onStart(int capacity, TJsonToCfg cfg) {
    TestPresenter.warmUP(MY_CPP_TESTER_ID,  capacity);
  }

  @Override
  public void onStop() {

  }

  @Override
  public void insert(Object elem) {

  }

  @Override
  public boolean exist(Object elem) {
    return false;
  }

  @Override
  public String get_algorithm_name() {
    return "C++.OnCacheSMRU.S";
  }

  @Override
  public int get_key_type() {
    return 1;
  }

  @Override
  public boolean amJavaTester() {
    return  false;
  }

}
