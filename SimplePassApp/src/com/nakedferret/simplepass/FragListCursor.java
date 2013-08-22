package com.nakedferret.simplepass;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockListFragment;

public class FragListCursor extends SherlockListFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {
	// A ListFragment that takes as arguments:
	// * ListItemLayout
	// * URI
	// * projections
	// * list of views that the projections match to
	// * column to search on
	// It notifies the activity when a list item is selected and
	// returns the URI for the row associated with the data

	private static final String ARG_LAYOUT = "layout";
	private static final String ARG_URI = "uri";
	private static final String ARG_PROJECTION = "projections";
	private static final String ARG_VIEWS = "views";
	private static final String ARG_SEARCH_COL = "searchColumn";

	private int layout;
	private Uri uri;
	private String[] projection;
	private int[] views;
	private String searchCol;

	private CursorAdapter adapter;

	private OnItemSelectedListener mListener;

	public static FragListCursor newInstance(int listItemLayout, Uri uri,
			String[] projection, int[] views, String searchColumn) {
		FragListCursor fragment = new FragListCursor();
		Bundle args = new Bundle();
		args.putInt(ARG_LAYOUT, listItemLayout);
		args.putString(ARG_URI, uri.toString());
		args.putStringArray(ARG_PROJECTION, projection);
		args.putIntArray(ARG_VIEWS, views);
		args.putString(ARG_SEARCH_COL, searchColumn);
		fragment.setArguments(args);
		return fragment;
	}

	public FragListCursor() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			layout = args.getInt(ARG_LAYOUT);
			uri = Uri.parse(args.getString(ARG_URI));
			projection = args.getStringArray(ARG_PROJECTION);
			views = args.getIntArray(ARG_VIEWS);
			searchCol = args.getString(ARG_SEARCH_COL);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SimpleCursorAdapter(getActivity(), layout, null,
				projection, views, 0);
		getListView().setOnItemClickListener(this);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnItemSelected");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnItemSelectedListener {
		public void onItemSelected(Uri uri);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mListener == null)
			return;

		Uri rowUri = uri.buildUpon().appendPath(Long.toString(id)).build();
		Log.d("SimplePass", "Generic Fragment - rowUri: " + rowUri.toString());
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), uri, projection, null, null,
				null);
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
}
