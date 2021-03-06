package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.IntArrayRes;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.SimplePass;
import com.nakedferret.simplepass.utils.Utils;

@EFragment(R.layout.frag_create_vault)
public class FragCreateVault extends Fragment implements OnItemSelectedListener {

	@App
	SimplePass app;

	@ViewById
	Spinner iterationSpinner;

	@ViewById
	EditText vaultNameInput, vaultPasswordInput;

	@StringArrayRes
	String[] securityLevelNameArray;

	@IntArrayRes
	int[] securityLevelIterArray;

	ProgressDialog progressDialog;

	public interface LICreateVault {
		void createVault(String name, String password, int iterations);
	}

	private int iters;
	private Object listener;
	// Interfaces the listener implements
	private static final Class[] LIINTERFACES = new Class[] { LICancel.class,
			LICreateVault.class };

	public FragCreateVault() {
		// Required empty public constructor
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = Utils.proxyListener(activity, LIINTERFACES);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IFragListener");
		}
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

		String pass = vaultPasswordInput.getText().toString();
		String name = vaultNameInput.getText().toString();
		int iterations = iters;

		((LICreateVault) listener).createVault(name, pass, iterations);
	}

	@Click(R.id.cancelButton)
	void onCancel() {
		((LICancel) listener).cancel();
	}

	@Override
	public void onDestroy() {
		if (progressDialog != null)
			progressDialog.dismiss();

		super.onDestroy();
	}

}
