package com.nakedferret.simplepass;

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

}
