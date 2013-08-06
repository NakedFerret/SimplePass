package com.nakedferret.simplepass;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.res.IntegerRes;
import com.nakedferret.simplepass.FragListAccounts.OnAccountSelectedListener;

@EActivity(R.layout.act_select_account)
public class ActSelectAccount extends SherlockFragmentActivity implements
		OnAccountSelectedListener {

	@FragmentById
	FragListAccounts accountFragment;

	@IntegerRes
	int widthPercent;

	@IntegerRes
	int heightPercent;

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
		int width = (int) (dm.getWidth() * (widthPercent / 100.0));
		int height = (int) (dm.getHeight() * (heightPercent / 100.0));
		getWindow().setLayout(width, height);
	}

	@AfterViews
	void initInterface() {
	}

	@Override
	public void onAccountSelected(Cursor c) {

	}

}
