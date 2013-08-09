package com.nakedferret.simplepass;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(R.layout.frag_device_speed)
public class FragDeviceSpeed extends Fragment {

	private OnIterationsCalculatedListener mListener;

	public FragDeviceSpeed() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnIterationsCalculatedListener) activity;
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

	public interface OnIterationsCalculatedListener {
		public void onIterationsCalculated(int iters);
	}

}
