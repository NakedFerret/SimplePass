package com.nakedferret.simplepass;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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

	@StringArrayRes
	String[] secondsArray;

	private ArrayAdapter<String> adapter;

	private OnVaultCreatedListener mListener;

	public FragCreateVault() {
		// Required empty public constructor
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
				android.R.layout.simple_spinner_dropdown_item, secondsArray);
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
	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
