package com.nakedferret.simplepass.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.nakedferret.simplepass.FragModifyImportInfo_;
import com.nakedferret.simplepass.FragModifyImportInfo_.FragmentBuilder_;
import com.nakedferret.simplepass.R;

@EActivity(R.layout.fragment_container)
public class ActImport extends FragmentActivity {

	private static final int REQUEST_PICK_FILE = 1;

	private Uri fileUri;

	@AfterViews
	void init() {
		launchFilePicker();
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
			if (data != null) {
				fileUri = data.getData();
				showFragMapColumnImport();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
