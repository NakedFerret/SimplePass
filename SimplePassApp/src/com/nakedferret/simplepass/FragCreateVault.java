package com.nakedferret.simplepass;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;

@EFragment(R.layout.frag_create_vault)
public class FragCreateVault extends SherlockFragment implements
		OnItemSelectedListener {

	@ViewById
	Spinner secondsSpinner;

	@ViewById
	EditText vaultNameInput, vaultPasswordInput;

	@StringArrayRes
	String[] secondsArray;

	private ArrayAdapter<String> adapter;
	private OnVaultCreatedListener mListener;
	private int iterationsPerSecond;

	private static final String ARG_ITERS = "iters";

	public FragCreateVault() {
		// Required empty public constructor
	}

	public FragCreateVault_ newInstance(int iters) {
		FragCreateVault_ f = new FragCreateVault_();
		Bundle args = new Bundle();
		args.putInt(ARG_ITERS, iters);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			iterationsPerSecond = args.getInt(ARG_ITERS);
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

	@AfterViews
	void initSpinner() {
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, secondsArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		secondsSpinner.setAdapter(adapter);
		secondsSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		Log.d("SimplePass", "Spinner Selected...pos,id: " + position + "," + id);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Click(R.id.createButton)
	void onCreateVault() {

		tryToEncrypt();
	}

	@Background
	void tryToEncrypt() {
		String password = "password";
		String salt = "salt";

		PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(
				new SHA256Digest());
		generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password
				.toCharArray()), salt.getBytes(), 200);
		KeyParameter keyParameter = (KeyParameter) generator
				.generateDerivedMacParameters(256);

		String clearText = "clearText";

		try {
			Key key = new SecretKeySpec(keyParameter.getKey(), "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encText = c.doFinal(clearText.getBytes("UTF-8"));
			byte[] iv = c.getIV();

			byte[] hexEncText = Hex.encode(encText);
			byte[] hexIv = Hex.encode(iv);

			String hexEncTextString = new String(hexEncText);
			String hexIvString = new String(hexIv);

			Log.d("SimplePass", "ClearText: " + clearText);
			Log.d("SimplePass", "Encoded: " + hexEncTextString);
			Log.d("SimplePass", "Iv: " + hexIvString);

			tryToDecrypt(password, salt, hexEncTextString, hexIvString);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void tryToDecrypt(String password, String salt,
			String hexEncTextString, String hexIvString) {
		PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(
				new SHA256Digest());
		generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password
				.toCharArray()), salt.getBytes(), 200);
		KeyParameter keyParameter = (KeyParameter) generator
				.generateDerivedMacParameters(256);

		try {
			byte[] iv = Hex.decode(hexIvString.getBytes());
			byte[] encText = Hex.decode(hexEncTextString.getBytes());

			Key key = new SecretKeySpec(keyParameter.getKey(), "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

			byte[] decText = c.doFinal(encText);
			String clearText = new String(decText, "UTF-8");

			Log.d("SimplePass", "Decrypted Text: " + clearText);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
