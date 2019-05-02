package com.bond.oncache.i;

import java.util.Map;

public interface ITester {
  void  onStart(Map<String, String>  cfg);
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
}
