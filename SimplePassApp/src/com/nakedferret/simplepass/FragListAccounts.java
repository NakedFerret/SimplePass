package com.nakedferret.simplepass;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.PasswordStorageContract.Account;

@EFragment
public class FragListAccounts extends ListFragment {

	private OnAccountSelectedListener mListener;
	CursorAdapter adapter;

	public FragListAccounts() {
		// Required empty public constructor
	}

	@AfterViews
	void initInterface() {
		adapter = new AccountAdaper(getActivity(), null, false);
	}

	void swapCursor(Cursor c) {
		adapter.swapCursor(c);
	}

	class AccountAdaper extends CursorAdapter {

		public AccountAdaper(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public void bindView(View v, Context cxt, Cursor c) {
			TextView tv = (TextView) v;
			String text = c.getString(c.getColumnIndex(Account.COL_NAME));
			tv.setText(text);
		}

		@Override
		public View newView(Context cxt, Cursor c, ViewGroup root) {
			c.moveToFirst();
			String text = c.getString(c.getColumnIndex(Account.COL_NAME));
			TextView tv = new TextView(cxt);
			tv.setText(text);
			return tv;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText(R.string.hello_blank_fragment);
		return textView;
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
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnAccountSelectedListener {
		public void onAccountSelected(Cursor c);
	}

}
