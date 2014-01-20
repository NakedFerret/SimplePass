package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.nakedferret.simplepass.R;

@EFragment(R.layout.frag_import_pick_file_type)
@OptionsMenu(R.menu.frag_import_pick_file_type)
public class FragImportPickFileType extends Fragment implements
		OnItemSelectedListener {

	@StringArrayRes
	String[] fileTypeArray, fileFormatArray;

	@ViewById
	Spinner fileTypeSpinner, fileMappingSpinner;

	private int fileType, fileMapping;
	private ActImport activity;

	public FragImportPickFileType() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ActImport) activity;
	}

	@AfterViews
	void init() {
		fileTypeSpinner.setAdapter(getAdapter(fileTypeArray));
		fileTypeSpinner.setOnItemSelectedListener(this);
		fileMappingSpinner.setAdapter(getAdapter(fileFormatArray));
		fileMappingSpinner.setOnItemSelectedListener(this);
	}

	private SpinnerAdapter getAdapter(String[] stringArray) {
		ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, stringArray);
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return a;
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View view, int position,
			long id) {

		if (spinner == fileTypeSpinner)
			fileType = position;
		else if (spinner == fileMappingSpinner)
			fileMapping = position;
	}

	@OptionsItem(R.id.action_continue)
	void actionContinue() {
		activity.processFile(fileType, fileMapping);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
