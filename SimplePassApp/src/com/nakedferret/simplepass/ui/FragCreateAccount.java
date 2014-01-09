package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;
import com.nakedferret.simplepass.Utils;

@EFragment(R.layout.frag_create_account)
public class FragCreateAccount extends Fragment implements
		LoaderCallbacks<Cursor>, OnItemSelectedListener {

	@ViewById
	Spinner groupSpinner;

	@FragmentArg
	String vaultUriString;

	@ViewById
	EditText accountNameInput, accountUsernameInput, accountPasswordInput;

	@App
	SimplePass app;

	private IFragListener mListener;
	private SimpleCursorAdapter adapter;
	private Uri vaultUri;
	private long categoryId;

	public FragCreateAccount() {
		// Required empty public constructor
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = getAdapter();
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groupSpinner.setAdapter(adapter);
		groupSpinner.setOnItemSelectedListener(this);
		getLoaderManager().initLoader(0, null, this);
	}

	private SimpleCursorAdapter getAdapter() {
		final int LAYOUT = android.R.layout.simple_spinner_item;
		final String[] PROJECTION = { "name" };
		final int[] VIEWS = { android.R.id.text1 };

		return new SimpleCursorAdapter(getActivity(), LAYOUT, null, PROJECTION,
				VIEWS, 0);
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

		Utils.addKeyboardInputExtras(accountNameInput);
		Utils.addKeyboardInputExtras(accountUsernameInput);
		Utils.addKeyboardInputExtras(accountPasswordInput);
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
		Uri uri = ContentProvider.createUri(Category.class, null);

		return new CursorLoader(getActivity(), uri, null, null, null, null);
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
		Uri categoryUri = ContentProvider.createUri(Category.class, categoryId);
		String name = accountNameInput.getText().toString();
		String username = accountUsernameInput.getText().toString();
		String password = accountPasswordInput.getText().toString();

		app.createAccount(vaultUri, categoryUri, name, username, password);
	}

	@Override
	public void onItemSelected(AdapterView<?> a, View v, int p, long id) {
		categoryId = id;
	}

	@Override
	public void onNothingSelected(AdapterView<?> a) {
		categoryId = 1;
	}

}
