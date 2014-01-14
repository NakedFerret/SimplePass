package com.nakedferret.simplepass.ui;

import android.app.Activity;
import android.widget.Button;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.nakedferret.simplepass.OverlayManager;
import com.nakedferret.simplepass.R;
import com.nakedferret.simplepass.Utils;

@EActivity(R.layout.act_main)
public class ActMain extends Activity {

	@ViewById
	Button overlayButton;

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
	protected void onStop() {
		super.onStop();
		finish(); // Finish the activity so the task gets destroyed
	}

}
