package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;

import com.activeandroid.content.ContentProvider;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Vault;

@EFragment
public class FragListVault extends ListFragment implements OnItemClickListener,
		LoaderCallbacks<Cursor> {

	private static Uri URI = null;
	private IFragListener mListener;
	private SimpleCursorAdapter adapter;

	public FragListVault() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_list_vault, container, false);
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
	public void onStart() {
		super.onStart();
		URI = ContentProvider.createUri(Vault.class, null);

		adapter = getAdapter();
		getListView().setOnItemClickListener(this);

		getLoaderManager().initLoader(0, null, this);
	}

	private SimpleCursorAdapter getAdapter() {
		final int LAYOUT = android.R.layout.simple_list_item_1;
		final String[] PROJECTION = { "name" };
		final int[] VIEWS = { android.R.id.text1 };

		return new SimpleCursorAdapter(getActivity(), LAYOUT, null, PROJECTION,
				VIEWS, 0);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.selectVaultTitle);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), URI, null, null, null, null);
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
		if (mListener == null)
			return;

		mListener.onVaultSelected(ContentProvider.createUri(Vault.class, id));
	}

	@Click(R.id.createVaultButton)
	void onCreateVaultClicked() {
		mListener.requestCreateVault();
	}

}
