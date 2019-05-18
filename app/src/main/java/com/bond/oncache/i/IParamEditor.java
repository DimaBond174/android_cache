package com.bond.oncache.i;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

public interface IParamEditor {
  String get_current_value();
  void set_current_value(String  val);
  String get_param_name();
}
