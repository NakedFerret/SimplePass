package com.nakedferret.simplepass.ui;

import android.database.Cursor;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.R.layout;
import com.nakedferret.simplepass.ui.FragListAccounts.OnAccountSelectedListener;

@EActivity(R.layout.act_select_account)
public class ActSelectAccount extends ActFloating implements
		OnAccountSelectedListener {

	@FragmentById
	FragListAccounts accountFragment;

	@AfterViews
	void initInterface() {
	}

	@Override
	public void onAccountSelected(Cursor c) {

	}

}
