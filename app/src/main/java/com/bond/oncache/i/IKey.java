package com.bond.oncache.i;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

public interface IKey {
  int  cmp(Object o);
  /*
  *  Hash must be unsigned
  * */
  long get_hash();
}
