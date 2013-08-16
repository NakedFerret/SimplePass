package com.nakedferret.simplepass;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.PasswordStorageContract.Account;

@EFragment
public class FragListAccounts extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor>, TextWatcher,
		FilterQueryProvider, OnMenuItemClickListener, OnItemClickListener {

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
		projections = new String[] { Account.COL_NAME, Account.COL_GROUP_ID };
		views = new int[] { android.R.id.text1, android.R.id.text2 };
	}

	@AfterViews
	void initInterface() {
		adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_2, null, projections, views,
				0);
		getListView().setOnItemClickListener(this);
		adapter.setFilterQueryProvider(this);
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

		Uri uri = Utils.buildContentUri(Account.TABLE_NAME);

		return new CursorLoader(getActivity(), uri, projections, null, null,
				null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add("Add").setIcon(R.drawable.ic_action_add)
				.setOnMenuItemClickListener(this)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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

	@Override
	public Cursor runQuery(CharSequence constraint) {

		ContentResolver r = getActivity().getContentResolver();
		Uri uri = Utils.buildContentUri(Account.TABLE_NAME);
		String selection = Account.COL_NAME + " like ?";
		String[] args = { constraint.toString() + "%" };

		if ("".equals(constraint.toString()))
			return r.query(uri, projections, null, null, null);
		else
			return r.query(uri, projections, selection, args, null);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		insertNewAccount();
		return true;
	}

	@Background
	void insertNewAccount() {
		Log.d("SimplePass", "Add button clicked");
		Uri uri = Utils.buildContentUri(Account.TABLE_NAME);
		ContentValues values = new ContentValues();
		values.put(Account.COL_NAME, "paypal");
		values.put(Account.COL_VAULT_ID, 1);
		values.put(Account.COL_GROUP_ID, "financial");
		values.put(Account.COL_USERNAME, "naked_ferret");
		values.put(Account.COL_PASSWORD, "secret4");

		getActivity().getContentResolver().insert(uri, values);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Cursor c = (Cursor) adapter.getItem(position);
		Log.d("SimplePass", "It worked! Cols: ");
		for( String s : c.getColumnNames())
			Log.d("SimplePass", s);
	}
}
