package com.nakedferret.simplepass;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.ContentValues;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(R.layout.frag_device_speed)
public class FragDeviceSpeed extends Fragment {

	private OnIterationsCalculatedListener mListener;

	private static final int INITIAL_ITER_GUESS = 1000;

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

	// Returns the number of iterations per second the phone can do
	// Roughly
	public interface OnIterationsCalculatedListener {
		public void onIterationsCalculated(int iters);
	}

	@AfterViews
	void init() {
		calculateIterations();
	}

	// Tries creating a key using a specific number of iterations,
	// times how long it took, adjusts the number of iterations, repeats.
	// Stops when the time it took to decrypt is 1 second +/- .05 seconds
	@Background
	void calculateIterations() {
		long margin = 500;
		int iter = INITIAL_ITER_GUESS;

		// Warm up CPU
		int i = 5;
		while (i > 0) {
			Utils.getKey("secret_pass", iter);
			i--;
		}

		while (margin > 50) {
			Log.d("SimplePass", "iters: " + iter);

			long start = System.currentTimeMillis();
			Utils.getKey("master_password", iter);
			long end = System.currentTimeMillis();

			long time = end - start;
			float factor = (float) 1000 / (float) time;
			Log.d("SimplePass", "Time: " + time);
			Log.d("SimplePass", "Factor: " + factor);
			iter *= factor;

			margin = Math.abs(1000 - time);
		}

	}
}
