package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.JoinView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.nakedferret.simplepass.Account;
import com.nakedferret.simplepass.Category;
import com.nakedferret.simplepass.IFragListener;
import com.nakedferret.simplepass.MultiUriCursorLoader;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;

@EFragment
public class FragListAccount extends ListFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {

	@FragmentArg
	String vaultUriString;

	@App
	SimplePass app;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_list_account, container, false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.frag_list_account, menu);
		super.onCreateOptionsMenu(menu, inflater);
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
		getActivity().setTitle(R.string.frag_list_account_title);
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
		MultiUriCursorLoader loader = new MultiUriCursorLoader(getActivity(),
				URI, null, SELECTION, selectionArgs, null);

		loader.addUri(ContentProvider.createUri(Account.class, null));
		loader.addUri(ContentProvider.createUri(Category.class, null));

		return loader;
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_lock_vault:
			app.lockVault(vaultUri);
			return true;
		case R.id.action_add_account:
			mListener.requestCreateAccount(vaultUri);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

}
