package com.bond.oncache.objs;

import android.util.Log;

import com.bond.oncache.TestPresenter;
import com.bond.oncache.cases.RegistryCases;
import com.bond.oncache.i.ITestCase;
import com.bond.oncache.i.ITester;
import com.bond.oncache.testers.RegistryTesters;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TJsonToCfg {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  public boolean is_valid  =  false;

  public void setJSON(String  json) {
    try {
       jsonObj  =  new JSONObject(json);
      //faux loop
      do {
        String str = jsonObj.getString("test case");
        if  (null == str)  break;
        cur_test_case  = RegistryCases.getCase(str);
        str  =  jsonObj.getString("insert threads");
        if (null == str)  {  break; }
        int  insert_threads  =  Integer.parseInt(str);
        str  =  jsonObj.getString("search threads");
        if (null == str)  {  break; }
        int  search_threads  =  Integer.parseInt(str);
        boolean  single_thread_case = 1 == (search_threads + insert_threads);
        JSONArray arr_tst  =  jsonObj.getJSONArray("testers");
        testers = new ArrayList<>();
        results = new HashMap<>();
        boolean data_exists  =  false;
        for (int  i  =  0;  i < arr_tst.length();  ++i)  {
          JSONObject o = arr_tst.getJSONObject(i);
          str  =  o.getString("tester");
          if  (null == str)  continue;
          ITester tester =  RegistryTesters.getTester(str);
          if  (null == tester)  continue;
          if (!single_thread_case && !tester.amThreadSafe()) continue;
          testers.add(tester);
          JSONArray arr_results  = null;
          try {
            arr_results = jsonObj.getJSONArray("results");
          } catch (Exception e) {}

          if  (null == arr_results)  continue;
          if  (arr_results.length() <= 0) continue;
          ArrayList<Entry>  cur_results = new ArrayList<>();
          for (int  j = 0 ;  j < arr_results.length();  ++j) {
            cur_results.add(new Entry(j, arr_tst.getInt(j)));
          }
          results.put(tester, cur_results);
          data_exists = true;
        }
        cur_tester = -1;
        testers_count = testers.size();
        if (testers_count > 0) {
          is_valid  =  true;
          if (data_exists) {
            TestPresenter.setProgress(100);
          }
        }
      } while(false);
    }  catch (Exception e) {
      Log.e(TAG, "setJSON: ", e);
    }
  }

  public String getJSON() {
    if (is_valid) {
      return  null;
    }
    return null;
  }

  public String getParam(String str) {
    String  re  =  null;
    try {
      re  =  jsonObj.getString(str);
    } catch (Exception e) {
      Log.e(TAG, "getParam() error:", e);
    }
    return  re;
  }

  public ITestCase  getCase() {
    if (is_valid)  return cur_test_case;
    return null;
  }

  public ITester getCurTester() {
    if  (is_valid  && cur_tester >=0  &&  cur_tester < testers_count) {
      return testers.get(cur_tester);
    }
    return null;
  }

  public ITester  getNextTester() {
    ++cur_tester;
    return getCurTester();
  }

  public  int getTesters_count()  {
    return  testers_count;
  }

  public  void setllTesters()  {
    cur_tester = -1;
  }

  public ArrayList<ITester> getTesters()  {
    return  testers;
  }

  public void putResults(Map<ITester, ArrayList<Entry>>  cur_results)  {
    results  =  cur_results;
  }

  public Map<ITester, ArrayList<Entry>> getResults()  {
    return results;
  }

  public void  setDefaultTestCase()  {
    ITestCase cur_test_case  =  RegistryCases.getDefault();
    StringBuilder sb = new StringBuilder(1024);
    sb.append("{\"test case\":\"")
        .append(cur_test_case.get_case_name()).append("\"")
        .append(",\"insert threads\":\"1\"")
        .append(",\"search threads\":\"0\"")
        .append(",\"repeat previous\":\"0\"")
        .append(",\"max items\":\"100000\"")
        .append(",\"capacity percent\":\"10\"")
        .append(",\"testers\":[");
    boolean not_first  =  false;
    ArrayList<String> tlist  =  RegistryTesters.getListTesters(cur_test_case.get_key_type());
    for (String s :  tlist) {
      if (not_first) {  sb.append(",");  }
      not_first  =  true;
      sb.append("{\"tester\":\"")
          .append(s).append("\"")
          .append(",\"results\":[]}");
    }
    sb.append("]}"); //testers
    //settings_json  =  sb.toString();
    setJSON(sb.toString());
  }
  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  static final String TAG = "TJsonToCfg";
  ITestCase cur_test_case  =  null;
  JSONObject jsonObj  =  null;
  ArrayList<ITester> testers  =  null;
  Map<ITester, ArrayList<Entry>>  results  =  null;
  volatile int cur_tester  =  -1;
  volatile int testers_count  =  -1;


}
