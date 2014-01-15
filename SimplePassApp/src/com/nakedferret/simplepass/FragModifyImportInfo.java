package com.nakedferret.simplepass;

import android.support.v4.app.ListFragment;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;

@EFragment
public class FragModifyImportInfo extends ListFragment {

	@FragmentArg
	int nameColumn, usernameColumn, passwordColumn, categoryColumn;

	@FragmentArg
	CSVImporter importer;

	public FragModifyImportInfo() {
		// Required empty public constructor
	}

	@AfterViews
	void init() {
		Utils.log(this, "number of columns: " + importer.getWidth());
	}

}
