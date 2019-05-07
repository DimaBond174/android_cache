package com.bond.oncache.objs;

public class ThreadLocalByte {
  public int getNextByte()  {
    int re = id.get() + 1;
    if  (re > 255)  {  re = 0; }
    id.set(re);
    return  re;
  }

  private static final ThreadLocal<Integer> id =
      new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue()
        {
          return new Integer(0);
        }
      };
}
