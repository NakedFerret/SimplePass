package com.nakedferret.simplepass;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListFragment;

public class FragListCursor extends SherlockListFragment {
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

	private OnItemSelected mListener;

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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnItemSelected) activity;
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

	public interface OnItemSelected {
		public void onFragmentInteraction(Uri uri);
	}

}
