package com.nakedferret.simplepass;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.PasswordStorageContract.Account;

@EFragment
public class FragListAccounts extends SherlockListFragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	private OnAccountSelectedListener mListener;
	private CursorAdapter adapter;

	private static final String[] projections = new String[] {
			Account.COL_NAME, Account.COL_GROUP };
	private static final int[] views = new int[] { R.id.nameText,
			R.id.groupText };

	public FragListAccounts() {

	}

	@AfterViews
	void initInterface() {
		adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listitem_account, null, projections, views, 0);
		setListAdapter(adapter);
		Log.d("SimplePass", "Account List Fragment");
		setEmptyText("Hello from Account List");
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAccountSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnAccountSelectedListener {
		public void onAccountSelected(Cursor c);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loader, Bundle args) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(PasswordStorageProvider.authority);
		builder.appendPath(Account.TABLE_NAME);

		return new CursorLoader(getActivity(), builder.build(), projections,
				null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		adapter.changeCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

}
