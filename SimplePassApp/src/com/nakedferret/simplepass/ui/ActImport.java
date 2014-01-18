package com.nakedferret.simplepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.res.IntArrayRes;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ui.FragModifyImportInfo_.FragmentBuilder_;
import com.nakedferret.simplepass.utils.Utils;

@EActivity(R.layout.fragment_container)
public class ActImport extends FragmentActivity {

	public static final int REQUEST_PICK_FILE = 1;

	private static final class COLUMN_MAPPING {
		private static final int NAME = 0;
		private static final int USERNAME = 1;
		private static final int PASSWORD = 2;
		private static final int CATEGORY = 3;
	}

	@IntArrayRes
	int[] lastpassMapping;

	private Uri fileUri;

	@AfterViews
	void init() {
		showFragImportPickFile();
	}

	private void showFragImportPickFile() {
		Fragment f = new FragImportPickFile_();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.commitAllowingStateLoss();
	}

	private void showFragMapColumnImport() {
		Fragment f = FragMapColumnImport_.builder().fileUri(fileUri).build();
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commitAllowingStateLoss();
	}

	public void showFragModifyImportInfo(int[] mapping) {
		FragmentBuilder_ b = FragModifyImportInfo_.builder();
		b.nameColumn(mapping[COLUMN_MAPPING.NAME]);
		b.usernameColumn(mapping[COLUMN_MAPPING.USERNAME]);
		b.passwordColumn(mapping[COLUMN_MAPPING.PASSWORD]);
		b.categoryColumn(mapping[COLUMN_MAPPING.CATEGORY]);
		b.fileUri(fileUri);
		Fragment f = b.build();

		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.addToBackStack(null);
		t.commitAllowingStateLoss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Utils.log(this, "onActivityResult: " + requestCode);
		switch (requestCode) {
		case REQUEST_PICK_FILE:
			if (data != null) {
				fileUri = data.getData();
				showFragImportPickFileType();
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
		t.commitAllowingStateLoss();
	}

	public void processFile(int fileType, int fileMapping) {
		Utils.log(this, "fileType: " + fileType);
		Utils.log(this, "fileMapping: " + fileMapping);

		if (fileMapping == CSVImporter.MAPPING.OTHER) {
			showFragMapColumnImport();
		} else if (fileMapping == CSVImporter.MAPPING.LASTPASS) {
			showFragModifyImportInfo(lastpassMapping);
		}
	}
}
