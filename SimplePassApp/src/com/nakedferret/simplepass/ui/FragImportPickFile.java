package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.nakedferret.simplepass.R;

@EFragment(R.layout.frag_import_pick_file)
public class FragImportPickFile extends Fragment {

	private ActImport activity;

	public FragImportPickFile() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActImport) activity;
		super.onAttach(activity);
	}

	@Click(R.id.pickFileButton)
	void onPickFile() {
		launchFilePicker();
	}

	private void launchFilePicker() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("file/*");
		// calling the fragments startActivityForResult() does not provide the
		// activity with the correct request code
		activity.startActivityForResult(i, ActImport.REQUEST_PICK_FILE);
	}

}
