package com.nakedferret.simplepass;

import android.database.Cursor;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.nakedferret.simplepass.FragListAccounts.OnAccountSelectedListener;

@EActivity(R.layout.act_select_account)
public class ActSelectAccount extends SherlockFragmentActivity implements
		OnAccountSelectedListener {

	@FragmentById
	FragListAccounts accountFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		showAsPopup();
	}

	private void showAsPopup() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = 1.0f;
		params.dimAmount = 0.5f;
		getWindow().setAttributes(params);

		// This sets the window size, while working around the
		// IllegalStateException thrown by ActionBarView
		Display dm = getWindowManager().getDefaultDisplay();
		int width = (int) (dm.getWidth() * .9);
		int height = (int) (dm.getHeight() * .9);
		getWindow().setLayout(width, height);
	}

	@AfterViews
	void initInterface() {
		Log.d("SimplePass", "Thread Policy: " + StrictMode.getThreadPolicy());
		Log.d("SimplePass", "VM Policy: " + StrictMode.getVmPolicy());
	}

	@Override
	public void onAccountSelected(Cursor c) {

	}

}
