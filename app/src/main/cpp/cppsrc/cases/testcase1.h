/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTCASE1_H
#define TESTCASE1_H

#include <jni.h>
#include "../i/ikey.h"
#include "../i/icase.h"
#include <deque>
#include <vector>
#include <future>
#include <shared_mutex>
#include "../spec/linuxsemaphore.h"

/*
*  Test case to insert/find intems with key(int64[3])
*/

using TimePoint  =  std::chrono::time_point<std::chrono::system_clock>;

class TestCase1  :  public ITestCase {
 public:
  TestCase1(jint  *cData, jint  rawDataLen);
  ~TestCase1() override ;
  void  stop()  override;
  void  warmUP (int32_t capacity, std::shared_ptr<IAlgorithmTester>  tester)  override;
  int  get_key_type()  override;
  void  doTest(int32_t  insert_threads,
      int32_t  search_threads,  int32_t  max_items)  override;
 private:
  std::vector<Elem *>  test_data;
  std::shared_ptr<IAlgorithmTester>  cur_tester;
  std::shared_mutex  cur_tester_mutex;
  std::atomic_bool  keep_run {  true };
  std::atomic_uint32_t  tester_run_id { 0 };
  Semaphore  semaphore;

  void  set_cur_tester(std::shared_ptr<IAlgorithmTester>  _cur_tester);
  std::shared_ptr<IAlgorithmTester>  get_cur_tester();

  class ParamsPack {
   public:
    ParamsPack(int32_t  _max_items, uint32_t  _my_run_id,
               const std::shared_ptr<IAlgorithmTester>  &_cur_tester)
        : max_items(_max_items),  my_run_id(_my_run_id),
          cur_tester(_cur_tester)  {  }
    const int32_t  max_items;
    const uint32_t  my_run_id;
    std::shared_ptr<IAlgorithmTester>  cur_tester;
  };

  void  run_insert(std::shared_ptr<ParamsPack> params);
  static void * s_run_insert(void  *ptr, std::shared_ptr<ParamsPack> params);

  void  run_search(std::shared_ptr<ParamsPack> params);
  static void * s_run_search(void  *ptr, std::shared_ptr<ParamsPack> params);

};


#endif // TESTCASE1_H
