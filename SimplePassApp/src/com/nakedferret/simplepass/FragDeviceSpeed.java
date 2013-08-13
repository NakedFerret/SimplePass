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

	// Tries creating a key and encrypting using a specific number of
	// iterations,
	// times how long it took, adjusts the number of iterations, repeats.
	// Stops when the time it took to decrypt is 1 second +/- .05 seconds
	@Background
	void calculateIterations() {
		//
		float margin = 2;
		int iter = INITIAL_ITER_GUESS;

		while (margin > .05) {

			long time = encryptDataAndTimeDecryption(iter);

			Log.d("SimplePass", "---\nNum of iterations: " + iter
					+ "\nTime it took to decrypt: " + time);
			break;
		}

	}

	private long encryptDataAndTimeDecryption(int iter) {
		try {
			// Encrypt Random Data
			// First, Generate a key
			byte[] keyValue = Utils.getKey("master_password", iter);
			Key key = new SecretKeySpec(keyValue, Utils.KEY_SPEC);
			Cipher c;

			// Initialize the cipher to encrypt...
			c = Cipher.getInstance(Utils.ENCRYPTION_CIPHER);
			c.init(Cipher.ENCRYPT_MODE, key);

			// Next encrypt and save the initializing vector
			byte[] encData = c.doFinal("account_password".getBytes());
			byte[] iv = c.getIV();

			// Now to time the decryption
			// Initialize the decrypting Cipher
			c = Cipher.getInstance(Utils.ENCRYPTION_CIPHER);
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

			// Now encrypt and calculate the time it takes to do that
			long start = System.currentTimeMillis();
			c.doFinal(encData);
			long end = System.currentTimeMillis();

			return end - start;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
