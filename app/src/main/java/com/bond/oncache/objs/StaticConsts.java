package com.bond.oncache.objs;

import android.view.Menu;

public class StaticConsts {
  public static final int START_ITEMS  =  1000;
  public static final int MAX_INT =   2147000647;

  public static final String PARM_test_case = "test case";
  public static final String PARM_insert_threads = "insert threads";
  public static final String PARM_search_threads = "search threads";
  public static final String PARM_max_items = "max items";
  public static final String PARM_capacity_percent = "capacity percent";
  public static final String PARM_repeat_previous = "repeat previous";
  public static final String PARM_testers = "testers";
  public static final String PARM_1tester = "tester";
  public static final String PARM_results = "results";

  public static final String  json_params[] = new String[] {
      PARM_test_case,
      PARM_insert_threads,
      PARM_search_threads,
      PARM_max_items,
      PARM_capacity_percent,
      PARM_repeat_previous
  };
  public static final String HistoryFolder = "history";
  public static final String DefConfig = "default_cfg";

  public static final int MENU_UiMain  =  Menu.FIRST;
  public static final String FirstFragTAG = "UiMainFrag";
  public static final int MENU_UiHistory  =  Menu.FIRST  +  1;
  public static final String UiHistoryTAG = "UiHistoryFrag";
  public static final int MENU_UiHistoryClear  =  Menu.FIRST  +  2;
  public static final int MENU_UiSettings  =  Menu.FIRST  +  3;
  public static final String UiSettingsTAG = "UiSettingsFrag";
  public static final int MENU_UiSettingsDef  =  Menu.FIRST  +  4;
  public static final int MENU_Exit =  Menu.FIRST  +  5;
}
