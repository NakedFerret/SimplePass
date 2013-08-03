package com.nakedferret.simplepass;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.nakedferret.simplepass.FragListAccounts.OnAccountSelectedListener;

@EActivity(R.layout.act_select_account)
public class ActSelectAccount extends SherlockFragmentActivity implements
		LoaderCallbacks<Cursor>, OnAccountSelectedListener {

	@FragmentById
	FragListAccounts accountFragment;

	@AfterViews
	void initInterface() {

	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0,
			Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0,
			Cursor arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccountSelected(Cursor c) {

	}

}
