package com.bond.oncache.i;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.content.Context;
import android.os.Handler;

public interface IView {
  void  setToolbarTittle(String  tittle);
  Handler getGuiHandler();
  Context  getForDialogCtx();
  void  showMessage(String  str);
  void  onPresenterChange();
  void  setFABicon();
  void  goBack();
}
