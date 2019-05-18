package com.bond.oncache.objs;

public class TestParam {
  public static final int TYPE_BOOL  = 0;
  public static final int TYPE_NUM  = 1;
  public static final int TYPE_ITEMS  = 2;
  public static final int TYPE_STRINGS  = 3;
  public static final int TYPE_FILE  = 3;
  public final int  type;
  public final String caption;
  public final int min_value;
  public final int max_value;
  public String value;
  public TestParam(int  type, String caption, String value) {
    this.type  =  type;
    this.caption  =  caption;
    this.value  =  value;
    min_value = -1;
    max_value = -1;
  }

  public TestParam(int  type, String caption, String value,
        int min_value,  int max_value)  {
    this.type  =  type;
    this.caption  =  caption;
    this.value  =  value;
    this.min_value  =  min_value;
    this.max_value  =  max_value;
  }
}
