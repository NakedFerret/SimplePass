package com.nakedferret.simplepass.ui;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;

@EActivity(R.layout.act_import)
public class ActImport extends Activity implements OnItemSelectedListener {

	private static final int REQUEST_PICK_FILE = 1;

	@Bean
	CSVImporter importer;

	@ViewById
	TextView firstRow;

	@ViewById
	Spinner nameSpinner, usernameSpinner, passwordSpinner, categorySpinner;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.act_import, menu);
		return true;
	}

	@AfterViews
	void init() {
		launchFilePicker();
	}

	@Background
	void processFile(Uri fileUri) {
		Utils.log(this, "processing: " + fileUri.toString());
		importer.setFile(new File(fileUri.getPath()));
		importer.process();
		populateUI();
	}

	@UiThread
	void populateUI() {
		firstRow.setText(importer.getFirstRow());
		final String[] numArray = getArrayForAdapter();
		setAdapterForSpinner(numArray, nameSpinner);
		setAdapterForSpinner(numArray, usernameSpinner);
		setAdapterForSpinner(numArray, passwordSpinner);
		setAdapterForSpinner(numArray, categorySpinner);
	}

	private String[] getArrayForAdapter() {
		int numOfColumns = importer.getWidth();
		String[] numArray = new String[numOfColumns];

		for (int i = 1; i <= numOfColumns; i++)
			numArray[i - 1] = "Column " + Integer.toString(i);

		return numArray;
	}

	private void setAdapterForSpinner(String[] numArray, Spinner spinner) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, numArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	private void launchFilePicker() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("file/*");
		startActivityForResult(i, REQUEST_PICK_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_PICK_FILE:
			if (data != null)
				processFile(data.getData());
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
