package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.OverlayManager;
import com.nakedferret.simplepass.R;

@EActivity(R.layout.act_main)
public class ActMain extends Activity {

	@ViewById
	Button overlayButton;

	@Bean
	CSVImporter importer;

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
			startActivity(new Intent(this, ActImport_.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
