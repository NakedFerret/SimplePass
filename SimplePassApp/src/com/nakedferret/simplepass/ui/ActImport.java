package com.nakedferret.simplepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.FragImportPickFileType_;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ui.FragModifyImportInfo_.FragmentBuilder_;
import com.nakedferret.simplepass.utils.Utils;

@EActivity(R.layout.fragment_container)
public class ActImport extends FragmentActivity {

	public static final int REQUEST_PICK_FILE = 1;

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
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction t = fm.beginTransaction();
		t.replace(R.id.fragmentContainer, f);
		t.commitAllowingStateLoss();
	}

	public void showFragModifyImportInfo(int n, int u, int p, int c) {
		FragmentBuilder_ b = FragModifyImportInfo_.builder();
		b.nameColumn(n);
		b.usernameColumn(u);
		b.passwordColumn(p);
		b.categoryColumn(c);
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
		showFragMapColumnImport();
	}
}
