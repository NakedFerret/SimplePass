package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.PasswordStorageContract.Vault_;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;
import com.nakedferret.simplepass.Vault;

@EFragment
public class FragListVault extends SherlockListFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor>, OnMenuItemClickListener {

	private final int LAYOUT = android.R.layout.simple_list_item_1;
	private Uri URI = null;
	private final String[] PROJECTION = { "name" };
	private final int[] VIEWS = { android.R.id.text1 };

	private IFragListener mListener;
	private SimpleCursorAdapter adapter;

	public FragListVault() {
		// Required empty public constructor
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onVaultSelected(uri);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add("Create Vault")
				.setShowAsActionFlags(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT)
				.setOnMenuItemClickListener(this);
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
//		getLoaderManager().initLoader(0, null, this);

		URI = ContentProvider.createUri(Vault.class, null);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.selectVaultTitle);
		tryManualQuery();
	}

	
	@Background
	void tryManualQuery() {
		Cursor c = getActivity().getContentResolver().query(URI, null, null, null, null);
		setListAdapter(adapter);
		adapter.changeCursor(c);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), URI, PROJECTION, null, null,
				null);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
		if (mListener != null)
			mListener.onVaultSelected(Utils.buildContentUri(Vault_.TABLE_NAME,
					id));
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		mListener.requestCreateVault();
		return true;
	}

}
