package com.nakedferret.simplepass;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.PasswordStorageContract.Account;

@EFragment
public class FragListAccounts extends SherlockListFragment implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
		TextWatcher {

	private OnAccountSelectedListener mListener;
	private CursorAdapter adapter;

	private String[] projections;
	private int[] views;

	public FragListAccounts() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		projections = new String[] { Account.COL_NAME, Account.COL_GROUP };
		views = new int[] { android.R.id.text1, android.R.id.text2 };
	}

	@AfterViews
	void initInterface() {
		adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null, projections, views,
				0);

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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem search = menu.add("Search");

		search.setIcon(R.drawable.ic_action_search)
				.setActionView(R.layout.edittext_search)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		EditText input = (EditText) search.getActionView();
		input.addTextChangedListener(this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		setListAdapter(adapter);
		adapter.changeCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.d("SimplePass", "Search Input: " + s.toString());
		adapter.getFilter().filter(s.toString());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

}
