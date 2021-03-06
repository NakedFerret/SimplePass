package com.nakedferret.simplepass.ui;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.CSVImporter.CSVMapping;
import com.nakedferret.simplepass.ImportManager.FILE_TYPE;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.Utils;

@EFragment(R.layout.frag_import_map_column)
@OptionsMenu(R.menu.frag_map_column_import)
public class FragImportMapColumn extends Fragment implements
		OnItemSelectedListener {

	@FragmentArg
	Uri fileUri;

	@Bean
	CSVImporter importer;

	@ViewById
	TextView firstRow;

	@ViewById
	Spinner nameSpinner, usernameSpinner, passwordSpinner, categorySpinner;

	private int nameColumn, usernameColumn, passwordColumn, categoryColumn;
	private ActImport activity;

	public FragImportMapColumn() {

	}

	@Override
	public void onAttach(Activity activity) {
		this.activity = (ActImport) activity;
		super.onAttach(activity);
	}

	@AfterViews
	void init() {
		processFile(fileUri);
	}

	@Background
	void processFile(Uri fileUri) {
		Utils.log(this, "processing: " + fileUri.toString());
		importer.prepare(new File(fileUri.getPath()), null);
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
		String[] numArray = new String[numOfColumns + 1];

		numArray[0] = "None";

		for (int i = 1; i <= numOfColumns; i++)
			numArray[i] = "Column " + Integer.toString(i);

		return numArray;
	}

	private void setAdapterForSpinner(String[] numArray, Spinner spinner) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, numArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int p, long id) {

		int columnNum = (int) id - 1;

		if (adapter.getId() == nameSpinner.getId())
			nameColumn = columnNum;
		else if (adapter.getId() == usernameSpinner.getId())
			usernameColumn = columnNum;
		else if (adapter.getId() == passwordSpinner.getId())
			passwordColumn = columnNum;
		else if (adapter.getId() == categorySpinner.getId())
			categoryColumn = columnNum;

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@OptionsItem(R.id.action_done)
	void actionDone() {
		CSVMapping newMapping = new CSVMapping(nameColumn, usernameColumn,
				passwordColumn, categoryColumn);
		int id = CSVMapping.addMapping(newMapping);
		activity.processFile(FILE_TYPE.CSV, id);
	}

}
