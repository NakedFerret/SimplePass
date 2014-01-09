package com.nakedferret.simplepass.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.nakedferret.simplepass.R;

public class ActFloating extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		showAsPopup();
		detectOutsideTouch();
	}

	private void showAsPopup() {
		// The width and height of the floating activity will be a fraction of
		// the screen size
		int width = getWidth();
		int height = getHeight();

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;
		getWindow().setAttributes(params);

		// This sets the window size, while working around the
		// IllegalStateException thrown by ActionBarView
		getWindow().setLayout(width, height);
	}

	@SuppressWarnings("deprecation")
	private int getWidth() {
		Display dm = getWindowManager().getDefaultDisplay();
		int widthPercent = getResources().getInteger(R.integer.widthPercent);
		int width = (int) (dm.getWidth() * (widthPercent / 100.0));
		return width;
	}

	@SuppressWarnings("deprecation")
	private int getHeight() {
		Display dm = getWindowManager().getDefaultDisplay();
		int heightPercent = getResources().getInteger(R.integer.heightPercent);
		int height = (int) (dm.getHeight() * (heightPercent / 100.0));
		return height;
	}

	private void detectOutsideTouch() {
		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,
				LayoutParams.FLAG_NOT_TOUCH_MODAL);

		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			Log.d("SimplePass", "Outside touch");
			finish();
			return true;
		}

		return super.onTouchEvent(event);
	}

}