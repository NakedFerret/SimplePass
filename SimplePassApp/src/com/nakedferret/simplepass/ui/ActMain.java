package com.nakedferret.simplepass.ui;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.OverlayManager;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;

@EActivity(R.layout.act_main)
public class ActMain extends Activity {

	@ViewById
	Button overlayButton;

	@Bean
	CSVImporter importer;

	private static final int PICK_FILE_REQUEST_CODE = 1;

	@AfterViews
	void init() {

		if (OverlayManager.overlayRunning())
			overlayButton.setText(R.string.stop_overlay);
		else
			overlayButton.setText(R.string.start_overlay);
	}

	@Click(R.id.overlayButton)
	void onOverlayButtonClicked() {

		if (OverlayManager.overlayRunning()) {
			OverlayManager.stopOverlay(this);
			overlayButton.setText(R.string.start_overlay);
		} else {
			OverlayManager.startOverlay(this);
			overlayButton.setText(R.string.stop_overlay);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.act_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_import:
			launchFilePicker();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void launchFilePicker() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("file/*");
		startActivityForResult(i, PICK_FILE_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null)
			checkFile(data.getData());

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Background
	void checkFile(Uri fileUri) {
		File file = new File(fileUri.getPath());
		importer.setFile(file);
		importer.process();
		Utils.log(this, "file is csv: " + importer.isValid());
		Utils.log(this, "Number of columns: " + importer.getWidth());
	}
}
