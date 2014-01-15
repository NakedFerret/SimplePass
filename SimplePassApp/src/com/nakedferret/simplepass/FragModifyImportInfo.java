package com.nakedferret.simplepass;

import java.io.File;

import android.net.Uri;
import android.support.v4.app.ListFragment;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.UiThread;

@EFragment
public class FragModifyImportInfo extends ListFragment {

	@FragmentArg
	int nameColumn, usernameColumn, passwordColumn, categoryColumn;

	@FragmentArg
	Uri fileUri;

	@Bean
	CSVImporter importer;

	public FragModifyImportInfo() {
		// Required empty public constructor
	}

	@AfterViews
	void init() {
		processFile();
	}

	@Background
	void processFile() {
		importer.setFile(new File(fileUri.getPath()));
		importer.process();
		populateUI();
	}

	@UiThread
	void populateUI() {

	}

}
