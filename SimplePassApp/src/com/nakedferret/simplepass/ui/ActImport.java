package com.nakedferret.simplepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.CSVImporter.COLUMN_MAPPING;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ui.FragImportDesignateVaults_.FragmentBuilder_;
import com.nakedferret.simplepass.utils.Utils;

@EActivity(R.layout.fragment_container)
public class ActImport extends FragmentActivity {

	public static final int REQUEST_PICK_FILE = 1;

	@NonConfigurationInstance
	boolean configurationChanged = false, resultHandled = false;

	@NonConfigurationInstance
	Uri fileUri;

	@Override
	protected void onStart() {
		super.onStart();

		if (!configurationChanged)
			showFragImportPickFile();

		// showFragImportPickFileType() called here to avoid a bug with
		if (fileUri != null && !resultHandled) {
			showFragImportPickFileType();
			resultHandled = true;
		}
	}

	private void showFragImportPickFile() {
		Fragment f = new FragImportPickFile_();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.commit();
	}

	private void showFragMapColumnImport() {
		Fragment f = FragImportMapColumn_.builder().fileUri(fileUri).build();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commit();

		DialogFragment dialog = FragInstructionsDialog_.builder()
				.messageId(R.string.mapColumnsForImport).build();
		dialog.show(getSupportFragmentManager(), null);
	}

	public void showFragModifyImportInfo(int[] mapping) {
		FragmentBuilder_ b = FragImportDesignateVaults_.builder();
		b.nameColumn(mapping[COLUMN_MAPPING.NAME]);
		b.usernameColumn(mapping[COLUMN_MAPPING.USERNAME]);
		b.passwordColumn(mapping[COLUMN_MAPPING.PASSWORD]);
		b.categoryColumn(mapping[COLUMN_MAPPING.CATEGORY]);
		b.fileUri(fileUri);
		Fragment f = b.build();

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

	public void processFile(int fileType, int fileMapping) {
		Utils.log(this, "fileType: " + fileType);
		Utils.log(this, "fileMapping: " + fileMapping);

		if (fileMapping == CSVImporter.MAPPING.OTHER) {
			showFragMapColumnImport();
		} else if (fileMapping == CSVImporter.MAPPING.LASTPASS) {
			showFragModifyImportInfo(CSVImporter.LASTPASS_MAPPING);
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		configurationChanged = true;
		return super.onRetainCustomNonConfigurationInstance();
	}

}
