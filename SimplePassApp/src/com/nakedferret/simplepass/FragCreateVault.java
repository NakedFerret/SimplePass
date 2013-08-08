package com.nakedferret.simplepass;

import android.app.Activity;
import android.net.Uri;

import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(R.layout.frag_create_vault)
public class FragCreateVault extends SherlockFragment {

	private OnVaultCreatedListener mListener;

	public FragCreateVault() {
		// Required empty public constructor
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onVaultSelected(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnVaultCreatedListener) activity;
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

	public interface OnVaultCreatedListener {
		public void onVaultSelected(Uri uri);
	}

}
