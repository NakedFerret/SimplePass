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
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.JoinView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.nakedferret.simplepass.Account;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.R;

@EFragment
public class FragListAccount extends ListFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {

	@FragmentArg
	String vaultUriString;

	private static Uri URI;
	private static String SELECTION;
	private static JoinView VIEW;
	// View that represents the join of Account and Category

	private String[] selectionArgs;
	private Uri vaultUri;
	private CursorAdapter adapter;
	private IFragListener mListener;

	public FragListAccount() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_list_account, container, false);
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

		VIEW = (JoinView) Cache.getView("account_w_cat");
		URI = VIEW.getUri();
		SELECTION = VIEW.getColumnName(Account.class, "vault") + " = ?";

		adapter = getAdapter();
		getListView().setOnItemClickListener(this);
	}

	private SimpleCursorAdapter getAdapter() {
		final int LAYOUT = R.layout.listview_account;
		final String[] PROJECTION = {
				VIEW.getColumnName(Account.class, "name"),
				VIEW.getColumnName(Category.class, "name") };
		final int[] VIEWS = { android.R.id.text1, android.R.id.text2 };

		return new SimpleCursorAdapter(getActivity(), LAYOUT, null, PROJECTION,
				VIEWS, 0);
	}

	@AfterViews
	void init() {
		vaultUri = Uri.parse(vaultUriString);
		selectionArgs = new String[] { vaultUri.getLastPathSegment() };
	}

	@Override
	public void onStart() {
		super.onStart();
		getLoaderManager().initLoader(0, null, this);
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

		Uri accountUri = ContentProvider.createUri(Account.class, id);
		mListener.onAccountSelected(accountUri);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), URI, null, SELECTION,
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

	@Click(R.id.createAccountButton)
	public void onCreateAccountClicked() {
		mListener.requestCreateAccount(vaultUri);
	}

}
