package com.nakedferret.simplepass;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.PasswordStorageContract.AccountWGroup;

@EFragment(R.layout.frag_create_account)
public class FragCreateAccount extends SherlockFragment implements
		LoaderCallbacks<Cursor> {

	private static final String ARG_ACCOUNT_ID = "account";

	private static final int LAYOUT = android.R.layout.simple_spinner_item;
	private static final String[] PROJECTION = { AccountWGroup.COL_GROUP_NAME };
	private static final int[] VIEWS = { android.R.id.text1 };

	@ViewById
	Spinner groupSpinner;

	private OnAccountCreatedListener mListener;
	private long accountId;

	private SimpleCursorAdapter adapter;

	public static FragCreateAccount newInstance(long accountId) {
		FragCreateAccount fragment = new FragCreateAccount_();
		Bundle args = new Bundle();
		args.putLong(ARG_ACCOUNT_ID, accountId);
		fragment.setArguments(args);
		return fragment;
	}

	public FragCreateAccount() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			accountId = getArguments().getLong(ARG_ACCOUNT_ID);
		}
		Utils.log(this, "accountId = " + accountId);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new SimpleCursorAdapter(getActivity(), LAYOUT, null,
				PROJECTION, VIEWS, 0);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupSpinner.setAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAccountCreatedListener) activity;
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

	public interface OnAccountCreatedListener {
		public void onFragmentInteraction(Uri uri);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Utils.log(this, "Creating Loader");
		Uri uri = Utils.buildContentUri(AccountWGroup.TABLE_NAME);
		String selection = AccountWGroup.COL_VAULT_ID + " = ?";
		String[] selectionArgs = { String.valueOf(accountId) };

		return new CursorLoader(getActivity(), uri, PROJECTION, selection,
				selectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		Utils.log(this, "Cursor loaded, rows: " + c.getCount());
		adapter.changeCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
	}

}
