package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.CSVImporter;
import com.nakedferret.simplepass.OverlayManager;
import com.nakedferret.simplepass.R;

@EActivity(R.layout.act_main)
@OptionsMenu(R.menu.act_main)
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

	@OptionsItem(R.id.action_import)
	void actionImport() {
		startActivity(new Intent(this, ActImport_.class));
	}

}
