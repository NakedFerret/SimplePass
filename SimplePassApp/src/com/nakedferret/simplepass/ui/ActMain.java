package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.ServiceOverlay;
import com.nakedferret.simplepass.ServiceOverlay_;
import com.nakedferret.simplepass.Utils;

@EActivity(R.layout.act_main)
public class ActMain extends Activity {

	@ViewById
	Button overlayButton;

	@AfterViews
	void init() {

		if (ServiceOverlay.overlayRunning())
			overlayButton.setText(R.string.stop_overlay);
		else
			overlayButton.setText(R.string.start_overlay);
	}

	@Click(R.id.overlayButton)
	void onOverlayButtonClicked() {

		if (ServiceOverlay.overlayRunning()) {
			ServiceOverlay.stopOverlay(this);
			overlayButton.setText(R.string.start_overlay);
		} else {
			ServiceOverlay.startOverlay(this);
			overlayButton.setText(R.string.stop_overlay);
		}

	}

	@Override
	protected void onPause() {
		finish(); // Finish the activity so the task gets destroyed
		super.onPause();
	}

}
