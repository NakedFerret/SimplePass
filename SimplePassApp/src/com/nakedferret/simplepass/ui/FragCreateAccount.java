package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.PasswordStorageContract.Account_;
import com.nakedferret.simplepass.PasswordStorageContract.Group_;
import com.nakedferret.simplepass.PasswordStorageContract.Vault_;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.Vault;

@EFragment(R.layout.frag_create_account)
public class FragCreateAccount extends Fragment implements
		LoaderCallbacks<Cursor> {

	private static final int LAYOUT = android.R.layout.simple_spinner_item;
	private static final String[] PROJECTION = { Group_.COL_NAME };
	private static final int[] VIEWS = { android.R.id.text1 };

	@ViewById
	Spinner groupSpinner;

	@FragmentArg
	String vaultUriString;

	private IFragListener mListener;
	private SimpleCursorAdapter adapter;
	private Uri vaultUri;

	public FragCreateAccount() {
		// Required empty public constructor
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

	@AfterViews
	void init() {
		vaultUri = Uri.parse(vaultUriString);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnAccountCreatedListener {
		public void onAccountCreated(Uri uri);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Utils.log(this, "Creating Loader");
		Uri uri = Utils.buildContentUri(Group_.TABLE_NAME);

		return new CursorLoader(getActivity(), uri, PROJECTION, null, null,
				null);
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

	@Click(R.id.createButton)
	void onCreateButton() {
		testAccount();
	}

	@Background
	void testAccount() {
		String masterPass = "master_password";

		Vault v = Vault.createVault("Personal", masterPass, 5000);
		byte[] key = Utils.getKey(masterPass, v.salt, v.iterations);

		String password = "account_pass";
		String name = "test account";
		String username = "test user";

		ContentValues account = Utils.createAccount(1, 1, name, username,
				password, key, v.iv);
		Utils.log(this, "Created Account");

		Utils.log(this, "Decrypting account...");
		account = Utils.decryptAccount(account, key, v.iv);
		Utils.log(
				this,
				"Account username: "
						+ account.getAsString(Account_.DEC_USERNAME));

	}
}
