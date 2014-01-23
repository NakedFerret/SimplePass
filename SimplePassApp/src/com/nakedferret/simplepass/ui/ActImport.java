package com.nakedferret.simplepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.googlecode.androidannotations.annotations.UiThread;
import com.nakedferret.simplepass.CSVImporter.CSVMapping;
import com.nakedferret.simplepass.ImportManager;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.utils.Utils;

@EActivity(R.layout.fragment_container)
public class ActImport extends FragmentActivity {

	public static final int REQUEST_PICK_FILE = 1;

	@NonConfigurationInstance
	boolean configurationChanged = false, resultHandled = false;

	@NonConfigurationInstance
	Uri fileUri;

	@NonConfigurationInstance
	@Bean
	ImportManager importManager;

	@AfterViews
	void init() {
		if (!configurationChanged)
			showFragImportPickFile();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// showFragImportPickFileType() called here to avoid a bug with
		if (fileUri != null && !resultHandled) {
			showFragImportPickFileType();
			resultHandled = true;
		}

		configurationChanged = false;
	}

	public void editImportAccount(int position) {
		Fragment f = FragImportEditAccount_.builder().selectedAccount(position)
				.build();

		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();
	}

	public void onAccountsSelected() {
		Fragment f = new FragImportModifyAndSave_();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();
	}

	private void showFragImportPickFile() {
		Fragment f = new FragImportPickFile_();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.commit();
	}

	@UiThread
	void showFragMapColumnImport() {
		Fragment f = FragImportMapColumn_.builder().fileUri(fileUri).build();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();

		DialogFragment dialog = FragInstructionsDialog_.builder()
				.messageId(R.string.mapColumnsForImport).build();
		dialog.show(getSupportFragmentManager(), null);
	}

	@UiThread
	public void showFragModifyImportInfo() {
		Fragment f = new FragImportDesignateVaults_();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();

		DialogFragment dialog = FragInstructionsDialog_.builder()
				.messageId(R.string.pickVaultForImport).build();
		dialog.show(getSupportFragmentManager(), null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Utils.log(this, "onActivityResult: " + requestCode);
		switch (requestCode) {
		case REQUEST_PICK_FILE:
			if (data != null) {
				fileUri = data.getData();
			}
			if (configurationChanged) {
				showFragImportPickFileType();
				resultHandled = true;
			} else {
				resultHandled = false;
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void showFragImportPickFileType() {
		Fragment f = new FragImportPickFileType_();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();
	}

	@Background
	public void processFile(int fileType, int fileMapping) {

		// Assuming the fileType is CSV...because it's the only one supported at
		// this time >.>
		CSVMapping mapping = CSVMapping.getMapping(fileMapping);

		if (mapping == null) {
			showFragMapColumnImport();
		} else {
			processCSV(mapping);
		}
	}

	@Background
	void processCSV(CSVMapping mapping) {
		importManager.processCSV(fileUri, mapping);
		showFragModifyImportInfo();
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		configurationChanged = true;
		return super.onRetainCustomNonConfigurationInstance();
	}

}
