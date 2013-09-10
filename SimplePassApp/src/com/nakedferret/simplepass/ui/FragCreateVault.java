package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
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
import com.nakedferret.simplepass.IVaultInteractionListener;
import com.nakedferret.simplepass.PasswordStorageContract.Vault;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;

@EFragment(R.layout.frag_create_vault)
public class FragCreateVault extends SherlockFragment implements
		OnItemSelectedListener {

	@ViewById
	Spinner iterationSpinner;

	@ViewById
	EditText vaultNameInput, vaultPasswordInput;

	@StringArrayRes
	String[] securityLevelNameArray;

	@IntArrayRes
	int[] securityLevelIterArray;

	ProgressDialog progressDialog;

	private IVaultInteractionListener mListener;
	private int iters;

	public FragCreateVault() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (IVaultInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IVaultInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	public interface OnVaultCreatedListener {
		public void onVaultCreated(Uri uri);
	}

	@AfterViews
	void init() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, securityLevelNameArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		iterationSpinner.setAdapter(adapter);
		iterationSpinner.setOnItemSelectedListener(this);
		iters = securityLevelIterArray[0];
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		iters = securityLevelIterArray[position];
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

		String pass = vaultPasswordInput.getText().toString();
		String name = vaultNameInput.getText().toString();
		int iterations = iters;

		ContentValues values = Utils.createVault(name, pass, iterations);
		ContentResolver r = getActivity().getContentResolver();
		Uri vaultUri = r
				.insert(Utils.buildContentUri(Vault.TABLE_NAME), values);
		onVaultSaved(vaultUri);
	}

	@UiThread
	void onVaultSaved(Uri vaultUri) {
		progressDialog.dismiss();
		if (mListener != null)
			mListener.onVaultCreated(vaultUri);
	}
}
