package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;
import com.nakedferret.simplepass.PasswordStorageContract.Account_;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;

@EFragment
public class FragListAccount extends ListFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {

	@FragmentArg
	String vaultUriString;

	private final int LAYOUT = R.layout.listview_account;
	private final String[] PROJECTION = { AccountWGroup.COL_NAME,
			AccountWGroup.COL_GROUP_NAME };
	private final int[] VIEWS = { android.R.id.text1, android.R.id.text2 };
	private final Uri URI = Utils.buildContentUri(AccountWGroup.TABLE_NAME);
	private final String SELECTION = Account_.COL_VAULT_ID + " = ?";

	private String[] selectionArgs;
	private Uri vaultUri;
	private CursorAdapter adapter;
	private IFragListener mListener;

	public FragListAccount() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (IFragListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IFragListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SimpleCursorAdapter(getActivity(), LAYOUT, null,
				PROJECTION, VIEWS, 0);
		getListView().setOnItemClickListener(this);
		getLoaderManager().initLoader(0, null, this);

		setHasOptionsMenu(true);
	}

	@AfterViews
	void init() {
		vaultUri = Uri.parse(vaultUriString);
		selectionArgs = new String[] { vaultUri.getLastPathSegment() };
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.chooseAccount);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnAccountSelectedListener {
		public void onAccountSelected(Uri uri);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO: react when account selected
		Utils.log(this, "account selected: " + id);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), URI, PROJECTION, SELECTION,
				selectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		setListAdapter(adapter);
		adapter.changeCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
	}

}
