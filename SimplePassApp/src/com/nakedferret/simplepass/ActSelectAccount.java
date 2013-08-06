package com.nakedferret.simplepass;

import android.database.Cursor;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.nakedferret.simplepass.FragListAccounts.OnAccountSelectedListener;

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
