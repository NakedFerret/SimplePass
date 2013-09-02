package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.PasswordStorageContract.Account;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;
import com.nakedferret.simplepass.R.layout;

public class FragListAccount extends SherlockListFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {
	private static final String ARG_VAULT_URI = "vault_uri";

	private Uri vaultUri;
	private CursorAdapter adapter;

	private final int LAYOUT = R.layout.listview_account;
	private final String[] PROJECTION = { AccountWGroup.COL_NAME,
			AccountWGroup.COL_GROUP_NAME };
	private final int[] VIEWS = { android.R.id.text1, android.R.id.text2 };
	private final Uri URI = Utils.buildContentUri(Account.TABLE_NAME);
	private final String SELECTION = Account.COL_VAULT_ID + " = ?";

	private String[] selectionArgs;

	private OnAccountSelectedListener mListener;

	public static FragListAccount newInstance(Uri vaultUri) {
		FragListAccount fragment = new FragListAccount();
		Bundle args = new Bundle();
		args.putString(ARG_VAULT_URI, vaultUri.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public FragListAccount() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			vaultUri = Uri.parse(getArguments().getString(ARG_VAULT_URI));
			selectionArgs = new String[] { vaultUri.getLastPathSegment() };
		}
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
		adapter = new SimpleCursorAdapter(getActivity(), LAYOUT, null,
				PROJECTION, VIEWS, 0);
		getListView().setOnItemClickListener(this);
		getLoaderManager().initLoader(0, null, this);
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
