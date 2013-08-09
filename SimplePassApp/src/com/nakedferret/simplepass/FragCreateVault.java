package com.nakedferret.simplepass;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.encoders.Hex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
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
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.IntArrayRes;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;

@EFragment(R.layout.frag_create_vault)
public class FragCreateVault extends SherlockFragment implements
		OnItemSelectedListener {

	private static final Digest DIGEST = new SHA256Digest();

	@ViewById
	Spinner secondsSpinner;

	@ViewById
	EditText vaultNameInput, vaultPasswordInput;

	@StringArrayRes
	String[] secondsStringArray;

	@IntArrayRes
	int[] secondsIntArray;

	ProgressDialog progressDialog;

	private ArrayAdapter<String> adapter;
	private OnVaultCreatedListener mListener;
	private int iterationsPerSecond;
	private int seconds;

	private static final String ARG_ITERS = "iters";

	private static final int SALT_SIZE = 32;

	public FragCreateVault() {
		// Required empty public constructor
	}

	public static FragCreateVault_ newInstance(int iters) {
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
	void init() {
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, secondsStringArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		secondsSpinner.setAdapter(adapter);
		secondsSpinner.setOnItemSelectedListener(this);
		seconds = secondsIntArray[0];
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		seconds = secondsIntArray[position];
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Click(R.id.createButton)
	void onCreateVault() {
		progressDialog = ProgressDialog.show(getActivity(), "Securing Vault",
				null, true, true);
		createAndSaveVault();
	}

	@Background
	void createAndSaveVault() {

		ContentValues values = createVault();
		ContentResolver r = getActivity().getContentResolver();
		r.insert(Utils.buildContentUri(Vault.TABLE_NAME), values);
		onVaultSaved();
	}

	private ContentValues createVault() {

		try {

			ContentValues values = new ContentValues();

			String password = vaultNameInput.getText().toString();
			byte[] salt = new byte[SALT_SIZE];
			new SecureRandom().nextBytes(salt);
			byte[] keyValue = getKey(password, salt, iterationsPerSecond
					* seconds);

			Key key = new SecretKeySpec(keyValue, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encPass = c.doFinal(password.getBytes("UTF-8"));
			byte[] iv = c.getIV();

			String hexIv = new String(Hex.encode(iv));
			String hexSalt = new String(Hex.encode(salt));
			String hexHash = new String(getHash(encPass, salt));

			values.put(Vault.COL_NAME, vaultNameInput.getText().toString());
			values.put(Vault.COL_ITERATIONS, iterationsPerSecond * seconds);
			values.put(Vault.COL_IV, hexIv);
			values.put(Vault.COL_SALT, hexSalt);
			values.put(Vault.COL_HASH, hexHash);
			
			return values;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private byte[] getHash(byte[] encPass, byte[] salt) {
		Digest d = DIGEST;
		d.reset();

		d.update(encPass, 0, encPass.length);
		d.update(salt, 0, salt.length);

		byte[] hash = new byte[d.getDigestSize()];
		d.doFinal(hash, 0);
		return hash;
	}

	private boolean checkPassword(String password, Cursor row) {
		// TODO: Implement checkPassword()
		return false;
	}

	private static byte[] getKey(String pass, byte[] salt, int iterations) {
		char[] passChar = pass.toCharArray();
		byte[] passBytes = PBEParametersGenerator
				.PKCS5PasswordToUTF8Bytes(passChar);
		PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(DIGEST);
		gen.init(passBytes, salt, iterations);
		KeyParameter key = (KeyParameter) gen.generateDerivedMacParameters(256);
		return key.getKey();
	}

	@UiThread
	void onVaultSaved() {
		progressDialog.dismiss();
	}
}
