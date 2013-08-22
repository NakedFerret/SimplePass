package com.nakedferret.simplepass;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(R.layout.frag_create_account)
public class FragCreateAccount extends SherlockFragment {

	private static final String ARG_ACCOUNT_ID = "account";
	private long accountId;

	private OnAccountCreatedListener mListener;

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
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}
