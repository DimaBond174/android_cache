//
// Created by dbond on 06.05.19.
//

#ifndef ANDROID_CACHE_SPECLOGGER_H
#define ANDROID_CACHE_SPECLOGGER_H

#include <android/log.h>

#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, kTAG, __VA_ARGS__))
#define LOGW(...) \
  ((void)__android_log_print(ANDROID_LOG_WARN, kTAG, __VA_ARGS__))
#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, kTAG, __VA_ARGS__))
#endif //ANDROID_CACHE_SPECLOGGER_H
