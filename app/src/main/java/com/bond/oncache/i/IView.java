package com.bond.oncache.i;

import android.content.Context;
import android.os.Handler;

public interface IView {
  void  setToolbarTittle(String  tittle);
  Handler getGuiHandler();
  Context  getForDialogCtx();
  void  showMessage(String  str);
  void  onPresenterChange();
}
