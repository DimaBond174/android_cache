package com.bond.oncache.i;

import com.bond.oncache.objs.TJsonToCfg;


public interface ITester {
  void  onStart(int capacity, TJsonToCfg cfg);
  void  onStop();
  void  insert(Object  elem);
  boolean  exist(Object  elem);
  String  get_algorithm_name();

  /*
  *  0 == IKeyInt3
  *  1 == IKeyString
  *  ...
  * */
  int  get_key_type();

  /*
   *  Java impl or CPP impl
   */
  boolean amJavaTester();

  boolean amThreadSafe();
}
