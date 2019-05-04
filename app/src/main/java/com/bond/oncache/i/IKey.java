package com.bond.oncache.i;

public interface IKey {
  int  cmp(Object o);
  /*
  *  Hash must be unsigned
  * */
  long get_hash();
}
