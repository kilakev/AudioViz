package com.kevyavno.app;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

  private final Activity activity;

  public MyGestureListener(Activity activity) {
    super();
    this.activity = activity;
  }
  @Override
  public boolean onSingleTapConfirmed(MotionEvent e) {
	  ((MainActivity)activity).screenTapped();
	return false;
  }
 
}